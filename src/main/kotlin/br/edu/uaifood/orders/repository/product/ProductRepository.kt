package br.edu.uaifood.orders.repository.product

import br.edu.uaifood.orders.repository.product.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductRepository: JpaRepository<ProductEntity, UUID> {
    fun findByName(name: String): ProductEntity?
    fun findByCategory(category: String): List<ProductEntity>
}