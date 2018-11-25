package com.n26.controller.domain

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.n26.controller.serializer.BigDecimalSerializer
import java.math.BigDecimal

data class StatisticsResponse(
        @field:JsonSerialize(using = BigDecimalSerializer::class)
        val sum: BigDecimal = BigDecimal.ZERO,
        @field:JsonSerialize(using = BigDecimalSerializer::class)
        val avg: BigDecimal = BigDecimal.ZERO,
        @field:JsonSerialize(using = BigDecimalSerializer::class)
        val max: BigDecimal = BigDecimal.ZERO,
        @field:JsonSerialize(using = BigDecimalSerializer::class)
        val min: BigDecimal = BigDecimal.ZERO,
        val count: Long = 0
)