package br.edu.uaifood.orders.usecase

import br.edu.uaifood.orders.repository.order.entity.OrderEntity
import org.springframework.stereotype.Component

@Component
class GenerateQrCodeUseCase {
     fun execute(orderPersisted: OrderEntity): String {
        val accessToken = "SEU_ACCESS_TOKEN"
        val requestUrl = "https://api.mercadopago.com/checkout/preferences"

        val payload = mapOf(
            "external_reference" to "${orderPersisted.id}",
            "items" to listOf(
                mapOf(
                    "title" to "Pedido ${orderPersisted.id}",
                    "quantity" to 1,
                    "unit_price" to orderPersisted.products.sumOf { it.price }
                )
            )
        )

        return "qr_fake"

//        val client = HttpClient()
//        val response: HttpResponse = client.post(requestUrl) {
//            header("Authorization", "Bearer $accessToken")
//            contentType(ContentType.Application.Json)
//            setBody(payload)
//        }
//
//        if (response.status == HttpStatusCode.Created) {
//            val responseBody = response.body<Map<String, Any>>()
//            return responseBody["init_point"] as String // URL para pagamento
//        } else {
//            throw Exception("Falha ao gerar QR Code: ${response.status}")
//        }
    }
}