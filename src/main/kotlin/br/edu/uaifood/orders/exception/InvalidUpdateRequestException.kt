package br.edu.uaifood.orders.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class InvalidUpdateRequestException(
    reason: String = "Path parameter and request body names must be equal",
) : ResponseStatusException(HttpStatus.BAD_REQUEST, reason)