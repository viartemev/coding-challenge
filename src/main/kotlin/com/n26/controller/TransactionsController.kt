package com.n26.controller

import com.n26.controller.domain.TransactionRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeFormatter


@RestController
@RequestMapping("/transactions")
class TransactionsController {

    @PostMapping
    fun addTransaction(@RequestBody transactionRequest: TransactionRequest): ResponseEntity<Any> {
        val now = Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2018-11-25T19:22:29.277Z"))
        if (transactionRequest.timestamp.isAfter(now)) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build<Any>()
        }
        if (Duration.between(transactionRequest.timestamp, now).seconds > 60) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build<Any>()
        }
        // TODO add the transaction
        return ResponseEntity.status(HttpStatus.CREATED).build<Any>()
    }

    @DeleteMapping
    fun deleteTransactions() = run {
        // TODO delete all transactions
        ResponseEntity.noContent().build<Nothing>()
    }
}