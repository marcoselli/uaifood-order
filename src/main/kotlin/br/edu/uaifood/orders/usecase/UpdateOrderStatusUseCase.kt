package br.edu.uaifood.orders.usecase


import br.edu.uaifood.orders.domain.Order
import br.edu.uaifood.orders.exception.OrderNotFoundException
import br.edu.uaifood.orders.repository.order.OrderRepository
import br.edu.uaifood.orders.repository.order.entity.OrderEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class UpdateOrderStatusUseCase(
    private val repository: OrderRepository
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun execute(orderId: Long) {
        runCatching {
            val orderPersisted = repository.findById(orderId).getOrNull() ?: throw OrderNotFoundException(orderId)
            val order = Order.from(orderPersisted)
            order.nextStatus()
            repository.save(OrderEntity.from(order, orderPersisted.id))
        }.onSuccess {
            logger.info("Order id $orderId status updated successfully")
        }.onFailure {

            logger.error("Fail to update order $orderId status - ${it.message}")
            throw it
        }
    }
}