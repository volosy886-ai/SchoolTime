package com.schooltime.app

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val prefs = getSharedPreferences("schooltime_settings", Context.MODE_PRIVATE)
            var glass by remember { mutableStateOf(prefs.getBoolean("glass", false)) }
            var tick by remember { mutableIntStateOf(0) }
            LaunchedEffect(Unit) { while (true) { delay(1000); tick++ } }
            val status = remember(tick) { Timetable.status() }
            val bg = if (glass) Brush.linearGradient(listOf(Color(0xFF263B55), Color(0xFF101A2B), Color(0xFF46566C))) else Brush.linearGradient(listOf(Color(0xFF090B10), Color(0xFF171C27)))
            Box(Modifier.fillMaxSize().background(bg).padding(20.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    Text("SchoolTime", color = Color.White, fontSize = 28.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { glass=false; prefs.edit().putBoolean("glass",false).apply(); refreshWidget() }) { Text("Классическая") }
                        Button(onClick = { glass=true; prefs.edit().putBoolean("glass",true).apply(); refreshWidget() }) { Text("Жидкое стекло") }
                    }
                    Column(Modifier.fillMaxWidth().background(if(glass) Color(0x664B6E91) else Color(0xFF202632), RoundedCornerShape(28.dp)).padding(24.dp), horizontalAlignment=Alignment.CenterHorizontally) {
                        Text(status.detail, color=Color(0xFF9DB7D8), fontSize=13.sp)
                        Text(status.title, color=Color.White, fontSize=27.sp)
                        if(status.remaining.isNotBlank()) Text(status.remaining, color=Color(0xFF72E5A2), fontSize=36.sp)
                        Text("Далее: ${status.next}", color=Color.LightGray)
                    }
                    Text("Добавь виджет SchoolTime на главный экран: удерживай пустое место → Виджеты → SchoolTime.", color=Color.LightGray, fontSize=13.sp)
                }
            }
        }
    }
    private fun refreshWidget() {
        val intent = android.content.Intent(this, SchoolTimeWidgetReceiver::class.java).apply { action = AppWidgetManager.ACTION_APPWIDGET_UPDATE; putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, AppWidgetManager.getInstance(this@MainActivity).getAppWidgetIds(ComponentName(this@MainActivity, SchoolTimeWidgetReceiver::class.java))) }
        sendBroadcast(intent)
    }
}
