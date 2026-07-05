package com.savia.camaguey.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Utilidad: DateUtils
 * Fechas, timestamps, expiraciones, agrupaciones para VisitStats.
 * Compatible API 21: usa SimpleDateFormat básico.
 */
object DateUtils {

    private val timeZone = TimeZone.getTimeZone("America/Havana")
    private val locale = Locale("es", "CU")

    // Formatters
    private val dayFormat = SimpleDateFormat("yyyy-MM-dd", locale).apply { timeZone = this@DateUtils.timeZone }
    private val weekFormat = SimpleDateFormat("yyyy-'W'ww", locale).apply { timeZone = this@DateUtils.timeZone }
    private val monthFormat = SimpleDateFormat("yyyy-MM", locale).apply { timeZone = this@DateUtils.timeZone }
    private val yearFormat = SimpleDateFormat("yyyy", locale).apply { timeZone = this@DateUtils.timeZone }
    private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", locale).apply { timeZone = this@DateUtils.timeZone }
    private val displayDateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", locale).apply { timeZone = this@DateUtils.timeZone }
    private val displayDayNameFormat = SimpleDateFormat("EEEE", locale).apply { timeZone = this@DateUtils.timeZone }
    private val displayMonthNameFormat = SimpleDateFormat("MMMM yyyy", locale).apply { timeZone = this@DateUtils.timeZone }

    // Timestamp keys for VisitStats
    fun timestampDia(): String = dayFormat.format(Date())
    fun timestampSemana(): String = weekFormat.format(Date())
    fun timestampMes(): String = monthFormat.format(Date())
    fun timestampAno(): String = yearFormat.format(Date())

    fun timestampDia(millis: Long): String = dayFormat.format(Date(millis))
    fun timestampSemana(millis: Long): String = weekFormat.format(Date(millis))
    fun timestampMes(millis: Long): String = monthFormat.format(Date(millis))
    fun timestampAno(millis: Long): String = yearFormat.format(Date(millis))

    // Display formats
    fun displayDate(millis: Long): String = displayDateFormat.format(Date(millis))
    fun displayDateTime(millis: Long): String = displayDateTimeFormat.format(Date(millis))
    fun displayDayName(millis: Long): String = displayDayNameFormat.format(Date(millis)).replaceFirstChar { it.uppercase() }
    fun displayMonthName(millis: Long): String = displayMonthNameFormat.format(Date(millis)).replaceFirstChar { it.uppercase() }

    // Relative dates
    fun todayStart(): Long {
        val cal = Calendar.getInstance(timeZone, locale)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun weekStart(): Long {
        val cal = Calendar.getInstance(timeZone, locale)
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun monthStart(): Long {
        val cal = Calendar.getInstance(timeZone, locale)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun yearStart(): Long {
        val cal = Calendar.getInstance(timeZone, locale)
        cal.set(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    // Expiration checks
    fun isExpired(expiryMillis: Long): Boolean = System.currentTimeMillis() > expiryMillis

    fun daysUntil(millis: Long): Int {
        val diff = millis - System.currentTimeMillis()
        return (diff / (24 * 60 * 60 * 1000)).toInt().coerceAtLeast(0)
    }

    fun daysSince(millis: Long): Int {
        val diff = System.currentTimeMillis() - millis
        return (diff / (24 * 60 * 60 * 1000)).toInt().coerceAtLeast(0)
    }

    // Date string for filters in VisitStats (today, this week start, etc.)
    fun todayDia(): String = timestampDia()
    fun thisWeekStart(): String = timestampSemana(weekStart())
    fun thisMonthStart(): String = timestampMes(monthStart())
    fun thisYearStart(): String = timestampAno(yearStart())
}
