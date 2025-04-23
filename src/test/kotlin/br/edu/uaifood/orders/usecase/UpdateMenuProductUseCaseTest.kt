package br.edu.uaifood.orders.usecase

import br.edu.uaifood.orders.controller.product.dto.ProductResponse
import br.edu.uaifood.orders.domain.Product
import br.edu.uaifood.orders.exception.ProductNotFoundException
import br.edu.uaifood.orders.repository.product.ProductRepository
import br.edu.uaifood.orders.repository.product.entity.ProductEntity
import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(RandomBeansExtension::class)
class UpdateMenuProductUseCaseTest {

    private val productRepository: ProductRepository = mockk()
    private val updateMenuProductUseCaseImpl = UpdateMenuProductUseCase(productRepository)

    @Test
    fun `should update a product from menu successfully`(
        @Random oldProduct: ProductEntity,
        @Random randomProduct: Product
    ) {
        // Given
        every { productRepository.findByName(any()) } returns oldProduct
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
        val result = updateMenuProductUseCaseImpl.execute(randomProduct.name, randomProduct)
        // Then
        assertEquals(expected.name, result.name)
        assertEquals(expected.description, result.description)
        assertEquals(expected.price, result.price)
        assertEquals(expected.category, result.category)
    }

    @Test
    fun `should throw an error when update and product name dont exist`(
        @Random randomProduct: Product
    ) {
        // Given
        every { productRepository.findByName(any()) } returns null
        // When - Then
        val error = assertThrows<ProductNotFoundException> {
            updateMenuProductUseCaseImpl.execute(randomProduct.name, randomProduct)
        }
        assertEquals("Product ${randomProduct.name} not found", error.reason)
        verify(exactly = 1) { productRepository.findByName(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
    }
}