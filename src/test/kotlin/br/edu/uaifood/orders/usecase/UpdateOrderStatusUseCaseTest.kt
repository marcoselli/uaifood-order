package br.edu.uaifood.orders.usecase

import br.edu.uaifood.orders.domain.OrderStatus
import br.edu.uaifood.orders.exception.OrderAlreadyFinishedException
import br.edu.uaifood.orders.exception.OrderNotFoundException
import br.edu.uaifood.orders.repository.order.OrderRepository
import br.edu.uaifood.orders.repository.order.entity.OrderEntity
import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(RandomBeansExtension::class)
class UpdateOrderStatusUseCaseTest {

    private val orderRepository: OrderRepository = mockk()
    private val updateOrderStatusUseCaseImpl = UpdateOrderStatusUseCase(orderRepository)

    @Test
    fun `should update order successfully`(@Random randomOrderPersisted: OrderEntity) {
        // given
        val orderId = 123L
        val orderPersisted = randomOrderPersisted.copy(
            id = orderId,
            status = OrderStatus.RECEIVED,
            products = listOf(randomOrderPersisted.products.first().copy(category = "DRINK"))
        )
        every { orderRepository.findById(any()) } returns Optional.of(orderPersisted)
        every { orderRepository.save(any()) } returns orderPersisted
        // when
        updateOrderStatusUseCaseImpl.execute(orderId)
        // then
        verify(exactly = 1) {
            orderRepository.findById(any())
            orderRepository.save(any())
        }
    }

    @Test
    fun `should throw an error when status is already FINISHED`(@Random randomOrderPersisted: OrderEntity) {
        // given
        val orderId = 123L
        val orderPersisted = randomOrderPersisted.copy(
            id = orderId,
            status = OrderStatus.FINISHED,
            products = listOf(randomOrderPersisted.products.first().copy(category = "DRINK"))
        )
        every { orderRepository.findById(any()) } returns Optional.of(orderPersisted)
        // when
        val error = assertThrows<OrderAlreadyFinishedException> {
            updateOrderStatusUseCaseImpl.execute(orderId)
        }
        // then
        assertEquals( "Order is already finished", error.body.detail)
        verify(exactly = 1) {
            orderRepository.findById(any())
        }
    }

    @Test
    fun `should throw an error when order not found`() {
        // given
        val orderId = 123L
        every { orderRepository.findById(any()) } returns Optional.empty()
        // when
        val error = assertThrows<OrderNotFoundException> {
            updateOrderStatusUseCaseImpl.execute(orderId)
        }
        // then
        assertEquals( "Order id $orderId is invalid", error.body.detail)
        verify(exactly = 1) {
            orderRepository.findById(any())
        }
    }
}