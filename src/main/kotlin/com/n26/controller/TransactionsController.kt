package com.n26.controller

import com.n26.controller.domain.TransactionRequest
import com.n26.service.TransactionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.time.Instant


@RestController
@RequestMapping("/transactions")
class TransactionsController(val transactionService: TransactionService) {

    @PostMapping
    fun addTransaction(@RequestBody transactionRequest: TransactionRequest): ResponseEntity<Any> {
        val now = Instant.now()
        if (Duration.between(transactionRequest.timestamp, now).seconds > 60) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build<Any>()
        }
        if (transactionRequest.timestamp.isAfter(now)) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build<Any>()
        }
        transactionService.addTransaction(now, transactionRequest)
        return ResponseEntity.status(HttpStatus.CREATED).build<Any>()
    }

    @DeleteMapping
    fun deleteTransactions() = run {
        transactionService.deleteTransactions()
        ResponseEntity.noContent().build<Nothing>()
    }
}