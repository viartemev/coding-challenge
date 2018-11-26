package com.n26.controller

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMismatchedInputException(ex: HttpMessageNotReadableException) = when (ex.cause) {
        is InvalidFormatException -> ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build<Nothing>()
        is MismatchedInputException -> ResponseEntity.badRequest().build<Nothing>()
        else -> ResponseEntity.badRequest().build<Nothing>()
    }
}