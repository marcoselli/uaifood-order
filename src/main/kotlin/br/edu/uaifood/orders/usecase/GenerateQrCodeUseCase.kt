package br.edu.uaifood.orders.usecase

import br.edu.uaifood.orders.repository.order.entity.OrderEntity
import org.springframework.stereotype.Component

@Component
class GenerateQrCodeUseCase {
     fun execute(orderPersisted: OrderEntity): String = "qrFake"
}