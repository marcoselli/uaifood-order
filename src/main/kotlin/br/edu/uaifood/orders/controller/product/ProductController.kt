package br.edu.uaifood.orders.controller.product


import br.edu.uaifood.orders.controller.product.dto.ProductResponse
import br.edu.uaifood.orders.controller.product.dto.UpsertProductRequest
import br.edu.uaifood.orders.domain.Product
import br.edu.uaifood.orders.exception.ErrorMessageModel
import br.edu.uaifood.orders.usecase.InsertProductIntoMenuUseCase
import br.edu.uaifood.orders.usecase.RemoveProductFromMenuUseCase
import br.edu.uaifood.orders.usecase.UpdateMenuProductUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/products")
class ProductController(
    private val insertProductIntoMenuUseCase: InsertProductIntoMenuUseCase,
    private val updateMenuProductUseCase: UpdateMenuProductUseCase,
    private val removeProductFromMenuUseCase: RemoveProductFromMenuUseCase,
    private val findProductsByCategoryUseCase: br.edu.uaifood.orders.usecase.FindProductsByCategoryUseCase
) {

    private val logger = LoggerFactory.getLogger(ProductController::class.java)

    @Operation(summary = "Insert new product into menu", description = "Returns 201 if successful")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Product inserted successfully", content = [Content(schema = Schema(implementation = ProductResponse::class))]),
            ApiResponse(responseCode = "400", description = "Error inserting Product", content = [Content(schema = Schema(implementation = ErrorMessageModel::class))]),
        ]
    )
    @PostMapping
    fun insertIntoMenu(@RequestBody upsertProductRequest: UpsertProductRequest) =
        logger.info("Inserting product ${upsertProductRequest.name} into menu")
            .let { insertProductIntoMenuUseCase.execute(Product.from(upsertProductRequest)) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @Operation(summary = "Update product from menu", description = "Returns 200 if successful")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Product updated successfully", content = [Content(schema = Schema(implementation = ProductResponse::class))]),
            ApiResponse(responseCode = "400", description = "Error updating Product", content = [Content(schema = Schema(implementation = ErrorMessageModel::class))]),
        ]
    )
    @PutMapping("/{product_name}")
    fun updateMenuProduct(@RequestBody upsertProductRequest: UpsertProductRequest,
                          @PathVariable("product_name") productName: String): ResponseEntity<ProductResponse> {
        logger.info("Updating product $productName from menu")
        if (productName != upsertProductRequest.name) {
            logger.info("Fail to update product $productName - Path parameter and request body names must be equal")
            throw br.edu.uaifood.orders.exception.InvalidUpdateRequestException()
        }
        return updateMenuProductUseCase.execute(productName, Product.from(upsertProductRequest))
            .let { ResponseEntity.status(HttpStatus.OK).body(it) }
    }

    @Operation(
        summary = "Remove a product from menu by name",
        description = "Idempotent method - will always returns 204"
    )
    @ApiResponses(
        value = [ApiResponse(responseCode = "204", description = "Product removed or not found")]
    )
    @DeleteMapping("/{product_name}")
    fun removeFromMenu(@PathVariable("product_name") productName: String): ResponseEntity<Void> =
        removeProductFromMenuUseCase.execute(productName)
            .let { ResponseEntity.noContent().build() }

    @Operation(summary = "Retrieve products by category", description = "Product List")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Product list with content"),
            ApiResponse(responseCode = "204", description = "Product list empty"),
        ]
    )
    @GetMapping
    fun getProductsByCategory(@RequestParam category: String): ResponseEntity<List<ProductResponse>> =
        findProductsByCategoryUseCase.execute(category)
            .let { products ->
                if (products.isEmpty()) {
                    ResponseEntity.noContent().build()
                } else {
                    ResponseEntity.ok(products)
                }
            }
}