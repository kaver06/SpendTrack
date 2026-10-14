package com.spendingtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "day_entries")
@TypeConverters(Converters::class)
data class DayEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val monthKey: String,       // e.g. "2026-10"
    val dayNumber: Int,         // 1..31, where Day 1 = 11th of month
    val food: Double = 0.0,
    val travelling: Double = 0.0,
    val others: Double = 0.0,
    val foodDetails: List<ExpenseDetail> = emptyList(),
    val travellingDetails: List<ExpenseDetail> = emptyList(),
    val othersDetails: List<ExpenseDetail> = emptyList()
)
