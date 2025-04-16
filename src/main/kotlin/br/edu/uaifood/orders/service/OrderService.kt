package br.edu.uaifood.orders.service


import br.edu.uaifood.orders.controller.order.dto.OrderResponse
import br.edu.uaifood.orders.domain.Order
import br.edu.uaifood.orders.domain.OrderStatus
import org.springframework.stereotype.Service

@Service
class OrderService {
    fun retrieveOrdersSortedByPriority(orders: List<Order>): List<OrderResponse> {
         return orders
            .filterNot { it.status == OrderStatus.FINISHED}
            .sortedWith(compareBy<Order> {it.status.priority}.thenBy { it.creationDate } )
            .map { OrderResponse.from(it) }
    }
}