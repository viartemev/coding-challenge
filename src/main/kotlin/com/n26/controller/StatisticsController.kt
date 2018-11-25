package com.n26.controller

import com.n26.controller.domain.StatisticsResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/statistics")
class StatisticsController {

    @GetMapping
    fun getStatistics() = run {
        // TODO add statistics fetching
        ResponseEntity.ok(StatisticsResponse())
    }
}