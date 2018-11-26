package com.n26.serivce

import arrow.core.Either
import com.n26.controller.domain.TransactionRequest
import com.n26.service.StatisticsStorage
import com.n26.service.TransactionService
import com.n26.service.exception.TransactionIsInTheFuture
import com.n26.service.exception.TransactionIsTooOld
import com.nhaarman.mockitokotlin2.any
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
        val transaction = TransactionRequest(BigDecimal.TEN, now.minusSeconds(90))

        assertThat(transactionService.addTransaction(now, transaction)).isEqualTo(Either.left(TransactionIsTooOld))
        verify(statisticsStorage, times(0)).addTransaction(transaction)
    }

    @Test
    fun `Transaction service should return TransactionIsInTheFuture error if the transaction is in the future`() {
        val statisticsStorage = mock<StatisticsStorage> {}
        val now = Instant.now()
        val transactionService = TransactionService(statisticsStorage)
        val transaction = TransactionRequest(BigDecimal.TEN, now.plusSeconds(3))

        assertThat(transactionService.addTransaction(now, transaction)).isEqualTo(Either.left(TransactionIsInTheFuture))
        verify(statisticsStorage, times(0)).addTransaction(transaction)
    }

    @Test
    fun `Transaction service should add the correct transaction to the storage`() {
        val statisticsStorage = mock<StatisticsStorage> {}
        val now = Instant.now()
        val transactionService = TransactionService(statisticsStorage)
        val transaction = TransactionRequest(BigDecimal.TEN, now)
        assertThat(transactionService.addTransaction(now, transaction)).isEqualTo(Either.right(Unit))
        verify(statisticsStorage, times(1)).addTransaction(transaction)
    }

}