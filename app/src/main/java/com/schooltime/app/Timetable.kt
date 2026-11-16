package com.schooltime.app

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.Duration

data class Lesson(
    val name: String,
    val start: String,
    val end: String
)

data class LessonStatus(
    val detail: String,
    val title: String,
    val remaining: String,
    val next: String
)

object Timetable {
    private val days = mapOf(
        DayOfWeek.MONDAY to listOf(
            Lesson("География", "09:10", "09:50"),
            Lesson("История", "10:05", "10:45"),
            Lesson("Русский язык", "11:00", "11:40"),
            Lesson("Литература", "11:50", "12:30"),
            Lesson("Английский язык", "12:50", "13:30"),
            Lesson("Математика", "13:40", "14:20")
        ),
        DayOfWeek.TUESDAY to listOf(
            Lesson("История", "08:30", "09:15"),
            Lesson("Музыка", "09:25", "10:10"),
            Lesson("Русский язык", "10:30", "11:15"),
            Lesson("Математика", "11:30", "12:15"),
            Lesson("Физкультура", "12:35", "13:20")
        ),
        DayOfWeek.WEDNESDAY to listOf(
            Lesson("Биология", "08:30", "09:15"),
            Lesson("ИЗО", "09:25", "10:10"),
            Lesson("Математика", "10:30", "11:15"),
            Lesson("Русский язык", "11:30", "12:15"),
            Lesson("Практикум по математике", "12:35", "13:20"),
            Lesson("Русский язык", "13:30", "14:15")
        ),
        DayOfWeek.THURSDAY to listOf(
            Lesson("Математика", "09:10", "09:50"),
            Lesson("Русский язык", "10:05", "10:45"),
            Lesson("Русский язык", "11:00", "11:40"),
            Lesson("Труд", "11:50", "12:30"),
            Lesson("Физкультура", "12:50", "13:30"),
            Lesson("Английский язык", "13:40", "14:20")
        ),
        DayOfWeek.FRIDAY to listOf(
            Lesson("Математика", "08:30", "09:15"),
            Lesson("Спортивный модуль", "09:25", "10:10"),
            Lesson("История", "10:30", "11:15"),
            Lesson("Английский язык", "11:30", "12:15"),
            Lesson("Литература", "12:35", "13:20"),
            Lesson("Литература", "13:40", "14:15")
        )
    )

    private val clock = DateTimeFormatter.ofPattern("HH:mm")

    fun status(): LessonStatus {
        val now = LocalTime.now()
        val lessons = days[LocalDate.now().dayOfWeek].orEmpty()

        if (lessons.isEmpty()) {
            return LessonStatus("СЕГОДНЯ", "Выходной", "", "Занятий нет")
        }

        val current = lessons.indexOfFirst {
            now >= LocalTime.parse(it.start) && now < LocalTime.parse(it.end)
        }

        if (current >= 0) {
            val lesson = lessons[current]
            val end = LocalTime.parse(lesson.end)
            val nextName = lessons.getOrNull(current + 1)?.name ?: "Занятия закончились"
            return LessonStatus(
                "СЕЙЧАС · ${current + 1} УРОК",
                lesson.name,
                countdown(now, end),
                nextName
            )
        }

        val nextIndex = lessons.indexOfFirst { now < LocalTime.parse(it.start) }
        if (nextIndex >= 0) {
            val nextLesson = lessons[nextIndex]
            return LessonStatus(
                if (nextIndex == 0) "СКОРО НАЧНЁТСЯ" else "ПЕРЕМЕНА",
                "Перемена",
                countdown(now, LocalTime.parse(nextLesson.start)),
                nextLesson.name
            )
        }

        return LessonStatus("НА СЕГОДНЯ", "Уроки закончились", "", "До завтра")
    }

    private fun countdown(now: LocalTime, target: LocalTime): String {
        val seconds = Duration.between(now, target).seconds.coerceAtLeast(0)
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "%02d:%02d".format(minutes, remainingSeconds)
    }
}
