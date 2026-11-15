package com.schooltime.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.ZoneOffset

private data class Lesson(val number: Int, val subject: String, val start: LocalTime, val end: LocalTime)
private data class SchoolDay(val title: String, val lessons: List<Lesson>)
private fun t(value: String) = LocalTime.parse(value, DateTimeFormatter.ofPattern("HH:mm"))
private fun l(n: Int, s: String, a: String, b: String) = Lesson(n, s, t(a), t(b))

private val timetable = mapOf(
    DayOfWeek.MONDAY to SchoolDay("ПОНЕДЕЛЬНИК", listOf(
        l(1,"География","09:10","09:50"), l(2,"История","10:05","10:45"),
        l(3,"Русский язык","11:00","11:40"), l(4,"Литература","11:50","12:30"),
        l(5,"Английский язык","12:50","13:30"), l(6,"Математика","13:40","14:20"))),
    DayOfWeek.TUESDAY to SchoolDay("ВТОРНИК", listOf(
        l(1,"История","08:30","09:15"), l(2,"Музыка","09:25","10:10"),
        l(3,"Русский язык","10:30","11:15"), l(4,"Математика","11:30","12:15"),
        l(5,"Физкультура","12:35","13:20"))),
    DayOfWeek.WEDNESDAY to SchoolDay("СРЕДА", listOf(
        l(1,"Биология","08:30","09:15"), l(2,"ИЗО","09:25","10:10"),
        l(3,"Математика","10:30","11:15"), l(4,"Русский язык","11:30","12:15"),
        l(5,"Практикум по математике","12:35","13:20"), l(6,"Русский язык","13:30","14:15"))),
    DayOfWeek.THURSDAY to SchoolDay("ЧЕТВЕРГ", listOf(
        l(1,"Математика","09:10","09:50"), l(2,"Русский язык","10:05","10:45"),
        l(3,"Русский язык","11:00","11:40"), l(4,"Труд","11:50","12:30"),
        l(5,"Физкультура","12:50","13:30"), l(6,"Английский язык","13:40","14:20"))),
    DayOfWeek.FRIDAY to SchoolDay("ПЯТНИЦА", listOf(
        l(1,"Математика","08:30","09:15"), l(2,"Спортивный модуль","09:25","10:10"),
        l(3,"История","10:30","11:15"), l(4,"Английский язык","11:30","12:15"),
        l(5,"Литература","12:35","13:20"), l(6,"Литература","13:40","14:15")))
)

private data class ScreenState(
    val day: String, val heading: String, val subject: String, val countdownLabel: String,
    val remaining: String, val nextLabel: String, val nextSubject: String, val isBreak: Boolean
)

private fun remaining(target: LocalDateTime, now: LocalDateTime): String {
    val seconds = (target.toEpochSecond(ZoneOffset.UTC) - now.toEpochSecond(ZoneOffset.UTC)).coerceAtLeast(0)
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) "%02d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}

private fun stateAt(now: LocalDateTime): ScreenState {
    val day = timetable[now.dayOfWeek] ?: return ScreenState(
        "ВЫХОДНОЙ", "ОТДЫХАЕМ", "Школьных уроков сегодня нет", "", "",
        "СЛЕДУЮЩИЙ ШАГ", "Набирайся сил 💙", false
    )
    val time = now.toLocalTime()
    val currentIndex = day.lessons.indexOfFirst { !time.isBefore(it.start) && time.isBefore(it.end) }
    if (currentIndex >= 0) {
        val current = day.lessons[currentIndex]
        val next = day.lessons.getOrNull(currentIndex + 1)
        return ScreenState(day.title, "${current.number} УРОК", current.subject,
            "ДО КОНЦА УРОКА", remaining(now.with(current.end), now),
            "СЛЕДУЮЩИЙ УРОК", next?.subject ?: "Уроки закончились! 🎉", false)
    }
    val nextIndex = day.lessons.indexOfFirst { time.isBefore(it.start) }
    if (nextIndex >= 0) {
        val next = day.lessons[nextIndex]
        val previous = day.lessons.getOrNull(nextIndex - 1)
        val onBreak = previous != null && !time.isBefore(previous.end)
        return ScreenState(day.title, if (onBreak) "ПЕРЕМЕНА" else "СКОРО НАЧАЛО",
            if (onBreak) "Отдыхай! ☀️" else "Скоро начнутся уроки",
            "ДО СЛЕДУЮЩЕГО УРОКА", remaining(now.with(next.start), now),
            "СЛЕДУЮЩИЙ УРОК", next.subject, onBreak)
    }
    return ScreenState(day.title, "НА СЕГОДНЯ ВСЁ", "Уроки закончились! 🎉", "",
        "", "ОТДЫХАЙ", "До завтра! 💙", false)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SchoolTimeScreen() }
    }
}

@Composable
fun SchoolTimeScreen() {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) { now = LocalDateTime.now(); delay(1000) }
    }
    val screen = remember(now) { stateAt(now) }
    val blue = Color(0xFF398BFF)
    val green = Color(0xFF55E69A)
    val accent = if (screen.isBreak) blue else green

    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = Color(0xFF090D14)) {
            Column(
                Modifier.fillMaxSize().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("SCHOOLTIME", color = blue, fontSize = 15.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                Spacer(Modifier.height(24.dp))
                Column(
                    Modifier.fillMaxWidth().background(Color(0xFF171F2B), RoundedCornerShape(28.dp)).padding(24.dp)
                ) {
                    Text("${screen.day} · ${screen.heading}",
                        color = if (screen.isBreak) blue else Color.LightGray,
                        fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(24.dp))
                    Text(screen.subject, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    if (screen.countdownLabel.isNotEmpty()) {
                        Spacer(Modifier.height(28.dp))
                        Text(screen.countdownLabel, color = Color.Gray, fontSize = 12.sp)
                    }
                    if (screen.remaining.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text(screen.remaining, color = accent, fontSize = 46.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(28.dp))
                    Box(Modifier.fillMaxWidth().height(1.dp).background(Color.DarkGray))
                    Spacer(Modifier.height(18.dp))
                    Text(screen.nextLabel, color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(screen.nextSubject, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(20.dp))
                Text("Обновляется автоматически 💙", color = Color.Gray, fontSize = 13.sp, textAlign = TextAlign.Center)
            }
        }
    }
}
