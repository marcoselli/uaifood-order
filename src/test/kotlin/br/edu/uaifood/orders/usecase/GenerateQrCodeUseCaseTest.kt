package br.edu.uaifood.orders.usecase

import br.edu.uaifood.orders.repository.order.entity.OrderEntity
import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(RandomBeansExtension::class)
class GenerateQrCodeUseCaseTest {

    @Test
    fun `should generate a valid code`(@Random orderEntity: OrderEntity) {
        val result = GenerateQrCodeUseCase().execute(orderEntity)
        assertEquals("qrFake", result)

    }
}