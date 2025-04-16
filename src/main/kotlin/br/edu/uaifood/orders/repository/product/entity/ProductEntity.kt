package br.edu.uaifood.orders.repository.product.entity

import br.edu.uaifood.orders.domain.Product
import br.edu.uaifood.orders.repository.order.entity.OrderEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import org.hibernate.annotations.JdbcTypeCode
import java.sql.Types
import java.util.*

@Entity(name = "product")
data class ProductEntity(
    @Id
    @GeneratedValue
    @JdbcTypeCode(Types.VARCHAR)
    val id: UUID = UUID.randomUUID(), //Its overriden by the database, but ensures current 'non-nullable' logic
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String,
    @ManyToMany(mappedBy = "products")
    val orders: List<OrderEntity> = emptyList()
) {
    companion object {
        fun from(newProduct: Product): ProductEntity =
            ProductEntity(
                name = newProduct.name,
                description = newProduct.description,
                price = newProduct.price,
                category = newProduct.category.name,
                imageUrl = newProduct.imageUrl
            )

        fun from(existingId: UUID, updatedProduct: Product): ProductEntity =
            ProductEntity(
                id = existingId,
                name = updatedProduct.name,
                description = updatedProduct.description,
                price = updatedProduct.price,
                category = updatedProduct.category.name,
                imageUrl = updatedProduct.imageUrl
            )
    }
}