package br.edu.uaifood.orders.usecase

import br.edu.uaifood.orders.domain.Product
import br.edu.uaifood.orders.repository.product.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FindProductsByIdsUseCase(
    var productRepository: ProductRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(products: List<Product>) =
        logger.info("Creating order").run {
            products.map { product ->
                productRepository.findById(product.id ?: throw IllegalArgumentException("Product ID is missing"))
                    .orElseThrow { IllegalArgumentException("Product not found: ${product.id}") }
            }
        }
}
