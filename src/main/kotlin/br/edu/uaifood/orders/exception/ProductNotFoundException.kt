package br.edu.uaifood.orders.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class ProductNotFoundException(reason: String) : ResponseStatusException(HttpStatus.NOT_FOUND, reason)
