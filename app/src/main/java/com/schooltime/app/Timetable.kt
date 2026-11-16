package com.schooltime.app

import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

internal data class Lesson(val name: String, val start: String, val end: String)
internal enum class SchoolPhase { LESSON, BREAK, BEFORE, FINISHED, WEEKEND }
internal data class LessonStatus(
    val phase: SchoolPhase,
    val detail: String,
    val title: String,
    val timerLabel: String,
    val remaining: String,
    val nextLabel: String,
    val next: String
)

internal object Timetable {
    private val days = mapOf(
        DayOfWeek.MONDAY to listOf(
            Lesson("География", "09:10", "09:50"), Lesson("История", "10:05", "10:45"),
            Lesson("Русский язык", "11:00", "11:40"), Lesson("Литература", "11:50", "12:30"),
            Lesson("Английский язык", "12:50", "13:30"), Lesson("Математика", "13:40", "14:20")
        ),
        DayOfWeek.TUESDAY to listOf(
            Lesson("История", "08:30", "09:15"), Lesson("Музыка", "09:25", "10:10"),
            Lesson("Русский язык", "10:30", "11:15"), Lesson("Математика", "11:30", "12:15"),
            Lesson("Физкультура", "12:35", "13:20")
        ),
        DayOfWeek.WEDNESDAY to listOf(
            Lesson("Биология", "08:30", "09:15"), Lesson("ИЗО", "09:25", "10:10"),
            Lesson("Математика", "10:30", "11:15"), Lesson("Русский язык", "11:30", "12:15"),
            Lesson("Практикум по математике", "12:35", "13:20"), Lesson("Русский язык", "13:30", "14:15")
        ),
        DayOfWeek.THURSDAY to listOf(
            Lesson("Математика", "09:10", "09:50"), Lesson("Русский язык", "10:05", "10:45"),
            Lesson("Русский язык", "11:00", "11:40"), Lesson("Труд", "11:50", "12:30"),
            Lesson("Физкультура", "12:50", "13:30"), Lesson("Английский язык", "13:40", "14:20")
        ),
        DayOfWeek.FRIDAY to listOf(
            Lesson("Математика", "08:30", "09:15"), Lesson("Спортивный модуль", "09:25", "10:10"),
            Lesson("История", "10:30", "11:15"), Lesson("Английский язык", "11:30", "12:15"),
            Lesson("Литература", "12:35", "13:20"), Lesson("Литература", "13:40", "14:15")
        )
    )

    fun status(now: LocalTime = LocalTime.now(), date: LocalDate = LocalDate.now()): LessonStatus {
        val lessons = days[date.dayOfWeek].orEmpty()
        if (lessons.isEmpty()) return LessonStatus(
            SchoolPhase.WEEKEND, "СЕГОДНЯ", "Выходной", "", "", "", "Занятий нет"
        )

        val currentIndex = lessons.indexOfFirst { now >= it.startTime() && now < it.endTime() }
        if (currentIndex >= 0) {
            val current = lessons[currentIndex]
            val next = lessons.getOrNull(currentIndex + 1)?.name ?: "Последний урок"
            return LessonStatus(
                SchoolPhase.LESSON, "СЕЙЧАС · ${currentIndex + 1} УРОК", current.name,
                "ДО ЗВОНКА", countdown(now, current.endTime()), "СЛЕДУЮЩИЙ УРОК", next
            )
        }

        val nextIndex = lessons.indexOfFirst { now < it.startTime() }
        if (nextIndex >= 0) {
            val next = lessons[nextIndex]
            val before = nextIndex == 0
            return LessonStatus(
                if (before) SchoolPhase.BEFORE else SchoolPhase.BREAK,
                if (before) "СКОРО НАЧНЁТСЯ" else "ПЕРЕМЕНА",
                if (before) "Уроки скоро начнутся" else "Перемена",
                if (before) "ДО ПЕРВОГО УРОКА" else "ДО УРОКА",
                countdown(now, next.startTime()), "СЛЕДУЮЩИЙ УРОК", next.name
            )
        }
        return LessonStatus(
            SchoolPhase.FINISHED, "НА СЕГОДНЯ", "Уроки закончились", "", "", "СЛЕДУЮЩИЙ УРОК", "До завтра"
        )
    }

    private fun Lesson.startTime() = LocalTime.parse(start)
    private fun Lesson.endTime() = LocalTime.parse(end)
    private fun countdown(now: LocalTime, target: LocalTime): String {
        val seconds = Duration.between(now, target).seconds.coerceAtLeast(0)
        return "%02d:%02d".format(seconds / 60, seconds % 60)
    }
}
