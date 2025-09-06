package com.abdulrahman_b.hijridatepicker
import com.abdulrahman_b.hijrahdatetime.extensions.HijrahDates.dayOfWeek
import java.time.DayOfWeek
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField

class HijriSelectableDatesImpl(
    private val disabledDaysOfWeek: Set<DayOfWeek> = emptySet(),
    private val disabledDates: Set<HijrahDate> = emptySet(),
    private val disabledMonths: Set<Int> = emptySet(), // Hijri months 1–12
    private val disabledYears: Set<Int> = emptySet(),  // Hijri years
) : HijriSelectableDates {

    override fun isSelectableDate(date: HijrahDate): Boolean {
        // Disable exact dates
        if (date in disabledDates) return false

        // Disable days of week
        if (date.dayOfWeek in disabledDaysOfWeek) return false

        // Disable whole months
        if (date.get(ChronoField.MONTH_OF_YEAR) in disabledMonths) return false

        // Disable whole years
        if (date.get(ChronoField.YEAR) in disabledYears) return false

        return true
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year !in disabledYears
    }
}
