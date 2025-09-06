package com.abdulrahman_b.hijridatepicker.multidatepicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abdulrahman_b.hijrahdatetime.extensions.HijrahDates.withDayOfMonth
import com.abdulrahman_b.hijrahdatetime.extensions.HijrahDates.year
import com.abdulrahman_b.hijridatepicker.HijriSelectableDates
import com.abdulrahman_b.hijridatepicker.LocalFirstDayOfWeek
import com.abdulrahman_b.hijridatepicker.LocalPickerDecimalStyle
import com.abdulrahman_b.hijridatepicker.LocalPickerFormatter
import com.abdulrahman_b.hijridatepicker.LocalPickerLocale
import com.abdulrahman_b.hijridatepicker.R
import com.abdulrahman_b.hijridatepicker.calculateDateFromPage
import com.abdulrahman_b.hijridatepicker.calculateDaysFromStartOfWeekToFirstOfMonth
import com.abdulrahman_b.hijridatepicker.calculatePageFromDate
import com.abdulrahman_b.hijridatepicker.calculateTotalPages
import com.abdulrahman_b.hijridatepicker.components.Day
import com.abdulrahman_b.hijridatepicker.components.MAX_CALENDAR_ROWS
import com.abdulrahman_b.hijridatepicker.components.WeekDays
import com.abdulrahman_b.hijridatepicker.components.updateDisplayedMonth
import com.abdulrahman_b.hijridatepicker.datepicker.DAYS_IN_WEEK
import com.abdulrahman_b.hijridatepicker.datepicker.DateInputFormat
import com.abdulrahman_b.hijridatepicker.datepicker.DateInputTextField
import com.abdulrahman_b.hijridatepicker.datepicker.DatePickerHorizontalPadding
import com.abdulrahman_b.hijridatepicker.datepicker.InputIdentifier
import com.abdulrahman_b.hijridatepicker.datepicker.InputTextFieldPadding
import com.abdulrahman_b.hijridatepicker.datepicker.RecommendedSizeForAccessibility
import com.abdulrahman_b.hijridatepicker.rangedatepicker.CalendarMonthSubheadPadding
import com.abdulrahman_b.hijridatepicker.rangedatepicker.customScrollActions
import com.abdulrahman_b.hijridatepicker.rememberDateInputValidator
import com.abdulrahman_b.hijridatepicker.tokens.DatePickerModalTokens
import java.time.chrono.HijrahDate
import java.time.format.DecimalStyle
import com.abdulrahman_b.hijridatepicker.R.string.date_range_picker_scroll_to_next_month
import com.abdulrahman_b.hijridatepicker.R.string.date_range_picker_scroll_to_previous_month

