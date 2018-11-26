package com.n26.service.domain

import com.n26.controller.domain.TransactionRequest
import java.math.BigDecimal

sealed class StatisticPerSecond

data class TransactionsPerSecond(
        val epochSecond: Long,
        var count: Long = 0,
        var sum: BigDecimal,
        var max: BigDecimal,
        var min: BigDecimal
) : StatisticPerSecond() {

    fun update(transaction: TransactionRequest) {
        count += 1
        sum += transaction.amount
        min = min.min(transaction.amount)
        max = max.max(transaction.amount)
    }
}

object EmptyStatisticPerSecond : StatisticPerSecond()

