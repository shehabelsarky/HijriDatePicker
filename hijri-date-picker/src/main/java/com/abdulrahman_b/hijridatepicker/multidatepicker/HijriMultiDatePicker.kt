package com.abdulrahman_b.hijridatepicker.multidatepicker

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.abdulrahman_b.hijridatepicker.HijriDatePickerFormatter
import com.abdulrahman_b.hijridatepicker.HijriSelectableDates
import com.abdulrahman_b.hijridatepicker.LocalDayOfWeekTextStyle
import com.abdulrahman_b.hijridatepicker.LocalFirstDayOfWeek
import com.abdulrahman_b.hijridatepicker.LocalPickerDecimalStyle
import com.abdulrahman_b.hijridatepicker.LocalPickerFormatter
import com.abdulrahman_b.hijridatepicker.LocalPickerLocale
import com.abdulrahman_b.hijridatepicker.components.DatePickerAnimatedContent
import com.abdulrahman_b.hijridatepicker.datepicker.DateEntryContainer
import com.abdulrahman_b.hijridatepicker.datepicker.DatePickerModeTogglePadding
import com.abdulrahman_b.hijridatepicker.datepicker.DisplayModeToggleButton
import com.abdulrahman_b.hijridatepicker.tokens.DatePickerModalTokens
import java.time.DayOfWeek
import java.time.chrono.HijrahDate
import java.time.format.DecimalStyle
import java.util.Locale

/**
 * Full-featured Hijri multi-date picker.
 * Same UI/UX as [HijriDatePicker], but supports multiple dates.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HijriMultiDatePicker(
    state: HijriMultiDatePickerState,
    modifier: Modifier = Modifier,
    dateFormatter: HijriDatePickerFormatter = remember { HijriMultiDatePickerDefaults.dateFormatter() },
    title: (@Composable () -> Unit)? = {
        HijriMultiDatePickerDefaults.DatePickerTitle(
            displayMode = state.displayMode,
            modifier = Modifier.padding(DateMultiPickerTitlePadding)
        )
    },
    headline: (@Composable () -> Unit)? = {
        HijriMultiDatePickerDefaults.DatePickerHeadline(
            selectedDates = state.selectedDates.filterNotNull(),
            displayMode = state.displayMode,
            modifier = Modifier.padding(DateMultiPickerHeadlinePadding)
        )
    },
    firstDayOfWeek: DayOfWeek = DayOfWeek.SATURDAY,
    dayOfWeekTextStyle: java.time.format.TextStyle = java.time.format.TextStyle.SHORT,
    locale: Locale = LocalConfiguration.current.locales[0],
    decimalStyle: DecimalStyle = DecimalStyle.of(locale),
    showModeToggle: Boolean = false,
    colors: DatePickerColors = DatePickerDefaults.colors()
) {
    val selectableDates = state.selectableDates

    CompositionLocalProvider(
        LocalPickerLocale provides locale,
        LocalPickerDecimalStyle provides decimalStyle,
        LocalPickerFormatter provides dateFormatter,
        LocalFirstDayOfWeek provides firstDayOfWeek,
        LocalDayOfWeekTextStyle provides dayOfWeekTextStyle
    ) {
        DateEntryContainer(
            modifier = modifier,
            title = title,
            headline = headline,
            modeToggleButton =
               /* if (showModeToggle) {
                    {
                        DisplayModeToggleButton(
                            modifier = Modifier.padding(DatePickerModeTogglePadding),
                            displayMode = state.displayMode,
                            onDisplayModeChange = { displayMode -> state.displayMode = displayMode },
                        )
                    }
                } else */

                null,
            headlineTextStyle = DatePickerModalTokens.HeaderHeadlineFont,
            headerMinHeight = DatePickerModalTokens.HeaderContainerHeight,
            colors = colors,
        ) {
            SwitchableMultiDateEntryContent(
                selectedDates = state.selectedDates.toMutableList(),
                displayedMonth = state.displayedMonth,
                displayMode = state.displayMode,
                onDatesSelectionChange = { newDates -> state.selectedDates = newDates.toSet() },
                onDisplayedMonthChange = { month -> state.displayedMonth = month },
                yearRange = state.yearRange,
                selectableDates = selectableDates,
                colors = colors
            )
        }
    }
}



/**
 * Switches between calendar multi-select picker and input mode.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwitchableMultiDateEntryContent(
    selectedDates: List<HijrahDate>,
    displayedMonth: HijrahDate,
    displayMode: DisplayMode,
    onDatesSelectionChange: (List<HijrahDate>) -> Unit,
    onDisplayedMonthChange: (HijrahDate) -> Unit,
    yearRange: IntRange,
    selectableDates: HijriSelectableDates,
    colors: DatePickerColors
) {
    val dateFormatter = LocalPickerFormatter.current

    DatePickerAnimatedContent(displayMode) { mode ->
        when (mode) {
            DisplayMode.Picker ->
                MultiDatePickerContent(
                    selectedDates = selectedDates,
                    displayedMonth = displayedMonth,
                    onDatesSelectionChange = onDatesSelectionChange,
                    onDisplayedMonthChange = onDisplayedMonthChange,
                    yearRange = yearRange,
                    selectableDates = selectableDates,
                    colors = colors
                )

            DisplayMode.Input -> {
                DateMultiInputContent(
                    selectedDates = selectedDates,
                    onDatesChange = onDatesSelectionChange,
                    yearRange = yearRange,
                    selectableDates = selectableDates,
                    colors = colors,
                    pattern = dateFormatter.inputDateSkeleton,
                    patternDelimiter = dateFormatter.inputDateDelimiter
                )
            }
        }
    }
}





private val DateMultiPickerTitlePadding = PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp)
private val DateMultiPickerHeadlinePadding = PaddingValues(start = 24.dp, end = 12.dp, bottom = 12.dp)

// An offset that is applied to the token value for the RangeSelectionHeaderContainerHeight. The
// implementation does not render a "Save" and "X" buttons by default, so we don't take those into
// account when setting the header's max height.
//private val HeaderHeightOffset = 60.dp
private val HeaderHeightOffset = 16.dp
private val DrawScope.RectangleCornerRadius get() = CornerRadius(8.dp.toPx(), 8.dp.toPx())