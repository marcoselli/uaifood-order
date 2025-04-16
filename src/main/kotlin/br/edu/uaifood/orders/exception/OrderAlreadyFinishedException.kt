package br.edu.uaifood.orders.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class OrderAlreadyFinishedException : ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is already finished")