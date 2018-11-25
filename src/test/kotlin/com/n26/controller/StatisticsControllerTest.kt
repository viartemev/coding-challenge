package com.n26.controller

import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@WebMvcTest
class StatisticsControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Test
    fun `statistics API should return 200 and empty statistics if nothing was added with correct formatting`() {
        mvc.perform(get("/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.sum", `is`("0.00")))
                .andExpect(jsonPath("$.avg", `is`("0.00")))
                .andExpect(jsonPath("$.max", `is`("0.00")))
                .andExpect(jsonPath("$.min", `is`("0.00")))
                .andExpect(jsonPath("$.count", `is`(0)))
    }

}