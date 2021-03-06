package com.n26.controller

import com.n26.service.domain.Statistics
import com.n26.service.TransactionService
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

@RunWith(SpringRunner::class)
@WebMvcTest(StatisticsController::class)
class StatisticsControllerTest {

    @Autowired
    lateinit var mvc: MockMvc
    @MockBean
    lateinit var transactioService: TransactionService

    @Test
    fun `Statistic API should return 200 and empty statistics if nothing was added with correct formatting`() {
        whenever(transactioService.getStatistics(any())).thenReturn(Statistics().calculateStatistic())

        mvc.perform(get("/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.sum", `is`("0.00")))
                .andExpect(jsonPath("$.avg", `is`("0.00")))
                .andExpect(jsonPath("$.max", `is`("0.00")))
                .andExpect(jsonPath("$.min", `is`("0.00")))
                .andExpect(jsonPath("$.count", `is`(0)))

        verify(transactioService, times(1)).getStatistics(any())
    }

    @Test
    fun `Statistic API should return 200 and correctly formatted numbers`() {
        val statisticsResponse = Statistics(
                sum = BigDecimal("10.345"),
                count = 2,
                avg = BigDecimal("6.1223"),
                max = BigDecimal("13.43643"),
                min = BigDecimal("3.8")
        )
        whenever(transactioService.getStatistics(any())).thenReturn(statisticsResponse)

        mvc.perform(get("/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.sum", `is`("10.35")))
                .andExpect(jsonPath("$.avg", `is`("6.12")))
                .andExpect(jsonPath("$.max", `is`("13.44")))
                .andExpect(jsonPath("$.min", `is`("3.80")))
                .andExpect(jsonPath("$.count", `is`(2)))

        verify(transactioService, times(1)).getStatistics(any())
    }

}