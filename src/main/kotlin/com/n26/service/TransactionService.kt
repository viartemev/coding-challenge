package com.n26.service

import arrow.core.Either
import com.n26.controller.domain.StatisticsResponse
import com.n26.controller.domain.TransactionRequest
import com.n26.service.exception.InvalidTransaction
import com.n26.service.exception.TransactionTimeIsFuture
import com.n26.service.exception.TransactionTimeTooOld
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class TransactionService {

    fun deleteTransactions() {
    }

    fun addTransaction(requestTime: Instant, transaction: TransactionRequest): Either<InvalidTransaction, Unit> {
        if (transaction.timestamp.isAfter(requestTime)) {
            return Either.left(TransactionTimeIsFuture)
        }
        if (Duration.between(transaction.timestamp, requestTime).seconds > 60) {
            return Either.left(TransactionTimeTooOld)
        }
        return Either.right(Unit)
    }

    fun getStatistics(): StatisticsResponse {
        return StatisticsResponse()
    }
}