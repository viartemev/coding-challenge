package com.n26.service.domain

import java.math.BigDecimal

data class Statistics(
        var sum: BigDecimal = BigDecimal.ZERO,
        var avg: BigDecimal = BigDecimal.ZERO,
        var max: BigDecimal? = null,
        var min: BigDecimal? = null,
        var count: Long = 0
) {
    fun update(transactionsPerSecond: TransactionsPerSecond): Statistics {
        sum += transactionsPerSecond.sum
        count += transactionsPerSecond.count
        min = if (min == null) transactionsPerSecond.min else transactionsPerSecond.min.min(min)
        max = if (max == null) transactionsPerSecond.max else transactionsPerSecond.max.max(max)
        return this
    }

    fun calculateStatistic(): Statistics {
        if (count != 0L) avg = sum.divide(count.toBigDecimal(), 2, BigDecimal.ROUND_HALF_UP)
        if (min == null) min = BigDecimal.ZERO
        if (max == null) max = BigDecimal.ZERO
        return this
    }

}