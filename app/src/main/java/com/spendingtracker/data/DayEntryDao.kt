package com.spendingtracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DayEntryDao {

    @Query("SELECT * FROM day_entries WHERE monthKey = :monthKey ORDER BY dayNumber ASC")
    fun getEntriesForMonth(monthKey: String): Flow<List<DayEntry>>

    @Query("SELECT * FROM day_entries WHERE monthKey = :monthKey AND dayNumber = :dayNumber LIMIT 1")
    suspend fun getEntry(monthKey: String, dayNumber: Int): DayEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entry: DayEntry): Long

    @Delete
    suspend fun delete(entry: DayEntry)

    @Query("DELETE FROM day_entries WHERE monthKey = :monthKey")
    suspend fun deleteMonth(monthKey: String)

    @Query("SELECT DISTINCT monthKey FROM day_entries ORDER BY monthKey DESC")
    fun getAllMonthKeys(): Flow<List<String>>
}
