package br.edu.uaifood.orders.repository

import br.edu.uaifood.orders.domain.Order
import br.edu.uaifood.orders.domain.OrderStatus
import br.edu.uaifood.orders.domain.Product
import br.edu.uaifood.orders.repository.order.OrderRepository
import br.edu.uaifood.orders.repository.order.entity.OrderEntity
import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime.parse
import kotlin.test.Test

@ExtendWith(RandomBeansExtension::class)
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    lateinit var entityManager: TestEntityManager

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Test
    fun `should find all orders`(
        @Random firstRandomProduct: Product,
        @Random secondRandomProduct: Product
    ) {
        //given
        entityManager.persist(OrderEntity.from(Order(1L, listOf(firstRandomProduct),
            OrderStatus.RECEIVED, parse("2023-06-20T19:34:50.63"), null)))
        entityManager.persist(OrderEntity.from(Order(2L, listOf(firstRandomProduct, secondRandomProduct),
            OrderStatus.READY, parse("2023-12-26T07:12:10.02"), "910.933.630-37")))

        //when
        val orders = orderRepository.findAll()

        //then
        assertThat(orders[0].status).isEqualTo(OrderStatus.RECEIVED)
        assertThat(orders[0].creationDate).isEqualTo("2023-06-20T19:34:50.63")
        assertThat(orders[0].customerCPF).isEqualTo(null)
        assertThat(orders[0].products.size).isEqualTo(1)
        assertThat(orders[0].products[0].name).isEqualTo(firstRandomProduct.name)
        assertThat(orders[1].status).isEqualTo(OrderStatus.READY)
        assertThat(orders[1].creationDate).isEqualTo("2023-12-26T07:12:10.02")
        assertThat(orders[1].customerCPF).isEqualTo("910.933.630-37")
        assertThat(orders[1].products.size).isEqualTo(2)
        assertThat(orders[1].products[0].name).isEqualTo(firstRandomProduct.name)
        assertThat(orders[1].products[1].name).isEqualTo(secondRandomProduct.name)
    }

    @Test
    fun `should return empty list if there is no orders`() {
        //when
        val orders = orderRepository.findAll()

        //then
        assertThat(orders.isEmpty())
    }
}