package com.n26.controller

import com.n26.service.TransactionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/statistics")
class StatisticsController(val transactionService: TransactionService) {

    @GetMapping
    fun getStatistics() = ResponseEntity.ok(transactionService.getStatistics())
}