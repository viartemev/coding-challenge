package com.n26.controller

import com.n26.controller.domain.TransactionRequest
import com.n26.service.TransactionService
import com.n26.service.exception.TransactionTimeIsFuture
import com.n26.service.exception.TransactionTimeTooOld
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant


@RestController
@RequestMapping("/transactions")
class TransactionsController(val transactionService: TransactionService) {

    @PostMapping
    fun addTransaction(@RequestBody transactionRequest: TransactionRequest) = transactionService
            .addTransaction(Instant.now(), transactionRequest).fold(
                    { error ->
                        when (error) {
                            is TransactionTimeIsFuture -> ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build<Any>()
                            is TransactionTimeTooOld -> ResponseEntity.status(HttpStatus.NO_CONTENT).build<Any>()
                        }
                    },
                    { ResponseEntity.status(HttpStatus.CREATED).build<Any>() })

    @DeleteMapping
    fun deleteTransactions() = run {
        transactionService.deleteTransactions()
        ResponseEntity.noContent().build<Nothing>()
    }
}