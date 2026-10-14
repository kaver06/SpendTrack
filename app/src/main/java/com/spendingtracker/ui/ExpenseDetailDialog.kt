package com.spendingtracker.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spendingtracker.data.ExpenseDetail
import com.spendingtracker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailDialog(
    dayNumber: Int,
    category: String,
    amount: Double,
    details: List<ExpenseDetail>,
    onDismiss: () -> Unit,
    onSave: (List<ExpenseDetail>) -> Unit
) {
    var editableDetails by remember(details) { mutableStateOf(details.toMutableList()) }
    var newDetailText by remember { mutableStateOf("") }

    val categoryColor = when (category) {
        "Food" -> Accent
        "Travelling" -> Secondary
        else -> AccentLight
    }

    val categoryEmoji = when (category) {
        "Food" -> "🍽️"
        "Travelling" -> "🚗"
        else -> "💰"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        // Half-screen height, translucent background with border
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.50f)
                .clip(RoundedCornerShape(18.dp))
                .background(Surface.copy(alpha = 0.97f))
                .border(0.8.dp, categoryColor.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Compact header row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(categoryColor.copy(alpha = 0.10f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$categoryEmoji Day $dayNumber · $category",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = categoryColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = "₹${amount.toInt()}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = categoryColor.copy(alpha = 0.65f),
                                fontSize = 11.sp
                            )
                        )
                    }
                    // Close – small X icon translucent
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(OnBackground.copy(alpha = 0.05f))
                            .border(0.5.dp, DividerColor, CircleShape)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = OnSurfaceDim,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Divider(color = DividerColor.copy(alpha = 0.5f), thickness = 0.5.dp)

                // Scrollable details list – takes remaining weight
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (editableDetails.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No notes yet. Add one below.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OnSurfaceDim,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                )
                            }
                        }
                    }

                    itemsIndexed(
                        items = editableDetails,
                        key = { _, item -> item.id }
                    ) { index, detail ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically()
                        ) {
                            DetailItem(
                                detail = detail,
                                index = index,
                                categoryColor = categoryColor,
                                onDelete = {
                                    editableDetails = editableDetails.toMutableList().also { list ->
                                        list.removeAt(index)
                                    }
                                }
                            )
                        }
                    }
                }

                Divider(color = DividerColor.copy(alpha = 0.5f), thickness = 0.5.dp)

                // Bottom input + save row — compact
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceVariant.copy(alpha = 0.6f))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Text field
                    OutlinedTextField(
                        value = newDetailText,
                        onValueChange = { newDetailText = it },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 38.dp, max = 90.dp),
                        placeholder = {
                            Text(
                                "Note...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnSurfaceDim.copy(alpha = 0.6f),
                                    fontSize = 12.sp
                                )
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = categoryColor.copy(alpha = 0.5f),
                            unfocusedBorderColor = DividerColor,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = categoryColor,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface
                        ),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 3,
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurface,
                            fontSize = 12.sp
                        )
                    )

                    // Add button – translucent bordered circle
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(categoryColor.copy(alpha = 0.12f))
                            .border(0.8.dp, categoryColor.copy(alpha = 0.4f), CircleShape)
                            .clickable {
                                if (newDetailText.isNotBlank()) {
                                    editableDetails = editableDetails.toMutableList().also {
                                        it.add(ExpenseDetail(description = newDetailText.trim()))
                                    }
                                    newDetailText = ""
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add",
                            tint = categoryColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Save button – translucent bordered pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(categoryColor.copy(alpha = 0.12f))
                            .border(0.8.dp, categoryColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { onSave(editableDetails) }
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Outlined.CheckCircle,
                                contentDescription = "Save",
                                tint = categoryColor,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                "Save",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = categoryColor,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(
    detail: ExpenseDetail,
    index: Int,
    categoryColor: Color,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(categoryColor.copy(alpha = 0.06f))
            .border(0.5.dp, categoryColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${index + 1}.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = categoryColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            ),
            modifier = Modifier.width(20.dp)
        )

        Text(
            text = detail.description,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall.copy(
                color = OnSurface,
                fontSize = 12.sp
            )
        )

        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.Transparent)
                .clickable { onDelete() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.RemoveCircleOutline,
                contentDescription = "Delete",
                tint = OnSurfaceDim.copy(alpha = 0.5f),
                modifier = Modifier.size(15.dp)
            )
        }
    }
}
