package com.n26.service.exception

sealed class InvalidTransaction(message: String) : RuntimeException(message)

object TransactionTimeTooOld : InvalidTransaction("Transaction time too old")
object TransactionTimeIsFuture : InvalidTransaction("Transaction time is future")


