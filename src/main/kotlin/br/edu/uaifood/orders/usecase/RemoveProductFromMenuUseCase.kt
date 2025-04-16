package br.edu.uaifood.orders.usecase

import br.edu.uaifood.orders.repository.product.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class RemoveProductFromMenuUseCase(
    private val productRepository: ProductRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(productName: String) {
        productRepository.findByName(productName)
            ?.let { productRepository.deleteById(it.id) }
            ?.also { logger.info("Product $productName removed from menu") }
    }
}
