package com.schooltime.app

import android.content.Context
import androidx.glance.*
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.unit.dp
import androidx.glance.unit.sp
import androidx.glance.background
import androidx.glance.color.ColorProvider as GlanceColorProvider
import androidx.compose.ui.graphics.Color

class SchoolTimeWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = context.getSharedPreferences("schooltime_settings", Context.MODE_PRIVATE)
            val glass = prefs.getBoolean("glass", false)
            val s = Timetable.status()
            Column(Modifier.fillMaxSize().background(ColorProvider(if(glass) 0xCC344B66.toInt() else 0xFF171C27.toInt())).padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(s.detail, style=TextStyle(color=ColorProvider(0xFF9DB7D8.toInt()), fontSize=10.sp))
                Text(s.title, style=TextStyle(color=ColorProvider(0xFFFFFFFF.toInt()), fontSize=16.sp))
                if(s.remaining.isNotBlank()) Text(s.remaining, style=TextStyle(color=ColorProvider(0xFF72E5A2.toInt()), fontSize=23.sp))
                Text("Далее: ${s.next}", style=TextStyle(color=ColorProvider(0xFFD0D7E2.toInt()), fontSize=10.sp))
            }
        }
    }
}
class SchoolTimeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SchoolTimeWidget()
}
