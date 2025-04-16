package br.edu.uaifood.orders.usecase

import Payment
import br.edu.uaifood.orders.controller.order.dto.OrderResponse
import br.edu.uaifood.orders.domain.Order
import br.edu.uaifood.orders.repository.order.OrderRepository
import br.edu.uaifood.orders.repository.order.entity.OrderEntity
import br.edu.uaifood.orders.repository.payment.PaymentRepository
import br.edu.uaifood.orders.repository.payment.entity.PaymentEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CreateOrderUseCase(
    var orderRepository: OrderRepository,
    var findProductsByIdsUseCase: FindProductsByIdsUseCase,
    var paymentRepository: PaymentRepository,
    val generateQrCodeUseCase: GenerateQrCodeUseCase
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(order: Order): OrderResponse {
        logger.info("Creating order")
        val managedProducts = findProductsByIdsUseCase.execute(order.products)
        val orderPersisted = OrderEntity.from(order).apply { products = managedProducts }
        val payment = Payment(
            order = Order.from(orderPersisted),
            status = PaymentStatus.PENDING,
            paymentId = order.id.toString(),
            amount =  order.products.sumOf { product -> product.price } ,
            qrCode = generateQrCodeUseCase.execute(orderPersisted)
        )
        return runCatching {
            orderPersisted.payment = PaymentEntity.from(payment)
            orderRepository.save(orderPersisted)
            paymentRepository.save(PaymentEntity.from(payment))
            OrderResponse.from(orderPersisted)
        }.onSuccess { logger.info("Order created successfully")
        }.onFailure {
            logger.info("Fail to create order: ${it.message}")
            throw  it
        }.getOrThrow()
    }
}