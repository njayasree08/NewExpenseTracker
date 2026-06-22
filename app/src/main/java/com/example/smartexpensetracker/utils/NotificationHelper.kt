package com.example.smartexpensetracker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.smartexpensetracker.R

object NotificationHelper {
    private const val CHANNEL_ID = "budget_notifications"
    private const val CHANNEL_NAME = "Budget Alerts"
    private const val NOTIFICATION_ID = 1001

    fun sendBudgetExceededNotification(context: Context, currentExpenses: Double, targetAmount: Double) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notifications for budget limit exceeded"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val message = "Warning: You have exceeded your target budget of ₹$targetAmount! Current spending: ₹$currentExpenses"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use a better icon if available
            .setContentTitle("Budget Exceeded!")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
