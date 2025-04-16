package br.edu.uaifood.orders.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler
    fun handleProductValidationException(exception: ProductValidationException): ResponseEntity<ErrorMessageModel> {

        val errorMessage = ErrorMessageModel(
            exception.statusCode.value(),
            exception.reason
        )
        return ResponseEntity(errorMessage, exception.statusCode)
    }

    @ExceptionHandler
    fun handleProductNotFoundException(exception: ProductNotFoundException): ResponseEntity<ErrorMessageModel> {

        val errorMessage = ErrorMessageModel(
            exception.statusCode.value(),
            exception.reason
        )
        return ResponseEntity(errorMessage, exception.statusCode)
    }
}

class ErrorMessageModel(
    var statusCode: Int,
    var message: String? = null
)