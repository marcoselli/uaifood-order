package br.edu.uaifood.orders.controller.product.dto

import br.edu.uaifood.orders.domain.Product
import br.edu.uaifood.orders.repository.product.entity.ProductEntity
import java.util.*

data class ProductResponse(
    val id: UUID? = null,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String
) {
    companion object {
        fun from(productPersisted: ProductEntity): ProductResponse =
            ProductResponse(
                id = productPersisted.id,
                name = productPersisted.name,
                description = productPersisted.description,
                price = productPersisted.price,
                category = productPersisted.category,
                imageUrl = productPersisted.imageUrl
            )

        fun from(product: Product): ProductResponse =
            ProductResponse(
                name = product.name,
                description = product.description,
                price = product.price,
                category = product.category.name,
                imageUrl = product.imageUrl
            )

    }
}
