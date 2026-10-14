package com.spendingtracker.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spendingtracker.data.DayEntry
import com.spendingtracker.data.ExpenseDetail
import com.spendingtracker.ui.theme.*
import com.spendingtracker.utils.CsvExporter
import com.spendingtracker.utils.ShareUtils
import com.spendingtracker.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    onNavigateToSettings: () -> Unit = {}
) {
    val entries by viewModel.entries.collectAsState()
    val currentMonthKey by viewModel.currentMonthKey.collectAsState()
    val isCurrentPeriod by viewModel.isCurrentPeriod.collectAsState()
    val totalDays by viewModel.totalDays.collectAsState()
    val context = LocalContext.current

    var showDetailDialog by remember { mutableStateOf(false) }
    var detailDialogDay by remember { mutableIntStateOf(0) }
    var detailDialogCategory by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    val entryMap = remember(entries) { entries.associateBy { it.dayNumber } }

    val lastFilledDay = remember(entries) {
        entries.filter { it.food > 0 || it.travelling > 0 || it.others > 0 }
            .maxOfOrNull { it.dayNumber } ?: 0
    }

    val daysToShow = remember(lastFilledDay, totalDays) {
        minOf(maxOf(lastFilledDay + 2, 5), totalDays)
    }

    val today = LocalDate.now()

    var dragTotal by remember { mutableStateOf(0f) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopBar(
                monthLabel = viewModel.getMonthLabel(),
                isCurrentPeriod = isCurrentPeriod,
                onExportCsv = {
                    CsvExporter.exportToCsv(context, currentMonthKey, entries, viewModel)
                },
                onShare = {
                    ShareUtils.shareAsText(context, currentMonthKey, entries, viewModel)
                },
                onSettings = onNavigateToSettings
            )
        },
        bottomBar = {
            StickyBottomCards(entries = entries)
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (dragTotal < -80f) {
                                viewModel.navigatePrevious()
                            } else if (dragTotal > 80f) {
                                viewModel.navigateNext()
                            }
                            dragTotal = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            dragTotal += dragAmount
                        }
                    )
                },
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item { ColumnHeader() }

            items(daysToShow, key = { it }) { idx ->
                val dayNumber = idx + 1
                val entry = entryMap[dayNumber]
                val date = viewModel.getDateForDay(dayNumber)
                val isToday = date == today

                AnimatedDayRow(
                    dayNumber = dayNumber,
                    date = date,
                    entry = entry,
                    isToday = isToday,
                    isEven = dayNumber % 2 == 0,
                    isEditable = isCurrentPeriod,
                    onFoodChange = { amount -> viewModel.updateFood(dayNumber, amount) },
                    onTravelChange = { amount -> viewModel.updateTravelling(dayNumber, amount) },
                    onOthersChange = { amount -> viewModel.updateOthers(dayNumber, amount) },
                    onFoodDetailClick = {
                        detailDialogDay = dayNumber
                        detailDialogCategory = "Food"
                        showDetailDialog = true
                    },
                    onTravelDetailClick = {
                        detailDialogDay = dayNumber
                        detailDialogCategory = "Travelling"
                        showDetailDialog = true
                    },
                    onOthersDetailClick = {
                        detailDialogDay = dayNumber
                        detailDialogCategory = "Others"
                        showDetailDialog = true
                    }
                )
            }

            item { TotalsRow(entries = entries) }
            item { SummarySection(entries = entries) }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showDetailDialog) {
        val entry = entryMap[detailDialogDay] ?: DayEntry(
            monthKey = currentMonthKey,
            dayNumber = detailDialogDay
        )
        val currentDetails = when (detailDialogCategory) {
            "Food" -> entry.foodDetails
            "Travelling" -> entry.travellingDetails
            else -> entry.othersDetails
        }
        val currentAmount = when (detailDialogCategory) {
            "Food" -> entry.food
            "Travelling" -> entry.travelling
            else -> entry.others
        }

        ExpenseDetailDialog(
            dayNumber = detailDialogDay,
            category = detailDialogCategory,
            amount = currentAmount,
            details = currentDetails,
            onDismiss = { showDetailDialog = false },
            onSave = { updatedDetails ->
                when (detailDialogCategory) {
                    "Food" -> viewModel.updateFoodDetails(detailDialogDay, updatedDetails)
                    "Travelling" -> viewModel.updateTravellingDetails(detailDialogDay, updatedDetails)
                    else -> viewModel.updateOthersDetails(detailDialogDay, updatedDetails)
                }
                showDetailDialog = false
            }
        )
    }
}

