package br.edu.uaifood.orders.repository.payment.entity

import Payment
import PaymentStatus
import br.edu.uaifood.orders.repository.order.entity.OrderEntity
import jakarta.persistence.*

@Entity(name = "payment")
data class PaymentEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    val paymentId: String?, // ID gerado pelo sistema de pagamento (e.g., Mercado Pago)
    @Enumerated(EnumType.STRING)
    var status: PaymentStatus, // Status do pagamento (e.g., PENDING, APPROVED)
    var qrCode: String?, // Método de pagamento (e.g., CREDIT_CARD, PIX)
    val amount: Double, // Valor do pagamento
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    var order: OrderEntity? = null // Relacionamento com o pedido
){
    companion object {
        fun from(payment: Payment): PaymentEntity {
            return PaymentEntity(
                id = null,
                status = payment.status,
                qrCode = payment.qrCode,
                amount = payment.amount,
                paymentId = payment.paymentId,
                order = null
            )
        }
    }
}