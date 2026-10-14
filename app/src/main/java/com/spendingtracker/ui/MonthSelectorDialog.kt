package com.spendingtracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.spendingtracker.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MonthSelectorDialog(
    currentKey: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val monthKeys = remember { generateMonthKeys() }
    val fmt = DateTimeFormatter.ofPattern("MMM yyyy")

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(20.dp))
                .background(Surface)
                .border(1.dp, DividerColor, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Select Month",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Accent,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(16.dp))

                monthKeys.forEach { key ->
                    val parts = key.split("-")
                    val year = parts[0].toInt()
                    val month = parts[1].toInt()
                    val date = LocalDate.of(year, month, 1)
                    val label = "${fmt.format(date)} (11th onwards)"
                    val isSelected = key == currentKey

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) GlowPurple else SurfaceVariant)
                            .border(
                                if (isSelected) 1.dp else 0.dp,
                                Accent.copy(alpha = 0.5f),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelect(key) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = if (isSelected) Accent else OnSurface,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                }

                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancel", color = OnSurfaceDim)
                }
            }
        }
    }
}

private fun generateMonthKeys(): List<String> {
    val today = LocalDate.now()
    val keys = mutableListOf<String>()
    // Go back 12 months
    for (i in 0..11) {
        val date = today.minusMonths(i.toLong())
        val key = "${date.year}-${date.monthValue.toString().padStart(2, '0')}"
        keys.add(key)
    }
    return keys
}
