package com.n26.controller

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.Instant
import java.time.temporal.ChronoUnit

@RunWith(SpringRunner::class)
@WebMvcTest(TransactionsController::class)
class TransactionsControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Test
    fun `transaction API should return 201 in case of success`() {
        val now = Instant.now()
        mvc.perform(post("/transactions")
                .content("{\"amount\": \"12.3343\", \"timestamp\": \"$now\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().`is`(201))
    }

    @Test
    fun `transaction API should return 204 if the transaction is older than 60 seconds`() {
        val now = Instant.now()
        mvc.perform(post("/transactions")
                .content("{\"amount\": \"12.3343\", \"timestamp\": \"${now.minus(10, ChronoUnit.MINUTES)}\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().`is`(204))
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
        mvc.perform(post("/transactions")
                .content("{\"amount\": \"12.3343\", \"timestamp\": \"${now.plusSeconds(3)}\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().`is`(422))
    }

    @Test
    fun `transaction API should return 204 on delete transactions method`() {
        mvc.perform(delete("/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().`is`(204))
    }

}