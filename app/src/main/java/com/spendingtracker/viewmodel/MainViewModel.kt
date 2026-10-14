package com.spendingtracker.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.spendingtracker.data.AppDatabase
import com.spendingtracker.data.DayEntry
import com.spendingtracker.data.ExpenseDetail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class UiState(
    val currentMonthKey: String = "",
    val entries: List<DayEntry> = emptyList(),
    val isCurrentPeriod: Boolean = true,
    val totalDays: Int = 30
)

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).dayEntryDao()

    private val _currentMonthKey = MutableStateFlow(getCurrentPeriodKey())
    val currentMonthKey: StateFlow<String> = _currentMonthKey.asStateFlow()

    val entries: StateFlow<List<DayEntry>> = _currentMonthKey
        .flatMapLatest { key -> dao.getEntriesForMonth(key) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isCurrentPeriod: StateFlow<Boolean> = _currentMonthKey
        .map { it == getCurrentPeriodKey() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val startMonthKey = getCurrentPeriodKey()

    val totalDays: StateFlow<Int> = _currentMonthKey
        .map { key ->
            val context = getApplication<Application>()
            val salaryDay = com.spendingtracker.data.SettingsManager.getSalaryDay(context)
            val parts = key.split("-")
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val startDate = LocalDate.of(year, month, salaryDay + 1)
            val endMonth = if (month == 12) 1 else month + 1
            val endYear = if (month == 12) year + 1 else year
            val endDate = LocalDate.of(endYear, endMonth, salaryDay)
            val days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
            days
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 30)

    fun navigatePrevious() {
        val key = _currentMonthKey.value
        if (key <= startMonthKey) {
            // Do not allow navigating before this month
            return
        }
        val parts = key.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val prevMonth = if (month == 1) 12 else month - 1
        val prevYear = if (month == 1) year - 1 else year
        _currentMonthKey.value = "$prevYear-${prevMonth.toString().padStart(2, '0')}"
    }

    fun navigateNext() {
        val key = _currentMonthKey.value
        val parts = key.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val nextMonth = if (month == 12) 1 else month + 1
        val nextYear = if (month == 12) year + 1 else year
        _currentMonthKey.value = "$nextYear-${nextMonth.toString().padStart(2, '0')}"
    }

    fun updateEntry(entry: DayEntry) {
        viewModelScope.launch {
            dao.insertOrUpdate(entry)
            // Notify widget
            val context = getApplication<Application>()
            com.spendingtracker.widget.SpendingWidgetProvider.updateWidget(context)
        }
    }

    fun getOrCreateEntry(dayNumber: Int): DayEntry {
        val existing = entries.value.find { it.dayNumber == dayNumber }
        return existing ?: DayEntry(
            monthKey = _currentMonthKey.value,
            dayNumber = dayNumber
        )
    }

    fun updateFood(dayNumber: Int, amount: Double) {
        val entry = getOrCreateEntry(dayNumber).copy(food = amount)
        updateEntry(entry)
    }

    fun updateTravelling(dayNumber: Int, amount: Double) {
        val entry = getOrCreateEntry(dayNumber).copy(travelling = amount)
        updateEntry(entry)
    }

    fun updateOthers(dayNumber: Int, amount: Double) {
        val entry = getOrCreateEntry(dayNumber).copy(others = amount)
        updateEntry(entry)
    }

    fun updateFoodDetails(dayNumber: Int, details: List<ExpenseDetail>) {
        val entry = getOrCreateEntry(dayNumber).copy(foodDetails = details)
        updateEntry(entry)
    }

    fun updateTravellingDetails(dayNumber: Int, details: List<ExpenseDetail>) {
        val entry = getOrCreateEntry(dayNumber).copy(travellingDetails = details)
        updateEntry(entry)
    }

    fun updateOthersDetails(dayNumber: Int, details: List<ExpenseDetail>) {
        val entry = getOrCreateEntry(dayNumber).copy(othersDetails = details)
        updateEntry(entry)
    }

    fun getTotalFood(): Double = entries.value.sumOf { it.food }
    fun getTotalTravelling(): Double = entries.value.sumOf { it.travelling }
    fun getTotalOthers(): Double = entries.value.sumOf { it.others }
    fun getTotalPersonal(): Double = getTotalFood() + getTotalOthers()
    fun getTotalTravelExpense(): Double = getTotalTravelling()

    fun getDateForDay(dayNumber: Int): LocalDate {
        val context = getApplication<Application>()
        val salaryDay = com.spendingtracker.data.SettingsManager.getSalaryDay(context)
        val parts = _currentMonthKey.value.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        return LocalDate.of(year, month, salaryDay + 1).plusDays((dayNumber - 1).toLong())
    }

    fun getMonthLabel(): String {
        val key = _currentMonthKey.value
        val parts = key.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val date = LocalDate.of(year, month, 11)
        val fmt = DateTimeFormatter.ofPattern("MMMM")
        return fmt.format(date)
    }

    fun getMonthHeaderText(): String {
        return getMonthLabel()
    }

    companion object {
        fun getCurrentPeriodKey(context: Context? = null): String {
            val today = LocalDate.now()
            val salaryDay = 10
            return if (today.dayOfMonth > salaryDay) {
                // Current month's 11th onwards
                "${today.year}-${today.monthValue.toString().padStart(2, '0')}"
            } else {
                // Previous month's 11th onwards
                val prev = today.minusMonths(1)
                "${prev.year}-${prev.monthValue.toString().padStart(2, '0')}"
            }
        }
    }
}
