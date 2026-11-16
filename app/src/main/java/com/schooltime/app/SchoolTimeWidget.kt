package com.schooltime.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews

internal object SchoolTimePrefs {
    const val FILE = "schooltime_settings"
    const val KEY_GLASS = "glass"
    fun isGlass(context: Context) = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        .getBoolean(KEY_GLASS, false)
}

class SchoolTimeWidgetProvider : AppWidgetProvider() {
    override fun onEnabled(context: Context) {
        scheduleTicks(context)
    }

    override fun onDisabled(context: Context) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.cancel(tickPendingIntent(context))
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        updateAll(context)
        scheduleTicks(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_TICK, Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED -> {
                updateAll(context)
                scheduleTicks(context)
            }
        }
    }

    companion object {
        const val ACTION_TICK = "com.schooltime.app.ACTION_WIDGET_TICK"

        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, SchoolTimeWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(component)
            if (ids.isEmpty()) return
            val status = Timetable.status()
            val glass = SchoolTimePrefs.isGlass(context)
            ids.forEach { id ->
                val views = RemoteViews(context.packageName, R.layout.widget_schooltime)
                views.setInt(
                    R.id.widget_root, "setBackgroundResource",
                    if (glass) R.drawable.widget_bg_glass else R.drawable.widget_bg_classic
                )
                views.setTextViewText(R.id.widget_status, status.detail)
                views.setTextViewText(R.id.widget_title, status.title)
                views.setTextViewText(R.id.widget_timer_label, status.timerLabel)
                views.setTextViewText(R.id.widget_timer, status.remaining)
                views.setTextViewText(R.id.widget_next, if (status.next.isBlank()) "" else "Далее: ${status.next}")
                views.setViewVisibility(R.id.widget_timer_label, if (status.remaining.isBlank()) android.view.View.GONE else android.view.View.VISIBLE)
                views.setViewVisibility(R.id.widget_timer, if (status.remaining.isBlank()) android.view.View.GONE else android.view.View.VISIBLE)
                val open = Intent(context, MainActivity::class.java)
                val pi = PendingIntent.getActivity(context, 0, open, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                views.setOnClickPendingIntent(R.id.widget_root, pi)
                manager.updateAppWidget(id, views)
            }
        }

        fun refresh(context: Context) = updateAll(context)

        private fun scheduleTicks(context: Context) {
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pending = tickPendingIntent(context)
            val first = SystemClock.elapsedRealtime() + 60_000L
            alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, first, 60_000L, pending)
        }

        private fun tickPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, SchoolTimeWidgetProvider::class.java).setAction(ACTION_TICK)
            return PendingIntent.getBroadcast(
                context, 501, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