@Composable
fun TopBar(
    monthLabel: String,
    isCurrentPeriod: Boolean,
    onExportCsv: () -> Unit,
    onShare: () -> Unit,
    onSettings: () -> Unit
) {
    Surface(color = Surface, shadowElevation = 6.dp) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Settings button – on the left
                VibrantIconButton(
                    onClick = onSettings,
                    borderColor = Color(0xFF818CF8),
                    bgColor = Color(0x1F818CF8),
                    icon = {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFFA5B4FC),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                // Month Capsule Box – Centered in the empty space between left and right buttons
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val capsuleModifier = if (isCurrentPeriod) {
                        Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(GradientStart, GradientEnd)
                                )
                            )
                    } else {
                        Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(GradientStart.copy(alpha = 0.10f))
                            .border(1.5.dp, GradientStart, RoundedCornerShape(percent = 50))
                    }

                    Box(
                        modifier = capsuleModifier.padding(horizontal = 24.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = monthLabel,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrentPeriod) White else Color(0xFFA5B4FC),
                                fontSize = 16.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }

                // Action buttons – on the right (CSV & Share)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VibrantIconButton(
                        onClick = onExportCsv,
                        borderColor = Color(0xFF38BDF8),
                        bgColor = Color(0x1F38BDF8),
                        icon = {
                            Icon(
                                Icons.Outlined.FileDownload,
                                contentDescription = "Export CSV",
                                tint = Color(0xFF7DD3FC),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                    VibrantIconButton(
                        onClick = onShare,
                        borderColor = Color(0xFF34D399),
                        bgColor = Color(0x1F34D399),
                        icon = {
                            Icon(
                                Icons.Outlined.Share,
                                contentDescription = "Share",
                                tint = Color(0xFF6EE7B7),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                }
            }
            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
        }
    }
}

@Composable
fun VibrantIconButton(
    onClick: () -> Unit,
    borderColor: Color,
    bgColor: Color,
    icon: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
fun ColumnHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceVariant)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Day",
            modifier = Modifier.width(66.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                color = OnSurfaceDim,
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center
        )
        CategoryHeader(label = "Food 🍽️", color = Accent, modifier = Modifier.weight(1f))
        CategoryHeader(label = "Travel 🚗", color = Secondary, modifier = Modifier.weight(1f))
        CategoryHeader(label = "Others 💰", color = AccentLight, modifier = Modifier.weight(1f))
    }
    HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
}

@Composable
fun CategoryHeader(label: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = label,
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium.copy(
            color = color,
            fontWeight = FontWeight.Bold
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
fun AnimatedDayRow(
    dayNumber: Int,
    date: LocalDate,
    entry: DayEntry?,
    isToday: Boolean,
    isEven: Boolean,
    isEditable: Boolean,
    onFoodChange: (Double) -> Unit,
    onTravelChange: (Double) -> Unit,
    onOthersChange: (Double) -> Unit,
    onFoodDetailClick: () -> Unit,
    onTravelDetailClick: () -> Unit,
    onOthersDetailClick: () -> Unit
) {
    val visible = remember { MutableTransitionState(false).apply { targetState = true } }
    val dateFmt = DateTimeFormatter.ofPattern("MMM d")

    AnimatedVisibility(
        visibleState = visible,
        enter = fadeIn(tween(200, dayNumber * 15)) + slideInVertically(
            animationSpec = tween(200, dayNumber * 15),
            initialOffsetY = { 20 }
        )
    ) {
        val bgColor = when {
            isToday -> TodayHighlight
            isEven -> RowAlt
            else -> Background
        }

        val borderModifier = if (isToday) {
            Modifier.drawBehind {
                drawRect(
                    color = Accent.copy(alpha = 0.35f),
                    style = Stroke(width = 1.2f)
                )
            }
        } else Modifier

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor)
                .then(borderModifier)
                .padding(vertical = 7.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.width(66.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Day $dayNumber",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (isToday) Accent else OnBackground,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = dateFmt.format(date),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = OnSurfaceDim,
                        fontSize = 10.sp
                    )
                )
            }

            AmountCell(
                value = entry?.food ?: 0.0,
                isEditable = isEditable,
                hasDetails = entry?.foodDetails?.isNotEmpty() == true,
                accentColor = Accent,
                onValueChange = onFoodChange,
                onDetailClick = onFoodDetailClick,
                modifier = Modifier.weight(1f)
            )
            AmountCell(
                value = entry?.travelling ?: 0.0,
                isEditable = isEditable,
                hasDetails = entry?.travellingDetails?.isNotEmpty() == true,
                accentColor = Secondary,
                onValueChange = onTravelChange,
                onDetailClick = onTravelDetailClick,
                modifier = Modifier.weight(1f)
            )
            AmountCell(
                value = entry?.others ?: 0.0,
                isEditable = isEditable,
                hasDetails = entry?.othersDetails?.isNotEmpty() == true,
                accentColor = AccentLight,
                onValueChange = onOthersChange,
                onDetailClick = onOthersDetailClick,
                modifier = Modifier.weight(1f)
            )
        }
        HorizontalDivider(color = DividerColor.copy(alpha = 0.5f), thickness = 0.5.dp)
    }
}

