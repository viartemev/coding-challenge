package com.n26.controller

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CustomRestExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMismatchedInputException(ex: HttpMessageNotReadableException): ResponseEntity<Any> {
        return when (ex.cause) {
            is InvalidFormatException -> ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
            is MismatchedInputException -> ResponseEntity.badRequest().build<Any>()
            else -> ResponseEntity.badRequest().build<Any>()
        }
    }
}