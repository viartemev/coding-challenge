package com.n26.serivce

import com.n26.service.StatisticStorage
import com.n26.service.domain.EmptyStatisticPerSecond
import com.n26.service.domain.Transaction
import com.n26.service.domain.TransactionsPerSecond
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant

class StatisticStorageTest {

    @Test
    fun `Statistic storage should be initialized with 60 EmptyStatisticPerSecond's`() {
        val storage = StatisticStorage()
        assertThat(storage.getStatistics()).hasSize(60)
        assertThat(storage.getStatistics()).containsOnly(EmptyStatisticPerSecond)
    }

    @Test
    fun `Statistic storage should return an immutable list of statistics`() {
        val storage = StatisticStorage()
        assertThat(storage.getStatistics()).isInstanceOf(List::class.java)
    }

    @Test
    fun `Statistic storage should replace old transactions statistic by new one if 60 seconds left`() {
        val storage = StatisticStorage()

        val curTrxAmount = BigDecimal.TEN
        val curTrxInstant = Instant.now()
        val curTrx = Transaction(curTrxAmount, curTrxInstant)

        val oldTrxAmount = BigDecimal.ONE
        val oldTrxInstant = curTrxInstant.minusSeconds(60)
        val oldTrx = Transaction(oldTrxAmount, oldTrxInstant)

        storage.addTransaction(oldTrx)
        storage.addTransaction(curTrx)

        val statisticForCurrentTime = TransactionsPerSecond(curTrxInstant, 1, curTrxAmount, curTrxAmount, curTrxAmount)
        val statisticFor60SecondsLeft = TransactionsPerSecond(oldTrxInstant, 1, oldTrxAmount, oldTrxAmount, oldTrxAmount)

        assertThat(storage.getStatistics()).containsOnlyOnce(statisticForCurrentTime)
        assertThat(storage.getStatistics()).doesNotContain(statisticFor60SecondsLeft)
    }

    @Test
    fun `Statistic storage should update transaction statistic if transaction with the same time added`() {
        val storage = StatisticStorage()
        val now = Instant.now()

        val curTrxAmount = BigDecimal.TEN
        val curTrx = Transaction(curTrxAmount, now)

        val oldTrxAmount = BigDecimal.ONE
        val oldTrx = Transaction(oldTrxAmount, now)

        storage.addTransaction(oldTrx)
        storage.addTransaction(curTrx)

        val statistic = TransactionsPerSecond(now, 2, curTrxAmount + oldTrxAmount, curTrxAmount, oldTrxAmount)

        assertThat(storage.getStatistics()).containsOnlyOnce(statistic)
    }


    @Test
    fun `Statistic storage should be filled by EmptyStatisticPerSecond on deleteTransactions method`() {
        val storage = StatisticStorage()
        storage.addTransaction(Transaction(BigDecimal.ONE, Instant.now()))
        assertThat(storage.getStatistics()).hasAtLeastOneElementOfType(TransactionsPerSecond::class.java)
        storage.deleteStatistics()
        assertThat(storage.getStatistics()).containsOnly(EmptyStatisticPerSecond)
    }
}