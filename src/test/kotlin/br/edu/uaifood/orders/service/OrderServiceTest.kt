package br.edu.uaifood.orders.service

import br.edu.uaifood.orders.controller.order.dto.OrderResponse
import br.edu.uaifood.orders.domain.Order
import br.edu.uaifood.orders.domain.OrderStatus
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals


class OrderServiceTest {

    private val orderService = OrderService()

    @Test
    fun `should sort orders and filter status FINISHED`() {
        // given
        val order1 = Order(
            products = emptyList(),
            status = OrderStatus.FINISHED,
            creationDate = LocalDateTime.now(),
            customerCpf = null
        )

        val order2 = Order(
            products = emptyList(),
            status = OrderStatus.READY ,
            creationDate = LocalDateTime.now(),
            customerCpf = null
        )

        val order3 = Order(
            products = emptyList(),
            status = OrderStatus.IN_PREPARATION,
            creationDate = LocalDateTime.now().minusMinutes(1),
            customerCpf = null
        )

        val order4 = Order(
            products = emptyList(),
            status = OrderStatus.RECEIVED,
            creationDate = LocalDateTime.now(),
            customerCpf = null
        )

        val order5 = Order(
            products = emptyList(),
            status = OrderStatus.IN_PREPARATION,
            creationDate = LocalDateTime.now(),
            customerCpf = null
        )

        val expected = listOf(
            OrderResponse.from(order2),
            OrderResponse.from(order3),
            OrderResponse.from(order5),
            OrderResponse.from(order4)
        )
        // when
        val result = orderService.retrieveOrdersSortedByPriority(listOf(order1, order2, order3, order4, order5))
        // then
        assertEquals(expected, result)
    }
}