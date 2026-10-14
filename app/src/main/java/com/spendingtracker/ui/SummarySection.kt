package com.spendingtracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendingtracker.data.DayEntry
import com.spendingtracker.ui.theme.*
import com.spendingtracker.viewmodel.MainViewModel

@Composable
fun SummarySection(viewModel: MainViewModel? = null, entries: List<DayEntry>) {
    val totalFood = entries.sumOf { it.food }
    val totalTravel = entries.sumOf { it.travelling }
    val totalOthers = entries.sumOf { it.others }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "📈 Category Breakdown",
            style = MaterialTheme.typography.titleMedium.copy(
                color = OnBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            ),
            modifier = Modifier.padding(bottom = 2.dp)
        )

        SummaryDetailRow(label = "🍽️ Total Food", value = totalFood, color = Accent)
        SummaryDetailRow(label = "🚗 Total Travelling", value = totalTravel, color = Secondary)
        SummaryDetailRow(label = "💰 Total Others", value = totalOthers, color = AccentLight)
    }
}

@Composable
fun SummaryDetailRow(label: String, value: Double, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceVariant)
            .padding(horizontal = 16.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = OnSurface,
                fontWeight = FontWeight.Medium
            )
        )
        Text(
            text = "₹${value.toInt()}",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = color,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
