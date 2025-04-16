package br.edu.uaifood.orders.repository.payment

import br.edu.uaifood.orders.repository.payment.entity.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository : JpaRepository<PaymentEntity, Long> {
    fun findByPaymentId(paymentId: String): PaymentEntity?
}
