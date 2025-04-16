package br.edu.uaifood.orders.repository.order

import br.edu.uaifood.orders.repository.order.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<OrderEntity, Long>