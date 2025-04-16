package br.edu.uaifood.orders.usecase

import br.edu.uaifood.orders.controller.order.dto.OrderResponse
import br.edu.uaifood.orders.domain.Order
import br.edu.uaifood.orders.repository.order.OrderRepository
import br.edu.uaifood.orders.service.OrderService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FindAllOrdersUseCase(
    var repository: OrderRepository,
    var service: OrderService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(): List<OrderResponse> {
        logger.info("Getting order list")
        return runCatching {
            val allOrders = repository.findAll().map { Order.from(it) }
            val allOrdersSorted = service.retrieveOrdersSortedByPriority(allOrders)
            allOrdersSorted
        }.onFailure {
            logger.info("Fail to find orders - ${it.message}")
            throw it
        }.getOrThrow()
    }
}