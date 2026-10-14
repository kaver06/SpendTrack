package com.spendingtracker.widget

// SpendingWidget.kt
// This file is intentionally kept minimal — all widget logic lives in SpendingWidgetProvider.kt
// which uses RemoteViews + Canvas-drawn bitmaps (compatible with all Android versions).
// If you want to migrate to Glance API in the future, implement GlanceAppWidget here.

object SpendingWidget {
    const val PERSONAL_MAX = 2500.0
    const val TRAVEL_MAX = 1000.0
}
