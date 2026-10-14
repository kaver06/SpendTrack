package com.spendingtracker.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.spendingtracker.data.DayEntry
import com.spendingtracker.viewmodel.MainViewModel
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object CsvExporter {

    fun exportToCsv(
        context: Context,
        monthKey: String,
        entries: List<DayEntry>,
        viewModel: MainViewModel
    ) {
        val parts = monthKey.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val dateFmt = DateTimeFormatter.ofPattern("MMM d")

        val sb = StringBuilder()
        sb.appendLine("Day,Date,Food,Food Details,Travelling,Travelling Details,Others,Others Details")

        val sortedEntries = entries.sortedBy { it.dayNumber }
        for (entry in sortedEntries) {
            val date = LocalDate.of(year, month, 11).plusDays((entry.dayNumber - 1).toLong())
            val dateStr = dateFmt.format(date)
            val foodDetails = entry.foodDetails.joinToString(" | ") { it.description }
            val travelDetails = entry.travellingDetails.joinToString(" | ") { it.description }
            val othersDetails = entry.othersDetails.joinToString(" | ") { it.description }
            sb.appendLine(
                "Day ${entry.dayNumber},$dateStr,${entry.food},\"$foodDetails\",${entry.travelling},\"$travelDetails\",${entry.others},\"$othersDetails\""
            )
        }

        val totalFood = entries.sumOf { it.food }
        val totalTravel = entries.sumOf { it.travelling }
        val totalOthers = entries.sumOf { it.others }
        val totalPersonal = totalFood + totalOthers

        sb.appendLine(",,,,,,,,")
        sb.appendLine("Totals,,$totalFood,,$totalTravel,,$totalOthers,")
        sb.appendLine("Total Personal Expense (Food + Others),,,,,,,,$totalPersonal")
        sb.appendLine("Total Travelling Expense,,,,,$totalTravel,,,")

        val fileName = "SpendTrack_${monthKey}.csv"

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { os ->
                        os.write(sb.toString().toByteArray())
                    }
                    Toast.makeText(context, "CSV saved to Downloads/$fileName", Toast.LENGTH_LONG).show()
                    // Share the file
                    ShareUtils.shareFile(context, it, "text/csv")
                } ?: run {
                    Toast.makeText(context, "Failed to save CSV", Toast.LENGTH_SHORT).show()
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)
                FileWriter(file).use { it.write(sb.toString()) }
                Toast.makeText(context, "CSV saved to Downloads/$fileName", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
