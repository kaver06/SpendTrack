package com.spendingtracker.data

data class ExpenseDetail(
    val id: String = java.util.UUID.randomUUID().toString(),
    val description: String
)
