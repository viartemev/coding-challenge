package com.n26.service

import com.n26.controller.domain.StatisticsResponse
import com.n26.controller.domain.TransactionRequest
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class TransactionService {

    fun deleteTransactions() {
    }

    fun addTransaction(requestTime: Instant, transaction: TransactionRequest) {
    }

    fun getStatistics(): StatisticsResponse {
        return StatisticsResponse()
    }
}