package com.spendingtracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendingtracker.data.SettingsManager
import com.spendingtracker.ui.theme.*
import com.spendingtracker.widget.SpendingWidgetProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    var personalTargetText by remember {
        mutableStateOf(SettingsManager.getPersonalTarget(context).toInt().toString())
    }
    var travelTargetText by remember {
        mutableStateOf(SettingsManager.getTravelTarget(context).toInt().toString())
    }
    var salaryDayText by remember {
        mutableStateOf(SettingsManager.getSalaryDay(context).toString())
    }
    var currencyText by remember {
        mutableStateOf(SettingsManager.getCurrency(context))
    }

    var savedMessageVisible by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Background,
        topBar = {
            Surface(color = Surface, shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(OnBackground.copy(alpha = 0.05f))
                            .border(0.5.dp, DividerColor, RoundedCornerShape(10.dp))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = OnSurfaceDim,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(Modifier.width(14.dp))

                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = OnBackground,
                            fontSize = 18.sp
                        )
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Widget Budget Targets Section
            Text(
                text = "Widget Circle Budgets",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Accent,
                    fontWeight = FontWeight.SemiBold
                )
            )

            SettingCard {
                SettingInputField(
                    label = "Personal Expense Limit (₹)",
                    description = "Sets the 100% outer circle boundary for personal expenses (default: 2500)",
                    value = personalTargetText,
                    onValueChange = { personalTargetText = it.filter { ch -> ch.isDigit() } }
                )

                HorizontalDivider(color = DividerColor.copy(alpha = 0.5f), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 12.dp))

                SettingInputField(
                    label = "Travelling Expense Limit (₹)",
                    description = "Sets the 100% outer circle boundary for travelling (default: 1000)",
                    value = travelTargetText,
                    onValueChange = { travelTargetText = it.filter { ch -> ch.isDigit() } }
                )
            }

            // Cycle & Salary Date Section
            Text(
                text = "Salary & Cycle Date",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Accent,
                    fontWeight = FontWeight.SemiBold
                )
            )

            SettingCard {
                SettingInputField(
                    label = "Salary Credit Day",
                    description = "Day of the month salary is credited. Day 1 starts the next day (default: 10th)",
                    value = salaryDayText,
                    onValueChange = {
                        val num = it.filter { ch -> ch.isDigit() }
                        if (num.isEmpty() || (num.toIntOrNull() ?: 0) in 1..28) {
                            salaryDayText = num
                        }
                    }
                )

                HorizontalDivider(color = DividerColor.copy(alpha = 0.5f), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 12.dp))

                SettingInputField(
                    label = "Currency Symbol",
                    description = "Display symbol used across app and text sharing",
                    value = currencyText,
                    onValueChange = { currencyText = it.take(3) },
                    keyboardType = KeyboardType.Text
                )
            }

            // Save button
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Accent.copy(alpha = 0.15f))
                    .border(0.8.dp, Accent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .clickable {
                        val personalTarget = personalTargetText.toDoubleOrNull() ?: 2500.0
                        val travelTarget = travelTargetText.toDoubleOrNull() ?: 1000.0
                        val salaryDay = salaryDayText.toIntOrNull() ?: 10

                        SettingsManager.setPersonalTarget(context, personalTarget)
                        SettingsManager.setTravelTarget(context, travelTarget)
                        SettingsManager.setSalaryDay(context, salaryDay)
                        SettingsManager.setCurrency(context, currencyText.ifBlank { "₹" })

                        SpendingWidgetProvider.updateWidget(context)
                        savedMessageVisible = true
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.Check,
                        contentDescription = "Save Settings",
                        tint = Accent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (savedMessageVisible) "Saved & Widget Updated!" else "Save Settings",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Accent,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            // Info Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceVariant.copy(alpha = 0.4f))
                    .border(0.5.dp, DividerColor, RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "SpendTrack",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = OnBackground,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "Calm Theme · Auto Day Counting · Resizable Circle Widget",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceDim,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SettingCard(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .border(0.5.dp, DividerColor, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun SettingInputField(
    label: String,
    description: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Number
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = OnBackground,
                fontWeight = FontWeight.Medium
            )
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall.copy(
                color = OnSurfaceDim,
                fontSize = 11.sp
            )
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Accent.copy(alpha = 0.5f),
                unfocusedBorderColor = DividerColor,
                focusedContainerColor = SurfaceVariant,
                unfocusedContainerColor = SurfaceVariant,
                focusedTextColor = OnBackground,
                unfocusedTextColor = OnBackground,
                cursorColor = Accent
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = OnBackground,
                fontSize = 13.sp
            )
        )
    }
}
