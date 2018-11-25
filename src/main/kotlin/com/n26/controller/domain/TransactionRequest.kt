package com.n26.controller.domain

import java.math.BigDecimal
import java.time.Instant

data class TransactionRequest(
        val amount: BigDecimal,
        val timestamp: Instant
)