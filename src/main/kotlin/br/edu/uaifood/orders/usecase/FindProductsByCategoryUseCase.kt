package br.edu.uaifood.orders.usecase

import br.edu.uaifood.orders.controller.product.dto.ProductResponse
import br.edu.uaifood.orders.repository.product.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FindProductsByCategoryUseCase(
    private val productRepository: ProductRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(category: String): List<ProductResponse> {
        logger.info("Getting products by category $category")
        val products = productRepository.findByCategory(category)
        return products.map { product -> ProductResponse.from(product) }
    }
}