/**
 * Equivalent of DateInputContent/DateRangeInputContent for MULTI date selection
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateMultiInputContent(
    selectedDates: List<HijrahDate>,
    onDatesChange: (List<HijrahDate>) -> Unit,
    yearRange: IntRange,
    pattern: String,
    patternDelimiter: Char,
    selectableDates: HijriSelectableDates,
    colors: DatePickerColors,
) {
    val locale = LocalPickerLocale.current
    val dateInputFormat = remember(locale) { DateInputFormat(pattern, patternDelimiter) }

    val dateInputValidator = rememberDateInputValidator(
        dateInputFormat = dateInputFormat,
        yearRange = yearRange,
        selectableDates = selectableDates,
        errorInvalidRangeInput = "" // not needed here
    )

    val patternDisplay = dateInputFormat.patternWithDelimiters.uppercase()

    Column(
        modifier = Modifier.padding(InputTextFieldPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        selectedDates.forEachIndexed { index, date ->
            DateInputTextField(
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        stringResource(R.string.date_input_label) + " ${index + 1}",
                        modifier = Modifier.semantics {
                            contentDescription =
                                "Date ${index + 1}, $patternDisplay"
                        }
                    )
                },
                placeholder = {
                    Text(
                        patternDisplay,
                        modifier = Modifier.clearAndSetSemantics {}
                    )
                },
                initialDate = date,
                onDateSelectionChange = { newDate ->
                    if (newDate != null) {
                        val mutableList = selectedDates.toMutableList()
                        mutableList[index] = newDate
                        onDatesChange(mutableList)
                    }
                },
                inputIdentifier = InputIdentifier.SingleDateInput,
                dateInputValidator = dateInputValidator,
                dateInputFormat = dateInputFormat,
                locale = locale,
                decimalStyle = DecimalStyle.STANDARD,
                colors = colors,
            )
        }

       /* // ➕ Add extra input for appending a new date
        TextButton(
            onClick = {
                val mutableList = selectedDates.toMutableList()
                mutableList.add(null) // empty new slot
                onDatesChange(mutableList)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.add_another_date))
        }*/
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MultiDatePickerContent(
    selectedDates: List<HijrahDate>,
    displayedMonth: HijrahDate,
    onDatesSelectionChange: (List<HijrahDate>) -> Unit,
    onDisplayedMonthChange: (HijrahDate) -> Unit,
    yearRange: IntRange,
    selectableDates: HijriSelectableDates,
    colors: DatePickerColors
) {
    val monthPager = rememberPagerState(
        initialPage = calculatePageFromDate(displayedMonth, yearRange),
    ) {
        calculateTotalPages(yearRange)
    }

    Column(modifier = Modifier.padding(horizontal = DatePickerHorizontalPadding)) {
        WeekDays(colors)

        VerticalMonthsListMulti(
            monthPager = monthPager,
            selectedDates = selectedDates,
            onDatesSelectionChange = onDatesSelectionChange,
            onDisplayedMonthChange = onDisplayedMonthChange,
            yearRange = yearRange,
            selectableDates = selectableDates,
            colors = colors
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerticalMonthsListMulti(
    monthPager: PagerState,
    selectedDates: List<HijrahDate>,
    onDatesSelectionChange: (List<HijrahDate>) -> Unit,
    onDisplayedMonthChange: (HijrahDate) -> Unit,
    yearRange: IntRange,
    selectableDates: HijriSelectableDates,
    colors: DatePickerColors
) {
    val today = HijrahDate.now()
    val dateFormatter = LocalPickerFormatter.current

    ProvideTextStyle(DatePickerModalTokens.DateLabelTextFont) {
        val coroutineScope = rememberCoroutineScope()
        val scrollToPreviousMonthLabel =
            stringResource(date_range_picker_scroll_to_previous_month)
        val scrollToNextMonthLabel =
            stringResource(date_range_picker_scroll_to_next_month)

        val customAccessibilityAction =
            customScrollActions(
                state = monthPager,
                coroutineScope = coroutineScope,
                scrollUpLabel = scrollToPreviousMonthLabel,
                scrollDownLabel = scrollToNextMonthLabel
            )

        VerticalPager(
            state = monthPager
        ) { currentPage ->
            val displayedMonth = calculateDateFromPage(currentPage, yearRange)

            Column(modifier = Modifier.fillMaxWidth()) {
                ProvideTextStyle(DatePickerModalTokens.RangeSelectionMonthSubheadFont) {
                    Text(
                        text = dateFormatter.formatMonthYear(
                            displayedMonth,
                            LocalPickerLocale.current,
                            LocalPickerDecimalStyle.current
                        ) ?: "-",
                        modifier = Modifier
                            .padding(paddingValues = CalendarMonthSubheadPadding)
                            .semantics { customActions = customAccessibilityAction },
                        color = colors.subheadContentColor
                    )
                }

                // MonthMulti instead of Month
                MonthMulti(
                    displayedMonth = displayedMonth,
                    selectedDates = selectedDates,
                    onDatesSelectionChange = onDatesSelectionChange,
                    today = today,
                    selectableDates = selectableDates,
                    colors = colors
                )
            }
        }
    }

    LaunchedEffect(monthPager) {
        updateDisplayedMonth(
            pagerState = monthPager,
            onDisplayedMonthChange = onDisplayedMonthChange,
            yearRange = yearRange
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MonthMulti(
    displayedMonth: HijrahDate,
    selectedDates: List<HijrahDate>,
    onDatesSelectionChange: (List<HijrahDate>) -> Unit,
    today: HijrahDate,
    selectableDates: HijriSelectableDates,
    colors: DatePickerColors
) {
    val dateFormatter = LocalPickerFormatter.current
    val firstDayOfWeek = LocalFirstDayOfWeek.current
    val daysFromStartOfWeekToFirstOfMonth = remember(displayedMonth) {
        calculateDaysFromStartOfWeekToFirstOfMonth(displayedMonth, firstDayOfWeek)
    }

    val numberOfDays = remember(displayedMonth) {
        displayedMonth.lengthOfMonth()
    }

    var cellIndex = 0
    Column(
        modifier = Modifier
            .requiredHeight(RecommendedSizeForAccessibility * MAX_CALENDAR_ROWS),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(MAX_CALENDAR_ROWS) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(DAYS_IN_WEEK) {
                    if (
                        cellIndex < daysFromStartOfWeekToFirstOfMonth ||
                        cellIndex >= (daysFromStartOfWeekToFirstOfMonth + numberOfDays)
                    ) {
                        // Empty cell
                        Spacer(
                            modifier = Modifier.requiredSize(
                                width = RecommendedSizeForAccessibility,
                                height = RecommendedSizeForAccessibility
                            )
                        )
                    } else {
                        val dayNumber = cellIndex - daysFromStartOfWeekToFirstOfMonth + 1
                        val date = displayedMonth.withDayOfMonth(dayNumber)
                        val isToday = date == today
                        val isSelected = selectedDates.contains(date)

                        val dayContentDescription =
                            if (isSelected) {
                                "Selected"
                            } else null

                        val formattedDateDescription =
                            dateFormatter.formatDate(
                                date = date,
                                locale = LocalPickerLocale.current,
                                decimalStyle = LocalPickerDecimalStyle.current,
                                forContentDescription = true
                            ) ?: ""

                        Day(
                            modifier = Modifier,
                            selected = isSelected,
                            onClick = {
                                val updated = if (isSelected) {
                                    selectedDates - date
                                } else {
                                    selectedDates + date
                                }
                                onDatesSelectionChange(updated)
                            },
                            animateChecked = isSelected,
                            enabled = remember(date, selectableDates) {
                                with(selectableDates) {
                                    isSelectableYear(date.year) && isSelectableDate(date)
                                }
                            },
                            today = isToday,
                            inRange = false, // not applicable for multi
                            description =
                                if (dayContentDescription != null) {
                                    "$dayContentDescription, $formattedDateDescription"
                                } else {
                                    formattedDateDescription
                                },
                            colors = colors,
                            dayNumber = dayNumber
                        )
                    }
                    cellIndex++
                }
            }
        }
    }
}








