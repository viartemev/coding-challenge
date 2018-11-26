package com.n26.service.domain

import java.math.BigDecimal
import java.time.Instant

sealed class StatisticPerSecond

object EmptyStatisticPerSecond : StatisticPerSecond()

data class TransactionsPerSecond(
        val timestamp: Instant,
        var count: Long = 0,
        var sum: BigDecimal,
        var max: BigDecimal,
        var min: BigDecimal
) : StatisticPerSecond() {
    fun update(transaction: Transaction) {
        count += 1
        sum += transaction.amount
        min = min.min(transaction.amount)
        max = max.max(transaction.amount)
    }
}

