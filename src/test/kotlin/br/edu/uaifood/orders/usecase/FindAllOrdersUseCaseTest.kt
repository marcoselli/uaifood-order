package br.edu.uaifood.orders.usecase


import br.edu.uaifood.orders.domain.OrderStatus
import br.edu.uaifood.orders.repository.order.OrderRepository
import br.edu.uaifood.orders.repository.order.entity.OrderEntity
import br.edu.uaifood.orders.repository.product.entity.ProductEntity
import br.edu.uaifood.orders.service.OrderService
import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime.parse
import kotlin.test.Test

@ExtendWith(RandomBeansExtension::class)
class FindAllOrdersUseCaseTest {
    private val orderRepository: OrderRepository = mockk()
    private val findAllOrdersUseCaseImpl = FindAllOrdersUseCase(orderRepository, OrderService())

    @Test
    fun `should find all orders`(
        @Random firstRandomProduct: ProductEntity,
        @Random secondRandomProduct: ProductEntity
    ) {
        //given
        val firstProduct = firstRandomProduct.copy(category = "DRINK")
        val secondProduct = secondRandomProduct.copy(category = "DESSERT")
        val firstOrder = OrderEntity(1, listOf(firstProduct),
            OrderStatus.RECEIVED, parse("2023-06-20T19:34:50.63"), null)
        val secondOrder = OrderEntity(2, listOf(firstProduct, secondProduct),
            OrderStatus.IN_PREPARATION, parse("2023-12-23T07:12:10.02"), "910.933.630-37")

        every { orderRepository.findAll() } returns listOf(firstOrder, secondOrder)

        //when
        val orders = findAllOrdersUseCaseImpl.execute()

        //then
        assertThat(orders[0].status).isEqualTo(OrderStatus.IN_PREPARATION)
        assertThat(orders[0].products.size).isEqualTo(2)
        assertThat(orders[0].products[0].name).isEqualTo(firstRandomProduct.name)
        assertThat(orders[0].products[1].name).isEqualTo(secondRandomProduct.name)
        assertThat(orders[1].status).isEqualTo(OrderStatus.RECEIVED)
        assertThat(orders[1].products.size).isEqualTo(1)
        assertThat(orders[1].products[0].name).isEqualTo(firstRandomProduct.name)
    }
}