package com.spendingtracker.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.widget.RemoteViews
import com.spendingtracker.R
import com.spendingtracker.data.AppDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class SpendingWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_UPDATE_WIDGET) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, SpendingWidgetProvider::class.java)
            )
            onUpdate(context, manager, ids)
        }
    }

    companion object {
        const val ACTION_UPDATE_WIDGET = "com.spendingtracker.UPDATE_WIDGET"

        fun updateWidget(context: Context) {
            val intent = Intent(context, SpendingWidgetProvider::class.java).apply {
                action = ACTION_UPDATE_WIDGET
            }
            context.sendBroadcast(intent)
        }

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val dao = db.dayEntryDao()

                    val personalMax = com.spendingtracker.data.SettingsManager.getPersonalTarget(context)
                    val travelMax = com.spendingtracker.data.SettingsManager.getTravelTarget(context)

                    val currentKey = com.spendingtracker.viewmodel.MainViewModel.getCurrentPeriodKey()
                    val entries = dao.getEntriesForMonth(currentKey).first()

                    val totalPersonal = entries.sumOf { it.food + it.others }
                    val totalTravel = entries.sumOf { it.travelling }

                    val personalPct = if (personalMax > 0) ((totalPersonal / personalMax) * 100).toInt().coerceIn(0, 100) else 0
                    val travelPct = if (travelMax > 0) ((totalTravel / travelMax) * 100).toInt().coerceIn(0, 100) else 0

                    val personalBitmap = createCircularProgressBitmap(
                        percentage = personalPct,
                        amount = totalPersonal.toInt(),
                        arcColor = Color.parseColor("#A855F7"), // Ultra-Bright Neon Purple
                        amountColor = Color.parseColor("#E9D5FF"),
                        label = "Personal"
                    )

                    val travelBitmap = createCircularProgressBitmap(
                        percentage = travelPct,
                        amount = totalTravel.toInt(),
                        arcColor = Color.parseColor("#FF3366"), // Ultra-Bright Neon Coral
                        amountColor = Color.parseColor("#FFE4E6"),
                        label = "Travel"
                    )

                    val views = RemoteViews(context.packageName, R.layout.widget_spending).apply {
                        setImageViewBitmap(R.id.widget_personal_circle, personalBitmap)
                        setImageViewBitmap(R.id.widget_travel_circle, travelBitmap)

                        val launchIntent = Intent(context, com.spendingtracker.MainActivity::class.java).apply {
                            action = Intent.ACTION_MAIN
                            addCategory(Intent.CATEGORY_LAUNCHER)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        val pendingIntent = android.app.PendingIntent.getActivity(
                            context, 0, launchIntent,
                            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                        )
                        setOnClickPendingIntent(R.id.widget_root, pendingIntent)
                        setOnClickPendingIntent(R.id.widget_personal_circle, pendingIntent)
                        setOnClickPendingIntent(R.id.widget_travel_circle, pendingIntent)
                    }

                    withContext(Dispatchers.Main) {
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun createCircularProgressBitmap(
            percentage: Int,
            amount: Int,
            arcColor: Int,
            amountColor: Int,
            label: String
        ): Bitmap {
            val width = 216
            val height = 256
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // Transparent background - clear entire bitmap
            bitmap.eraseColor(Color.TRANSPARENT)

            val centerX = width / 2f
            val centerY = 100f
            val radius = 86f
            val strokeWidth = 16f

            val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#263042")
                style = Paint.Style.STROKE
                this.strokeWidth = strokeWidth
                strokeCap = Paint.Cap.ROUND
            }

            val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = arcColor
                style = Paint.Style.STROKE
                this.strokeWidth = strokeWidth
                strokeCap = Paint.Cap.ROUND
            }

            val oval = RectF(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
            )

            // Draw background track ring
            canvas.drawArc(oval, -90f, 360f, false, trackPaint)

            // Draw active consumption arc
            val sweepAngle = (percentage / 100f) * 360f
            if (sweepAngle > 0) {
                canvas.drawArc(oval, -90f, sweepAngle, false, arcPaint)
            }

            // Percentage in the center - BRIGHT WHITE, BOLD
            val percentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textAlign = Paint.Align.CENTER
                textSize = 38f
                isFakeBoldText = true
            }
            canvas.drawText("$percentage%", centerX, centerY + 2f, percentPaint)

            // Amount below percentage - BRIGHT COLOR, BOLD
            val amtPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = amountColor
                textAlign = Paint.Align.CENTER
                textSize = 22f
                isFakeBoldText = true
            }
            canvas.drawText("₹$amount", centerX, centerY + 34f, amtPaint)

            // Label WELL BELOW the circle - BRIGHT WHITE, BOLD, ZERO OVERLAP
            val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textAlign = Paint.Align.CENTER
                textSize = 26f
                isFakeBoldText = true
            }
            canvas.drawText(label, centerX, height - 20f, labelPaint)

            return bitmap
        }
    }
}
