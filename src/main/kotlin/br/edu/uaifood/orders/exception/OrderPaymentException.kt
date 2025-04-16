package br.edu.uaifood.orders.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class OrderPaymentException (reason: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, reason)
