package com.n26.controller

import arrow.core.Either
import com.n26.controller.domain.TransactionRequest
import com.n26.service.TransactionService
import com.n26.service.exception.TransactionTimeIsFuture
import com.n26.service.exception.TransactionTimeTooOld
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit

@RunWith(SpringRunner::class)
@WebMvcTest(TransactionsController::class)
class TransactionsControllerTest {

    @Autowired
    lateinit var mvc: MockMvc
    @MockBean
    lateinit var transactioService: TransactionService

    @Test
    fun `transaction API should return 201 in case of success`() {
        val now = Instant.now()
        val transactionRequest = TransactionRequest(BigDecimal("12.3343"), now)
        whenever(transactioService.addTransaction(any(), eq(transactionRequest))).doReturn(Either.right(Unit))

        mvc.perform(post("/transactions")
                .content("{\"amount\": \"12.3343\", \"timestamp\": \"$now\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().`is`(201))
        verify(transactioService, times(1)).addTransaction(any(), eq(transactionRequest))
    }

    @Test
    fun `transaction API should return 204 if the transaction is older than 60 seconds`() {
        val now = Instant.now()
        val nowMinus10Minutes = now.minus(10, ChronoUnit.MINUTES)
        val transactionRequest = TransactionRequest(BigDecimal("12.3343"), nowMinus10Minutes)
        whenever(transactioService.addTransaction(any(), eq(transactionRequest))).doReturn(Either.left(TransactionTimeTooOld))

        mvc.perform(post("/transactions")
                .content("{\"amount\": \"12.3343\", \"timestamp\": \"$nowMinus10Minutes\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().`is`(204))
        verify(transactioService, times(1)).addTransaction(any(), eq(transactionRequest))
    }

    @Test
    fun `transaction API should return 400 if the JSON is invalid`() {
        mvc.perform(post("/transactions")
                .content("String")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().`is`(400))
    }

    @Test
    fun `transaction API should return 422 if any of the fields are not parsable`() {
        mvc.perform(post("/transactions")
                .content("{\"timestamp\":\"4/23/2018 11:32 PM\", \"amount\":\"262.01\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().`is`(422))
    }

    @Test
    fun `transaction API should return 422 if the transaction date is in the future`() {
        val now = Instant.now()
        val nowPlus3Seconds = now.plusSeconds(3)
        val transactionRequest = TransactionRequest(BigDecimal("12.3343"), nowPlus3Seconds)
        whenever(transactioService.addTransaction(any(), eq(transactionRequest))).doReturn(Either.left(TransactionTimeIsFuture))

        mvc.perform(post("/transactions")
                .content("{\"amount\": \"12.3343\", \"timestamp\": \"$nowPlus3Seconds\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().`is`(422))
        verify(transactioService, times(1)).addTransaction(any(), eq(transactionRequest))
    }

    @Test
    fun `transaction API should return 204 on delete transactions method`() {
        doNothing().whenever(transactioService).deleteTransactions()
        mvc.perform(delete("/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().`is`(204))

        verify(transactioService, times(1)).deleteTransactions()
    }

}