package br.edu.uaifood.orders.usecase

import br.edu.uaifood.orders.controller.product.dto.ProductResponse
import br.edu.uaifood.orders.domain.Product
import br.edu.uaifood.orders.exception.ProductNotFoundException
import br.edu.uaifood.orders.repository.product.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UpdateMenuProductUseCase(
    private val productRepository: ProductRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(productName: String, updatedProduct: Product): ProductResponse {
        return runCatching {
            val oldProduct = productRepository.findByName(productName)
                ?: throw ProductNotFoundException("Product $productName not found")
            productRepository.save(br.edu.uaifood.orders.repository.product.entity.ProductEntity.from(oldProduct.id, updatedProduct))
                .let { ProductResponse.from(it) }
        }.onSuccess { logger.info("Product $productName updated from menu successfully")
        }.onFailure {
            logger.info("Fail to update product $productName: ${it.message}")
            throw it
        }.getOrThrow()
    }
}