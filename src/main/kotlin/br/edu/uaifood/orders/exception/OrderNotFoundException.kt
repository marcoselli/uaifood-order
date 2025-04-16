package br.edu.uaifood.orders.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class OrderNotFoundException(orderId: Long):
    ResponseStatusException(HttpStatus.NOT_FOUND, "Order id $orderId is invalid")