@Composable
fun AmountCell(
    value: Double,
    isEditable: Boolean,
    hasDetails: Boolean,
    accentColor: Color,
    onValueChange: (Double) -> Unit,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember(value) {
        mutableStateOf(if (value == 0.0) "" else value.toInt().toString())
    }

    Row(
        modifier = modifier.padding(horizontal = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicAmountField(
            text = text,
            isEditable = isEditable,
            accentColor = accentColor,
            onTextChange = { newText ->
                text = newText
                onValueChange(newText.toDoubleOrNull() ?: 0.0)
            },
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(2.dp))

        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (hasDetails) accentColor.copy(alpha = 0.20f)
                    else Color.Transparent
                )
                .border(
                    0.8.dp,
                    if (hasDetails) accentColor else DividerColor.copy(alpha = 0.5f),
                    RoundedCornerShape(6.dp)
                )
                .clickable { onDetailClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Details",
                tint = if (hasDetails) accentColor else OnSurfaceDim.copy(alpha = 0.45f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun BasicAmountField(
    text: String,
    isEditable: Boolean,
    accentColor: Color,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isEditable) {
        Box(
            modifier = modifier
                .height(38.dp)
                .drawBehind {
                    drawLine(
                        color = accentColor.copy(alpha = 0.5f),
                        start = Offset(4f, size.height - 2f),
                        end = Offset(size.width - 4f, size.height - 2f),
                        strokeWidth = 1.5f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = text,
                onValueChange = { v ->
                    val filtered = v.filter { it.isDigit() || it == '.' }
                    onTextChange(filtered)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                textStyle = TextStyle(
                    color = OnBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                cursorBrush = SolidColor(accentColor),
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            text = "-",
                            style = TextStyle(
                                color = OnSurfaceDim.copy(alpha = 0.4f),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    innerTextField()
                }
            )
        }
    } else {
        Text(
            text = if (text.isEmpty() || text == "0") "-" else "₹$text",
            modifier = modifier,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (text.isEmpty() || text == "0") OnSurfaceDim.copy(alpha = 0.35f) else OnBackground,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TotalsRow(entries: List<DayEntry>) {
    val totalFood = entries.sumOf { it.food }
    val totalTravel = entries.sumOf { it.travelling }
    val totalOthers = entries.sumOf { it.others }

    Spacer(Modifier.height(8.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceVariant)
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(listOf(Accent.copy(alpha = 0.6f), Secondary.copy(alpha = 0.6f))),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total",
                modifier = Modifier.width(66.dp),
                style = MaterialTheme.typography.titleSmall.copy(
                    color = OnBackground,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            TotalCell(value = totalFood, color = Accent, modifier = Modifier.weight(1f))
            TotalCell(value = totalTravel, color = Secondary, modifier = Modifier.weight(1f))
            TotalCell(value = totalOthers, color = AccentLight, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun TotalCell(value: Double, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = if (value == 0.0) "-" else "₹${value.toInt()}",
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
fun StickyBottomCards(entries: List<DayEntry>) {
    val totalPersonal = entries.sumOf { it.food + it.others }
    val totalTravel = entries.sumOf { it.travelling }

    Surface(
        color = Surface,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BigSummaryCard(
                icon = "💼",
                label = "Total Personal",
                amount = totalPersonal,
                color = Accent,
                modifier = Modifier.weight(1f)
            )
            BigSummaryCard(
                icon = "🚗",
                label = "Total Travel",
                amount = totalTravel,
                color = Secondary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BigSummaryCard(
    icon: String,
    label: String,
    amount: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(68.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.10f))
            .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = icon, fontSize = 12.sp)
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = OnSurfaceDim,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    )
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = "₹${amount.toInt()}",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
        }
    }
}
