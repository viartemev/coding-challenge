package com.n26.serivce

import arrow.core.Either
import com.n26.service.domain.Transaction
import com.n26.service.StatisticsStorage
import com.n26.service.TransactionService
import com.n26.service.domain.EmptyStatisticPerSecond
import com.n26.service.domain.TransactionsPerSecond
import com.n26.service.exception.TransactionIsInTheFuture
import com.n26.service.exception.TransactionIsTooOld
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant

class TransactionServiceTest {

    @Test
    fun `Transaction service should return TransactionIsTooOld error if the transaction is older than 60 seconds`() {
        val statisticsStorage = mock<StatisticsStorage> {}
        val now = Instant.now()
        val transactionService = TransactionService(statisticsStorage)
        val transaction = Transaction(BigDecimal.TEN, now.minusMillis(60001))

        assertThat(transactionService.addTransaction(now, transaction)).isEqualTo(Either.left(TransactionIsTooOld))
        verify(statisticsStorage, times(0)).addTransaction(transaction)
    }

    @Test
    fun `Transaction service should return TransactionIsInTheFuture error if the transaction is in the future`() {
        val statisticsStorage = mock<StatisticsStorage> {}
        val now = Instant.now()
        val transactionService = TransactionService(statisticsStorage)
        val transaction = Transaction(BigDecimal.TEN, now.plusSeconds(3))

        assertThat(transactionService.addTransaction(now, transaction)).isEqualTo(Either.left(TransactionIsInTheFuture))
        verify(statisticsStorage, times(0)).addTransaction(transaction)
    }

    @Test
    fun `Transaction service should add the correct transaction to the storage`() {
        val statisticsStorage = mock<StatisticsStorage> {}
        val now = Instant.now()
        val transactionService = TransactionService(statisticsStorage)
        val transaction = Transaction(BigDecimal.TEN, now)

        assertThat(transactionService.addTransaction(now, transaction)).isEqualTo(Either.right(Unit))
        verify(statisticsStorage, times(1)).addTransaction(transaction)
    }

    @Test
    fun `Transaction service should delegate delete trensaction operation to the storage`() {
        val statisticsStorage = mock<StatisticsStorage> {}
        val transactionService = TransactionService(statisticsStorage)

        assertThat(transactionService.deleteTransactions()).isEqualTo(Unit)
        verify(statisticsStorage, times(1)).deleteStatistics()
    }

    @Test
    fun `Transaction service should return empty statistics if there are no transactions`() {
        val now = Instant.now()
        val statisticsStorage = mock<StatisticsStorage> {
            on { getStatistics() } doReturn listOf(EmptyStatisticPerSecond, EmptyStatisticPerSecond)
        }
        val transactionService = TransactionService(statisticsStorage)
        val statistics = transactionService.getStatistics(now)

        assertThat(statistics.count).isEqualTo(0)
        assertThat(statistics.max).isEqualTo(BigDecimal.ZERO)
        assertThat(statistics.min).isEqualTo(BigDecimal.ZERO)
        assertThat(statistics.sum).isEqualTo(BigDecimal.ZERO)
        assertThat(statistics.avg).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `Transaction service should collect statistic from all transactions`() {
        val now = Instant.now()
        val validStat1 = TransactionsPerSecond(now.minusMillis(30000), 1, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN)
        val validStat2 = TransactionsPerSecond(now, 1, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE)
        val outDatedTransaction = TransactionsPerSecond(now.minusMillis(60001), 1, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE)
        val statisticsStorage = mock<StatisticsStorage> {
            on { getStatistics() } doReturn listOf(EmptyStatisticPerSecond, validStat1, validStat2, outDatedTransaction)
        }
        val transactionService = TransactionService(statisticsStorage)
        val statistics = transactionService.getStatistics(now)

        assertThat(statistics.count).isEqualTo(2)
        assertThat(statistics.max).isEqualTo(BigDecimal.TEN)
        assertThat(statistics.min).isEqualTo(BigDecimal.ONE)
        assertThat(statistics.sum).isEqualTo(BigDecimal(11))
        assertThat(statistics.avg).isEqualTo(BigDecimal("5.50"))
    }
}