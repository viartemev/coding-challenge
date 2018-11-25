package com.n26.controller

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@RunWith(SpringRunner::class)
@WebMvcTest
class TransactionsControllerTest {

    @Autowired
    lateinit var mvc: MockMvc
    val currentInstant = "2018-11-25T19:22:29.277Z"
    val futureInstant = "2018-11-25T19:32:29.277Z"
    val currentMinus10Minutes = "2018-11-25T19:10:29.277Z"

    @Test
    fun `transactions API should return 201 in case of success`() {
        mvc.perform(post("/transactions")
                .content("{\"amount\": \"12.3343\", \"timestamp\": \"$currentInstant\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().`is`(201))
    }

    @Test
    fun `transactions API should return 204 if the transaction is older than 60 seconds`() {
        mvc.perform(post("/transactions")
                .content("{\"amount\": \"12.3343\", \"timestamp\": \"$currentMinus10Minutes\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().`is`(204))
    }

    @Test
    fun `transactions API should return 400 if the JSON is invalid`() {
        mvc.perform(post("/transactions")
                .content("String")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().`is`(400))
    }

    @Test
    fun `transactions API should return 422 if any of the fields are not parsable`() {
        mvc.perform(post("/transactions")
                .content("{\"timestamp\":\"4/23/2018 11:32 PM\", \"amount\":\"262.01\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().`is`(422))
    }

    @Test
    fun `transactions API should return 422 if the transaction date is in the future`() {
        mvc.perform(post("/transactions")
                .content("{\"amount\": \"12.3343\", \"timestamp\": \"$futureInstant\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().`is`(422))
    }

}