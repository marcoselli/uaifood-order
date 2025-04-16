package br.edu.uaifood.orders.usecase

import br.edu.uaifood.orders.controller.product.dto.ProductResponse
import br.edu.uaifood.orders.domain.Product
import br.edu.uaifood.orders.repository.product.ProductRepository
import br.edu.uaifood.orders.repository.product.entity.ProductEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class InsertProductIntoMenuUseCase(
    private val productRepository: ProductRepository
){

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(product: Product): ProductResponse {
        return runCatching {
            productRepository.findByName(product.name)
                ?.let { Product.from(it) }
                ?.also { alreadySavedProduct -> alreadySavedProduct.ensureUniqueness(product) }

            productRepository.save(ProductEntity.from(product))
                .let { ProductResponse.from(it) }
        }.onSuccess { logger.info("Product ${product.name} inserted into menu successfully")
        }.onFailure {
            logger.info("Fail to insert product ${product.name}: ${it.message}")
            throw it
        }.getOrThrow()
    }
}