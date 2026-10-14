package com.spendingtracker.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.spendingtracker.data.DayEntry
import com.spendingtracker.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ShareUtils {

    fun shareAsText(
        context: Context,
        monthKey: String,
        entries: List<DayEntry>,
        viewModel: MainViewModel? = null
    ) {
        val parts = monthKey.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val monthFmt = DateTimeFormatter.ofPattern("MMMM yyyy")
        val startDate = LocalDate.of(year, month, 11)
        val monthLabel = monthFmt.format(startDate)

        val sb = StringBuilder()
        sb.appendLine("📊 Spending Tracker - $monthLabel")
        sb.appendLine("──────────────────────")
        sb.appendLine("Day | Food | Travel | Others")
        sb.appendLine("──────────────────────")

        val sortedEntries = entries.sortedBy { it.dayNumber }
        for (entry in sortedEntries) {
            if (entry.food == 0.0 && entry.travelling == 0.0 && entry.others == 0.0) continue
            val food = if (entry.food > 0) "₹${entry.food.toInt()}" else "-"
            val travel = if (entry.travelling > 0) "₹${entry.travelling.toInt()}" else "-"
            val others = if (entry.others > 0) "₹${entry.others.toInt()}" else "-"
            sb.appendLine("Day ${entry.dayNumber} | $food | $travel | $others")
        }

        val totalFood = entries.sumOf { it.food }
        val totalTravel = entries.sumOf { it.travelling }
        val totalOthers = entries.sumOf { it.others }
        val totalPersonal = totalFood + totalOthers

        sb.appendLine("──────────────────────")
        sb.appendLine("Total | ₹${totalFood.toInt()} | ₹${totalTravel.toInt()} | ₹${totalOthers.toInt()}")
        sb.appendLine("──────────────────────")
        sb.appendLine("💼 Total Personal Expense: ₹${totalPersonal.toInt()}")
        sb.appendLine("🚗 Total Travelling Expense: ₹${totalTravel.toInt()}")

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sb.toString())
        }
        context.startActivity(Intent.createChooser(intent, "Share via"))
    }

    fun shareFile(context: Context, uri: Uri, mimeType: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share CSV via"))
    }
}
