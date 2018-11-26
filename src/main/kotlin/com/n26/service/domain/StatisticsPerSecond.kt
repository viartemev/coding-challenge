package com.n26.service.domain

import java.math.BigDecimal

sealed class StatisticPerSecond

data class TransactionsPerSecond(
        val epochSecond: Long,
        var count: Long = 0,
        var sum: BigDecimal,
        var max: BigDecimal,
        var min: BigDecimal
) : StatisticPerSecond()

object EmptyStatisticPerSecond : StatisticPerSecond()

