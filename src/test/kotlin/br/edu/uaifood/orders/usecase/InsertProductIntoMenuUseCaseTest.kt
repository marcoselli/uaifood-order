package br.edu.uaifood.orders.usecase

import br.edu.uaifood.orders.controller.product.dto.ProductResponse
import br.edu.uaifood.orders.domain.Product
import br.edu.uaifood.orders.repository.product.ProductRepository
import br.edu.uaifood.orders.repository.product.entity.ProductEntity
import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(RandomBeansExtension::class)
class InsertProductIntoMenuUseCaseTest {

    private val productRepository: ProductRepository = mockk()
    private val insertProductMenuUseCaseImpl = InsertProductIntoMenuUseCase(productRepository)

    @Test
    fun `should insert a product into menu successfully`(
        @Random randomProduct: Product
    ) {
        // Given
        every { productRepository.findByName(any()) } returns null
        val productPersisted = ProductEntity.from(randomProduct)
        every { productRepository.save(any()) } returns productPersisted
        val expected = ProductResponse(
            name = randomProduct.name,
            description = randomProduct.description,
            price = randomProduct.price,
            category = randomProduct.category.name,
            imageUrl = randomProduct.imageUrl,
        )
        // When
        val result = insertProductMenuUseCaseImpl.execute(randomProduct)
        // Then
        assertEquals(expected.name, result.name)
        assertEquals(expected.description, result.description)
        assertEquals(expected.price, result.price)
        assertEquals(expected.category, result.category)
    }
}