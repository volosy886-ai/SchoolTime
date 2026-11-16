package com.schooltime.app

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class SchoolTimeWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            val prefs = context.getSharedPreferences(
                "schooltime_settings",
                Context.MODE_PRIVATE
            )

            val glass = prefs.getBoolean("glass", false)
            val status = Timetable.status()

            val backgroundColor = if (glass) {
                0xCC344B66.toInt()
            } else {
                0xFF171C27.toInt()
            }

            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(backgroundColor))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    status.detail,
                    style = TextStyle(
                        color = ColorProvider(0xFF9DB7D8.toInt()),
                        fontSize = 10.sp
                    )
                )

                Text(
                    status.title,
                    style = TextStyle(
                        color = ColorProvider(0xFFFFFFFF.toInt()),
                        fontSize = 16.sp
                    )
                )

                if (status.remaining.isNotBlank()) {
                    Text(
                        status.remaining,
                        style = TextStyle(
                            color = ColorProvider(0xFF72E5A2.toInt()),
                            fontSize = 23.sp
                        )
                    )
                }

                Text(
                    "Далее: ${status.next}",
                    style = TextStyle(
                        color = ColorProvider(0xFFD0D7E2.toInt()),
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

class SchoolTimeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget =
        SchoolTimeWidget()
}
