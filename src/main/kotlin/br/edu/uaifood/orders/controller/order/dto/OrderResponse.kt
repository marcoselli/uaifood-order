package br.edu.uaifood.orders.controller.order.dto

import br.edu.uaifood.orders.controller.product.dto.ProductResponse
import br.edu.uaifood.orders.domain.Order
import br.edu.uaifood.orders.domain.OrderStatus
import br.edu.uaifood.orders.repository.order.entity.OrderEntity


data class OrderResponse(
    var orderId: Long? = null,
    var products: List<ProductResponse> = emptyList(),
    var status: OrderStatus,
    var creationDate: String
) {
    companion object {
        fun from(orderPersisted: OrderEntity): OrderResponse =
            OrderResponse(
                orderId = orderPersisted.id,
                products = orderPersisted.products.map { ProductResponse.from(it) },
                status = orderPersisted.status,
                creationDate = orderPersisted.creationDate.toString()
            )

        fun from(order: Order): OrderResponse =
            OrderResponse(
                products = order.products.map { ProductResponse.from(it) },
                status = order.status,
                creationDate = order.creationDate.toString()
            )
    }
}
