package com.schooltime.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Mint = Color(0xFF77E6C3)
private val Blue = Color(0xFF91B7ED)
private val Ink = Color(0xFF101722)
private val Panel = Color(0xFF1A2637)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences(SchoolTimePrefs.FILE, MODE_PRIVATE)
        setContent {
            var glass by remember { mutableStateOf(prefs.getBoolean(SchoolTimePrefs.KEY_GLASS, false)) }
            var onSettings by remember { mutableStateOf(false) }
            var tick by remember { mutableIntStateOf(0) }
            LaunchedEffect(Unit) { while (true) { delay(1000); tick++ } }
            val status = remember(tick) { Timetable.status() }
            val pageBrush = if (glass) {
                Brush.linearGradient(listOf(Color(0xFF263D55), Color(0xFF101B2B), Color(0xFF344B65)))
            } else Brush.verticalGradient(listOf(Color(0xFF101722), Color(0xFF080C13)))

            MaterialTheme(colorScheme = darkColorScheme(primary = Mint, surface = Panel, background = Ink)) {
                Box(Modifier.fillMaxSize().background(pageBrush)) {
                    if (onSettings) {
                        SettingsPage(
                            glass = glass,
                            onBack = { onSettings = false },
                            onSelect = { selected ->
                                glass = selected
                                prefs.edit().putBoolean(SchoolTimePrefs.KEY_GLASS, selected).apply()
                                SchoolTimeWidgetProvider.refresh(this@MainActivity)
                            }
                        )
                    } else {
                        HomePage(status = status, glass = glass, onSettings = { onSettings = true })
                    }
                }
            }
        }
        SchoolTimeWidgetProvider.refresh(this)
    }

    override fun onResume() {
        super.onResume()
        SchoolTimeWidgetProvider.refresh(this)
    }
}

@Composable
private fun HomePage(status: LessonStatus, glass: Boolean, onSettings: () -> Unit) {
    val cardColor = if (glass) Color(0xB83C5875) else Panel
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Surface(color = Color.White.copy(alpha = 0.08f), shape = CircleShape) {
                TextButton(onClick = onSettings, contentPadding = PaddingValues(horizontal = 15.dp, vertical = 7.dp)) {
                    Text("⚙  Настройки", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        Card(
            modifier = Modifier.fillMaxWidth().widthIn(max = 520.dp),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = if (glass) 0.dp else 7.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 22.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(status.detail, color = Blue, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.weight(1f))
                    val badgeText = when (status.phase) {
                        SchoolPhase.LESSON -> "Урок"
                        SchoolPhase.BREAK -> "Перемена"
                        SchoolPhase.BEFORE -> "Скоро"
                        SchoolPhase.FINISHED -> "Завершено"
                        SchoolPhase.WEEKEND -> "Выходной"
                    }
                    Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.10f)) {
                        Text(badgeText, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), color = if (status.phase == SchoolPhase.LESSON) Mint else Blue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(24.dp))
                Text(status.title, color = Color.White, fontSize = 30.sp, lineHeight = 34.sp, fontWeight = FontWeight.ExtraBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (status.remaining.isNotBlank()) {
                    Spacer(Modifier.height(24.dp))
                    Text(status.timerLabel, color = Blue, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(status.remaining, color = Mint, fontSize = 39.sp, lineHeight = 44.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                Spacer(Modifier.height(18.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.13f))
                Spacer(Modifier.height(15.dp))
                Text(status.nextLabel.ifBlank { "ИНФОРМАЦИЯ" }, color = Blue, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(status.next, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(
            when (status.phase) {
                SchoolPhase.FINISHED -> "Хорошего отдыха!"
                SchoolPhase.WEEKEND -> "Приятных выходных!"
                else -> "Хорошего учебного дня!"
            }, color = Color.White.copy(alpha = 0.55f), fontSize = 14.sp, fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SettingsPage(glass: Boolean, onBack: () -> Unit, onSelect: (Boolean) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Назад", color = Mint, fontSize = 15.sp) }
        }
        Spacer(Modifier.height(20.dp))
        Text("Оформление", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Bold)
        Text("Выбери внешний вид приложения и виджета", color = Blue, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
        Spacer(Modifier.height(28.dp))
        ThemeChoiceCard(
            title = "Классическая",
            subtitle = "Тёмная тема · сдержанный дизайн",
            selected = !glass,
            preview = Panel,
            onClick = { onSelect(false) }
        )
        Spacer(Modifier.height(16.dp))
        ThemeChoiceCard(
            title = "Жидкое стекло",
            subtitle = "Полупрозрачные оттенки и мягкие контуры",
            selected = glass,
            preview = Color(0xB83C5875),
            onClick = { onSelect(true) }
        )
        Spacer(Modifier.height(28.dp))
        Text("Выбранная тема применяется и к виджету.", color = Color.White.copy(alpha = 0.60f), fontSize = 13.sp)
    }
}

@Composable
private fun ThemeChoiceCard(title: String, subtitle: String, selected: Boolean, preview: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().widthIn(max = 520.dp).then(
            if (selected) Modifier.border(2.dp, Mint, RoundedCornerShape(24.dp)) else Modifier
        ),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.045f)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(62.dp).background(preview, RoundedCornerShape(17.dp)), contentAlignment = Alignment.Center) {
                Text("12:34", color = Mint, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Column(Modifier.weight(1f).padding(start = 15.dp)) {
                Text(title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Blue, fontSize = 12.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 4.dp))
            }
            if (selected) Text("✓", color = Mint, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}
