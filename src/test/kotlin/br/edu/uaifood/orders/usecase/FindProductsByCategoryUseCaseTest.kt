package br.edu.uaifood.orders.usecase

import br.edu.uaifood.orders.controller.product.dto.ProductResponse
import br.edu.uaifood.orders.repository.product.ProductRepository
import br.edu.uaifood.orders.repository.product.entity.ProductEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertTrue

class FindProductsByCategoryUseCaseTest {

    private val productRepository: ProductRepository = mockk()
    private val findProductsByCategoryUseCaseImpl = FindProductsByCategoryUseCase(productRepository)

    @Test
    fun `should find a list of products given a category name`() {
        // Given
        val category = "SNACK"
        val returnProducts = List(10) {
            ProductEntity(
                id = UUID.randomUUID(),
                name = "Chips",
                category = category,
                price = 1.99,
                description = "Crocante e saboroso",
                imageUrl = "http://example.com/chips.png"
            )
        }

        every { productRepository.findByCategory(category)} returns returnProducts

        // When
        val result: List<ProductResponse> = findProductsByCategoryUseCaseImpl.execute(category)

        // Then
        assertTrue(result.all { it.category == category })
        verify(exactly = 1) { productRepository.findByCategory(category) }
    }

}