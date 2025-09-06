package com.abdulrahman_b.hijridatepicker.multidatepicker

import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.abdulrahman_b.hijrahdatetime.extensions.HijrahDates
import com.abdulrahman_b.hijridatepicker.HijriSelectableDates
import com.abdulrahman_b.hijridatepicker.valueOf
import java.time.chrono.HijrahDate

/**
 * Represents the state of a Hijri multi-date picker.
 */
@ExperimentalMaterial3Api
@Stable
interface HijriMultiDatePickerState {

    /**
     * The currently selected dates, represented as a set of [HijrahDate].
     */
    var selectedDates: Set<HijrahDate>

    /**
     * The currently displayed month.
     */
    var displayedMonth: HijrahDate

    /**
     * The current display mode of the date picker.
     */
    var displayMode: DisplayMode

    /**
     * The allowed year range.
     */
    val yearRange: IntRange

    /**
     * Defines which dates are selectable.
     */
    val selectableDates: HijriSelectableDates
}

@OptIn(ExperimentalMaterial3Api::class)
internal class HijriMultiDatePickerStateImpl(
    initialSelectedDates: Set<HijrahDate>,
    initialDisplayedMonth: HijrahDate,
    initialDisplayMode: DisplayMode,
    override val yearRange: IntRange,
    override val selectableDates: HijriSelectableDates,
) : HijriMultiDatePickerState {

    override var selectedDates by mutableStateOf(initialSelectedDates)

    override var displayedMonth by mutableStateOf(initialDisplayedMonth)

    override var displayMode by mutableStateOf(initialDisplayMode)

    companion object {
        fun Saver(selectableDates: HijriSelectableDates): Saver<HijriMultiDatePickerState, *> =
            listSaver(
                save = {
                    listOf(
                        it.selectedDates.map { d -> d.toEpochDay() },
                        it.displayedMonth.toEpochDay(),
                        it.yearRange.first,
                        it.yearRange.last,
                        it.displayMode.toString()
                    )
                },
                restore = { value ->
                    HijriMultiDatePickerStateImpl(
                        initialSelectedDates = (value[0] as List<Long>).map(HijrahDates::ofEpochDay).toSet(),
                        initialDisplayedMonth = HijrahDates.ofEpochDay(value[1] as Long),
                        initialDisplayMode = DisplayMode.valueOf(value[4] as String),
                        yearRange = IntRange(value[2] as Int, value[3] as Int),
                        selectableDates = selectableDates
                    )
                }
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberHijriMultiDatePickerState(
    initialSelectedDates: Set<HijrahDate> = emptySet(),
    initialDisplayedMonth: HijrahDate = HijrahDate.now(),
    initialDisplayMode: DisplayMode = DisplayMode.Picker,
    yearRange: IntRange = HijriMultiDatePickerDefaults.YearRange,
    selectableDates: HijriSelectableDates = HijriMultiDatePickerDefaults.AllDates,
): HijriMultiDatePickerState {
    return rememberSaveable(
        saver = HijriMultiDatePickerStateImpl.Saver(selectableDates)
    ) {
        HijriMultiDatePickerStateImpl(
            initialSelectedDates,
            initialDisplayedMonth,
            initialDisplayMode,
            yearRange,
            selectableDates,
        )
    }
}
