import br.edu.uaifood.orders.domain.Order
import br.edu.uaifood.orders.repository.payment.entity.PaymentEntity

data class Payment(
    val order: Order?,
    val status: PaymentStatus,
    val paymentId: String?,
    val amount: Double,
    val qrCode: String?
) {
    fun isApproved() = status == PaymentStatus.APPROVED

    companion object {
        fun from(paymentPersisted: PaymentEntity): Payment =
            Payment(
                order = if (paymentPersisted.order != null ) Order.from(paymentPersisted.order!!) else null,
                status = paymentPersisted.status,
                paymentId = paymentPersisted.paymentId,
                amount = paymentPersisted.amount,
                qrCode = paymentPersisted.qrCode
            )
    }
}

enum class PaymentStatus {
    PENDING,
    APPROVED,
    DECLINED,
    CANCELLED
}