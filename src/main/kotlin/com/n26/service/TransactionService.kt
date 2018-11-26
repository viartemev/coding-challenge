package com.n26.service

import arrow.core.Either
import arrow.core.Either.Companion.left
import arrow.core.Either.Companion.right
import com.n26.controller.domain.StatisticsResponse
import com.n26.service.domain.Transaction
import com.n26.service.domain.TransactionsPerSecond
import com.n26.service.exception.InvalidTransaction
import com.n26.service.exception.TransactionIsInTheFuture
import com.n26.service.exception.TransactionIsTooOld
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

@Service
class TransactionService(val statisticsStorage: StatisticsStorage) {

    fun deleteTransactions() = statisticsStorage.deleteStatistics()

    fun addTransaction(requestTime: Instant, transaction: Transaction): Either<InvalidTransaction, Unit> =
            validateTransaction(requestTime, transaction)
                    .map { statisticsStorage.addTransaction(it) }

    fun validateTransaction(requestTime: Instant, transaction: Transaction) = when {
        transaction.timestamp.isAfter(requestTime) -> left(TransactionIsInTheFuture)
        Duration.between(transaction.timestamp, requestTime).toMillis() >= 60000 -> left(TransactionIsTooOld)
        else -> right(transaction)
    }

    fun getStatistics(requestTime: Instant) = statisticsStorage
            .getStatistics()
            .asSequence()
            .filterIsInstance<TransactionsPerSecond>()
            .filter { requestTime.epochSecond - it.epochSecond <= 60 }
            .fold(StatisticsResponse()) { sumStat, statPerSecond -> sumStat.update(statPerSecond) }
            .apply { if (count != 0L) avg = sum.divide(count.toBigDecimal(), 2, BigDecimal.ROUND_HALF_UP) }
            .apply { if (min == null) min = BigDecimal.ZERO }
            .apply { if (max == null) max = BigDecimal.ZERO }
}