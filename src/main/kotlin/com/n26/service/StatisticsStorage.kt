package com.n26.service

import com.n26.service.domain.Transaction
import com.n26.service.domain.EmptyStatisticPerSecond
import com.n26.service.domain.StatisticPerSecond
import com.n26.service.domain.TransactionsPerSecond
import com.n26.utils.ThreadSafe
import org.springframework.stereotype.Component
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

@ThreadSafe
@Component
class StatisticsStorage {
    private val storage = Array<StatisticPerSecond>(60) { EmptyStatisticPerSecond }
    private val lock = ReentrantReadWriteLock(true)

    fun getStatistics() = lock.read { storage.toList() }

    fun deleteStatistics() = lock.write { storage.fill(EmptyStatisticPerSecond) }

    fun addTransaction(transaction: Transaction) {
        val transactionEpochSecond = transaction.timestamp.epochSecond
        val index = (transactionEpochSecond % 60).toInt()
        lock.write {
            val statistic = storage[index]
            when (statistic) {
                is EmptyStatisticPerSecond -> storage[index] = extractStatisticFromTransaction(transaction)
                is TransactionsPerSecond -> if (statistic.epochSecond != transactionEpochSecond) {
                    storage[index] = extractStatisticFromTransaction(transaction)
                } else statistic.update(transaction)
            }
        }
    }

    private fun extractStatisticFromTransaction(transaction: Transaction) = TransactionsPerSecond(
            epochSecond = transaction.timestamp.epochSecond,
            count = 1,
            sum = transaction.amount,
            max = transaction.amount,
            min = transaction.amount
    )

}
