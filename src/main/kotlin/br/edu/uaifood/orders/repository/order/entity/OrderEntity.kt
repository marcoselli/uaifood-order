package br.edu.uaifood.orders.repository.order.entity


import br.edu.uaifood.orders.domain.Order
import br.edu.uaifood.orders.domain.OrderStatus
import br.edu.uaifood.orders.repository.payment.entity.PaymentEntity
import br.edu.uaifood.orders.repository.product.entity.ProductEntity
import jakarta.persistence.*
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.GenerationType.IDENTITY
import java.time.LocalDateTime

@Entity(name = "food_order")
data class OrderEntity(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long?,
    @ManyToMany(cascade = [CascadeType.MERGE])
    @JoinTable(
        name = "food_order_product",
        joinColumns = [JoinColumn(name = "food_order_id")],
        inverseJoinColumns = [JoinColumn(name = "product_id")]
    )
    var products: List<ProductEntity> = emptyList(),
    @Enumerated(STRING)
    var status: OrderStatus,
    var creationDate: LocalDateTime,
    var customerCPF: String?,
    @OneToOne(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    var payment: PaymentEntity? = null // Relacionamento one-to-one com pagamento
) {
    companion object {
        fun from(order: Order, orderId: Long? = null): OrderEntity {
            return OrderEntity(
                id = orderId,
                status = order.status,
                products = order.products.map { ProductEntity.from(it) },
                creationDate = order.creationDate,
                customerCPF = order.customerCpf
            )
        }
    }
}
