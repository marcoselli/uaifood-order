package br.edu.uaifood.orders.controller.order

import br.edu.uaifood.orders.controller.order.dto.OrderRequest
import br.edu.uaifood.orders.controller.order.dto.OrderResponse
import br.edu.uaifood.orders.domain.Order
import br.edu.uaifood.orders.exception.ErrorMessageModel
import br.edu.uaifood.orders.exception.OrderPaymentException
import br.edu.uaifood.orders.service.CheckoutService
import br.edu.uaifood.orders.usecase.CreateOrderUseCase
import br.edu.uaifood.orders.usecase.FindAllOrdersUseCase
import br.edu.uaifood.orders.usecase.UpdateOrderStatusUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/orders")
class OrderController(
    private val createOrderUseCase: CreateOrderUseCase,
    private val findAllOrdersUseCase: FindAllOrdersUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase,
    private val checkoutService: CheckoutService
) {

    @Operation(summary = "Get a list of orders", description = "Returns 200 if successful")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Orders", content = [Content(array = ArraySchema(schema = Schema(implementation = OrderResponse::class)))]),
        ]
    )
    @GetMapping
    fun findOrders() =
        findAllOrdersUseCase.execute()
            .let { status(OK).body(it) }

    @Operation(summary = "Create a order", description = "Returns 201 if successful")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Order Created", content = [Content(schema = Schema(implementation = OrderResponse::class))]),
            ApiResponse(responseCode = "400", description = "Error creating Order", content = [Content(schema = Schema(implementation = ErrorMessageModel::class))]),
        ]
    )
    @PostMapping
    fun createOrder(@RequestBody orderRequest: OrderRequest, @RequestParam cpf: String?): ResponseEntity<OrderResponse> {
        val paymentConfirmed = checkoutService.fakeCheckout()
        if (paymentConfirmed) {
            return createOrderUseCase.execute(Order.from(orderRequest, cpf))
                .let { status(HttpStatus.CREATED).body(it) }
        } else {
            throw OrderPaymentException("There was a problem with payment and the order was not received")
        }
    }

    @Operation(summary = "Update order status", description = "Returns 200 if successful")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Order status updated"),
            ApiResponse(responseCode = "404", description = "Order not found", content = [Content(schema = Schema(implementation = ErrorMessageModel::class))]),
        ]
    )
    @PatchMapping("/{id}")
    fun updateOrderStatus(@PathVariable id: Long): ResponseEntity<Void> {
        return updateOrderStatusUseCase.execute(id)
            .let { status(OK).build() }
    }
}