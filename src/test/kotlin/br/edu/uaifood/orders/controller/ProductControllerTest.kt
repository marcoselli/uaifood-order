package br.edu.uaifood.orders.controller


import br.edu.uaifood.orders.domain.Product
import br.edu.uaifood.orders.repository.product.ProductRepository
import br.edu.uaifood.orders.repository.product.entity.ProductEntity
import br.edu.uaifood.orders.util.JsonReader
import com.ninjasquad.springmockk.MockkBean
import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith(RandomBeansExtension::class)
class ProductControllerTest(
    @Autowired
    private val jsonReader: JsonReader,
    @Autowired
    private val mockMvc: MockMvc
) {
    @MockkBean
    private lateinit var productRepository: ProductRepository

    @Test
    fun `should insert product into menu successfully`() {
        // Given
        val productRequest = jsonReader.import("product_request_ok.json")
        val productEntity = ProductEntity.from(
            jsonReader.importClass("product_request_ok.json", Product::class.java)
        )
        // When
        every { productRepository.findByName(any()) } returns null
        every { productRepository.save(any()) } returns productEntity
        mockMvc.perform(
            post("/v1/products")
                .content(productRequest)
                .contentType(MediaType.APPLICATION_JSON)
        )
        // Then
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("Coke"))
            .andExpect(jsonPath("$.description").value("Coca-cola 350ml"))
            .andExpect(jsonPath("$.price").value(10.00))
            .andExpect(jsonPath("$.category").value("DRINK"))
            .andExpect(jsonPath("$.image_url").value("base-img-url.com"))
    }

    @Test
    fun `should update product from menu successfully`(@Random oldProduct: ProductEntity) {
        // Given
        val productRequest = jsonReader.import("product_request_ok.json")
        // When
        every { productRepository.findByName(any()) } returns oldProduct.copy(name = "Coke")
        val product = jsonReader.importClass("product_request_ok.json", Product::class.java)
        val newProductEntity = ProductEntity.from(product).copy(id = oldProduct.id)
        every { productRepository.save(any()) } returns newProductEntity
        mockMvc.perform(
            put("/v1/products/Coke")
                .content(productRequest)
                .contentType(MediaType.APPLICATION_JSON)
        )
            // Then
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("Coke"))
            .andExpect(jsonPath("$.description").value("Coca-cola 350ml"))
            .andExpect(jsonPath("$.price").value(10.00))
            .andExpect(jsonPath("$.category").value("DRINK"))
            .andExpect(jsonPath("$.image_url").value("base-img-url.com"))
    }

    //@Test
    fun `should throw an error when update and query param and request body names are different`(
        @Random oldProduct: ProductEntity
    ) {
        // Given
        val productRequest = jsonReader.import("product_request_ok.json")
        // When
        mockMvc.perform(
            put("/v1/products/ANY_OTHER_NAME_HERE")
                .content(productRequest)
                .contentType(MediaType.APPLICATION_JSON)
        )
            // Then
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status_code").value(400))
            .andExpect(jsonPath("$.message").value("Path parameter and request body names must be equal"))
    }

    @Test
    fun `should remove product from menu successfully`(@Random ProductEntity: ProductEntity) {
        // Given
        every { productRepository.findByName(any()) } returns ProductEntity
        every { productRepository.deleteById(any()) } just Runs
        // When
        mockMvc.perform(delete("/v1/products/ANY_PRODUCT_NAME_HERE"))
        // Then
            .andExpect(status().isNoContent)
    }

    @Test
    fun `should return a list of products given a category name`() {
        // Given
        val category = "SNACK"
        val returnProducts = List(10){
            ProductEntity(
                id = UUID.randomUUID(),
                name = "Chips",
                category = category,
                price = 1.99,
                description = "Crocante e saboroso",
                imageUrl = "http://example.com/chips.png"
            )}

        every { productRepository.findByCategory(category)} returns returnProducts

        // When
        mockMvc.perform(
            get("/v1/products?category=$category")
                .contentType(MediaType.APPLICATION_JSON)
        )
            // Then
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[1].category").value("SNACK"))
    }

    @Test
    fun `when returning an empty list of products it should return a 204 status code`() {
        // Given
        every { productRepository.findByCategory(any())} returns emptyList()

        // When
        mockMvc.perform(
            get("/v1/products?category=SNACK")
                .contentType(MediaType.APPLICATION_JSON)
        )
            // Then
            .andExpect(status().isNoContent)
    }

}