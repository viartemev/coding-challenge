package com.n26.controller.domain

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.n26.controller.serializer.BigDecimalSerializer
import com.n26.service.domain.TransactionsPerSecond
import java.math.BigDecimal

data class StatisticsResponse(
        @field:JsonSerialize(using = BigDecimalSerializer::class)
        var sum: BigDecimal = BigDecimal.ZERO,
        @field:JsonSerialize(using = BigDecimalSerializer::class)
        var avg: BigDecimal = BigDecimal.ZERO,
        @field:JsonSerialize(using = BigDecimalSerializer::class)
        var max: BigDecimal? = null,
        @field:JsonSerialize(using = BigDecimalSerializer::class)
        var min: BigDecimal? = null,
        var count: Long = 0
) {
    fun update(transactionsPerSecond: TransactionsPerSecond): StatisticsResponse {
        sum += transactionsPerSecond.sum
        count += transactionsPerSecond.count
        min = if (min == null) transactionsPerSecond.min else transactionsPerSecond.min.min(min)
        max = if (max == null) transactionsPerSecond.max else transactionsPerSecond.max.max(max)
        return this
    }

}