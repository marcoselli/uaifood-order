package br.edu.uaifood.orders.domain

import br.edu.uaifood.orders.controller.product.dto.UpsertProductRequest
import br.edu.uaifood.orders.exception.ProductValidationException
import br.edu.uaifood.orders.repository.product.entity.ProductEntity
import java.util.*

data class Product(
    val id: UUID? = null,
    val name: String,
    val description: String,
    val price: Double,
    val category: ProductCategory,
    val imageUrl: String
) {
    companion object {
        fun from(upsertProductRequest: UpsertProductRequest) =
            Product(
                id = upsertProductRequest.id,
                name = upsertProductRequest.name,
                description = upsertProductRequest.description,
                price = validatePrice(upsertProductRequest.price),
                category = validateCategory(upsertProductRequest.category),
                imageUrl = upsertProductRequest.imageUrl
            )

        fun from(productPersisted: ProductEntity) =
            Product(
                name = productPersisted.name,
                description = productPersisted.description,
                price = validatePrice(productPersisted.price),
                category = validateCategory(productPersisted.category),
                imageUrl = productPersisted.imageUrl

            )

        private fun validatePrice(price: Double) =
            if (price > 0) price else throw ProductValidationException("Product price should be greater than zero")

        private fun validateCategory(productCategory: String) =
            when (productCategory) {
                "SNACK" -> ProductCategory.SNACK
                "SIDE_DISH" -> ProductCategory.SIDE_DISH
                "DRINK" -> ProductCategory.DRINK
                "DESSERT" -> ProductCategory.DESSERT
                else -> throw ProductValidationException("Unknown product category")
            }
    }

    fun ensureUniqueness(product: Product) {
        if ((this.name == product.name) and (this.category == product.category))
            throw ProductValidationException("Product ${this.name} already exists")
        if ((this.name == product.name) and (this.category != product.category))
            throw ProductValidationException("Product ${this.name} already exists in another category")
    }

}

enum class ProductCategory {
    SNACK,  SIDE_DISH, DRINK, DESSERT,
}
