package com.schooltime.app

import java.time.DayOfWeek
import java.time.LocalTime
import java.time.LocalDateTime

data class Lesson(val name: String, val start: String, val end: String)
data class Status(val title: String, val detail: String, val next: String, val remaining: String)

object Timetable {
    private val days = mapOf(
        DayOfWeek.MONDAY to listOf(Lesson("География","09:10","09:50"),Lesson("История","10:05","10:45"),Lesson("Русский язык","11:00","11:40"),Lesson("Литература","11:50","12:30"),Lesson("Английский","12:50","13:30"),Lesson("Математика","13:40","14:20")),
        DayOfWeek.TUESDAY to listOf(Lesson("История","08:30","09:15"),Lesson("Музыка","09:25","10:10"),Lesson("Русский язык","10:30","11:15"),Lesson("Математика","11:30","12:15"),Lesson("Физкультура","12:35","13:20")),
        DayOfWeek.WEDNESDAY to listOf(Lesson("Биология","08:30","09:15"),Lesson("ИЗО","09:25","10:10"),Lesson("Математика","10:30","11:15"),Lesson("Русский язык","11:30","12:15"),Lesson("Практикум по математике","12:35","13:20"),Lesson("Русский язык","13:30","14:15")),
        DayOfWeek.THURSDAY to listOf(Lesson("Математика","09:10","09:50"),Lesson("Русский язык","10:05","10:45"),Lesson("Русский язык","11:00","11:40"),Lesson("Труд","11:50","12:30"),Lesson("Физкультура","12:50","13:30"),Lesson("Английский","13:40","14:20")),
        DayOfWeek.FRIDAY to listOf(Lesson("Математика","08:30","09:15"),Lesson("Спортивный модуль","09:25","10:10"),Lesson("История","10:30","11:15"),Lesson("Английский","11:30","12:15"),Lesson("Литература","12:35","13:20"),Lesson("Литература","13:40","14:15"))
    )
    fun today(): List<Lesson> = days[LocalDateTime.now().dayOfWeek] ?: emptyList()
    fun status(): Status {
        val now = LocalTime.now()
        val lessons = today()
        if (lessons.isEmpty()) return Status("Выходной", "Сегодня уроков нет", "Отдыхай!", "")
        lessons.forEachIndexed { i, l ->
            val s = LocalTime.parse(l.start); val e = LocalTime.parse(l.end)
            if (!now.isBefore(s) && now.isBefore(e)) return Status(l.name, "ИДЁТ УРОК ${i+1}", lessons.getOrNull(i+1)?.name ?: "Уроки закончились", left(e))
            if (now.isBefore(s)) return Status("Перемена", "До урока ${i+1}", l.name, left(s))
        }
        return Status("Уроки закончились! 🎉", "На сегодня всё", "Отдыхай!", "")
    }
    private fun left(t: LocalTime): String {
        val sec = java.time.Duration.between(LocalTime.now(), t).seconds.coerceAtLeast(0)
        return "%02d:%02d".format(sec/60, sec%60)
    }
}
