package com.n26.service.exception

sealed class InvalidTransaction(message: String) : RuntimeException(message)

object TransactionIsTooOld : InvalidTransaction("Transaction time is older than 60 seconds")
object TransactionIsInTheFuture : InvalidTransaction("Transaction time is in the future")


