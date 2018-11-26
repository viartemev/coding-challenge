package com.n26.service

import arrow.core.Either
import arrow.core.Either.Companion.left
import arrow.core.Either.Companion.right
import com.n26.service.domain.Statistics
import com.n26.service.domain.Transaction
import com.n26.service.domain.TransactionsPerSecond
import com.n26.service.exception.InvalidTransaction
import com.n26.service.exception.TransactionIsInTheFuture
import com.n26.service.exception.TransactionIsTooOld
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class TransactionService(val statisticStorage: StatisticStorage) {

    fun deleteTransactions() = statisticStorage.deleteStatistics()

    fun addTransaction(requestTime: Instant, transaction: Transaction): Either<InvalidTransaction, Unit> =
            validateTransaction(requestTime, transaction)
                    .map { statisticStorage.addTransaction(it) }

    fun validateTransaction(requestTime: Instant, transaction: Transaction) = when {
        transaction.timestamp.isAfter(requestTime) -> left(TransactionIsInTheFuture)
        Duration.between(transaction.timestamp, requestTime).toMillis() >= 60000 -> left(TransactionIsTooOld)
        else -> right(transaction)
    }

    fun getStatistics(requestTime: Instant) = statisticStorage
            .getStatistics()
            .asSequence()
            .filterIsInstance<TransactionsPerSecond>()
            .filter { Duration.between(it.timestamp, requestTime).toMillis() < 60000 }
            .fold(Statistics()) { stat, transactionsPerSecond -> stat.update(transactionsPerSecond) }
            .also { it.calculateStatistic() }
}