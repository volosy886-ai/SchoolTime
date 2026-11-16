package com.schooltime.app

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Mint = Color(0xFF77E6C3)
private val Blue = Color(0xFF91B7ED)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("schooltime_settings", Context.MODE_PRIVATE)

        setContent {
            var glass by remember { mutableStateOf(prefs.getBoolean("glass", false)) }
            var settingsOpen by remember { mutableStateOf(false) }
            var tick by remember { mutableIntStateOf(0) }

            LaunchedEffect(Unit) {
                while (true) {
                    delay(1000)
                    tick++
                }
            }

            val status = remember(tick) { Timetable.status() }
            val isLesson = status.detail.startsWith("СЕЙЧАС")
            val background = if (glass) {
                Brush.linearGradient(listOf(Color(0xFF263D55), Color(0xFF101B2B), Color(0xFF344B65)))
            } else {
                Brush.verticalGradient(listOf(Color(0xFF101722), Color(0xFF080C13)))
            }

            MaterialTheme {
                Box(
                    modifier = Modifier.fillMaxSize().background(background).padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { settingsOpen = true }) {
                                Text("⚙", fontSize = 28.sp, color = Color.White)
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (glass) Color(0xAA4A6685) else Color(0xFF1A2637)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (glass) 0.dp else 8.dp)
                        ) {
                            Column(Modifier.fillMaxWidth().padding(24.dp)) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(status.detail, color = Blue, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                    Surface(
                                        shape = RoundedCornerShape(50.dp),
                                        color = if (isLesson) Color(0xFF174D42) else Color(0xFF34445B)
                                    ) {
                                        Text(
                                            if (isLesson) "Урок" else "Перемена",
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                            color = if (isLesson) Mint else Blue,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(Modifier.height(30.dp))
                                Text(status.title, color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(28.dp))

                                Text(if (isLesson) "До звонка" else "До урока", color = Blue, fontSize = 15.sp)
                                Text(
                                    status.remaining.ifBlank { "— — : — —" },
                                    color = Mint,
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(Modifier.height(20.dp))
                                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                                Spacer(Modifier.height(16.dp))
                                Text("Следующий урок", color = Blue, fontSize = 14.sp)
                                Text(status.next, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                        Text("Хорошего учебного дня!", color = Color.White.copy(alpha = 0.55f), fontSize = 14.sp)
                    }

                    if (settingsOpen) {
                        AlertDialog(
                            onDismissRequest = { settingsOpen = false },
                            containerColor = Color(0xFF1B293B),
                            title = { Text("Оформление", color = Color.White) },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            glass = false
                                            prefs.edit().putBoolean("glass", false).apply()
                                            refreshWidget()
                                            settingsOpen = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34445B))
                                    ) { Text("Классическая") }

                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            glass = true
                                            prefs.edit().putBoolean("glass", true).apply()
                                            refreshWidget()
                                            settingsOpen = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF397A75))
                                    ) { Text("Жидкое стекло") }
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { settingsOpen = false }) {
                                    Text("Закрыть", color = Mint)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun refreshWidget() {
        val manager = AppWidgetManager.getInstance(this)
        val component = ComponentName(this, SchoolTimeWidgetReceiver::class.java)
        val ids = manager.getAppWidgetIds(component)
        val intent = Intent(this, SchoolTimeWidgetReceiver::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        sendBroadcast(intent)
    }
}
