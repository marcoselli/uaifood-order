package br.edu.uaifood.orders.controller


import br.edu.uaifood.orders.controller.order.dto.OrderRequest
import br.edu.uaifood.orders.domain.Order
import br.edu.uaifood.orders.domain.OrderStatus
import br.edu.uaifood.orders.repository.order.OrderRepository
import br.edu.uaifood.orders.repository.order.entity.OrderEntity
import br.edu.uaifood.orders.repository.payment.PaymentRepository
import br.edu.uaifood.orders.repository.payment.entity.PaymentEntity
import br.edu.uaifood.orders.repository.product.entity.ProductEntity
import br.edu.uaifood.orders.service.CheckoutService
import br.edu.uaifood.orders.service.OrderService
import br.edu.uaifood.orders.usecase.FindProductsByIdsUseCase
import br.edu.uaifood.orders.usecase.GenerateQrCodeUseCase
import br.edu.uaifood.orders.util.JsonReader
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith(RandomBeansExtension::class)
class OrderControllerTest(
    @Autowired
    private val mockMvc: MockMvc,
    @Autowired
    private val jsonReader: JsonReader
) {
    @MockkBean
    private lateinit var orderRepository: OrderRepository

    @SpykBean
    private lateinit var orderService: OrderService

    @MockkBean
    private lateinit var checkoutService: CheckoutService

    @MockkBean
    private lateinit var findProductsByIdsUseCase: FindProductsByIdsUseCase

    @MockkBean
    private lateinit var generateQrCodeUseCase: GenerateQrCodeUseCase

    @MockkBean
    private lateinit var paymentRepository: PaymentRepository

    @Test
    fun `should find all orders`(@Random randomProduct: ProductEntity) {
        // Given
        val firstOrder = OrderEntity(1, listOf(randomProduct.copy(category = "DESSERT")),
            OrderStatus.FINISHED,  LocalDateTime.parse("2023-06-20T07:12:10.02"), null)
        val secondOrder = OrderEntity(2, listOf(randomProduct.copy(category = "DRINK")),
            OrderStatus.READY, LocalDateTime.parse("2023-12-23T19:34:50.63"), null)

        // When
        every { orderRepository.findAll() } returns listOf(firstOrder, secondOrder)

        mockMvc.perform(
            get("/v1/orders")
        )

        // Then
        .andExpect(status().isOk)
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.[0].status").value("READY"))
        .andExpect(jsonPath("$.[0].products[0].name").value(randomProduct.name))
        .andExpect(jsonPath("$.[0].creation_date").value("2023-12-23T19:34:50.630"))
    }

    @Test
    fun `should save a order successfully`(
        @Random paymentPersisted: PaymentEntity,
        @Random productPersisted: ProductEntity
    ) {
        // Given
        val orderRequest = jsonReader.import("order_request_ok.json")
        val orderPersisted = OrderEntity.from(
            Order.from(
                jsonReader.importClass("order_request_ok.json", OrderRequest::class.java),
                null
            )
        )

        paymentPersisted.status = PaymentStatus.PENDING;

        // When
        every { orderRepository.save(any()) } returns orderPersisted
        every { checkoutService.fakeCheckout() } returns true
        every { paymentRepository.save(any()) } returns paymentPersisted
        every { generateQrCodeUseCase.execute(any()) } returns ""
        every { checkoutService.fakeCheckout() } returns true
        every { findProductsByIdsUseCase.execute(any()) } returns listOf(productPersisted.copy(category = "DESSERT"))


        mockMvc.perform(
            post("/v1/orders")
                .content(orderRequest)
                .contentType(APPLICATION_JSON)
        )
            // Then
            .andExpect(status().isCreated)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value("WAITING_PAYMENT"))
    }

    @Test
    fun `should save a order with Cpf if customer choose to identify via Cpf`(
        @Random productPersisted: ProductEntity,
        @Random paymentPersisted: PaymentEntity
    ) {
        // Given
        val orderRequest = jsonReader.import("order_request_ok.json")
        val orderPersisted = OrderEntity.from(
            Order.from(
                jsonReader.importClass("order_request_ok.json", OrderRequest::class.java),
                "910.933.630-37"
            )
        )

        // When
        every { orderRepository.save(any()) } returns orderPersisted
        every { checkoutService.fakeCheckout() } returns true
        every { generateQrCodeUseCase.execute(any()) } returns ""
        every { findProductsByIdsUseCase.execute(any()) } returns listOf(productPersisted.copy(category = "DESSERT"))
        every { paymentRepository.save(any()) } returns paymentPersisted

        mockMvc.perform(
            post("/v1/orders?cpf=910.933.630-37")
                .content(orderRequest)
                .contentType(APPLICATION_JSON)
        )
        // Then
            .andExpect(status().isCreated)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value("WAITING_PAYMENT"))
    }


    //@Test
    fun `should not save a order if payment is not confirmed`() {
        // Given
        val orderRequest = jsonReader.import("order_request_ok.json")

        // When
        every { checkoutService.fakeCheckout() } returns false
        mockMvc.perform(
            post("/v1/orders")
                .content(orderRequest)
                .contentType(APPLICATION_JSON)
        )
            // Then
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.status_code").value(400))
            .andExpect(jsonPath("$.message").value("There was a problem with payment and the order was not received"))
    }
}