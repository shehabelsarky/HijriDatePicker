package com.abdulrahman_b.hijridatepicker.multidatepicker

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.abdulrahman_b.hijrahdatetime.extensions.HijrahDates
import com.abdulrahman_b.hijridatepicker.HijriDatePickerFormatter
import com.abdulrahman_b.hijridatepicker.HijriSelectableDates
import com.abdulrahman_b.hijridatepicker.LocalPickerDecimalStyle
import com.abdulrahman_b.hijridatepicker.LocalPickerFormatter
import com.abdulrahman_b.hijridatepicker.LocalPickerLocale
import com.abdulrahman_b.hijridatepicker.R
import java.time.chrono.HijrahDate

/**
 * Defaults for Hijri multi-date picker components.
 */
@OptIn(ExperimentalMaterial3Api::class)
object HijriMultiDatePickerDefaults {

    val YearRange = HijrahDates.MIN_YEAR..HijrahDates.MAX_YEAR

    val AllDates = object : HijriSelectableDates {}

    const val YEAR_MONTH_SKELETON: String = "yMMMM"
    const val YEAR_ABBR_MONTH_DAY_SKELETON: String = "yMMMMd"

    @Suppress("SpellCheckingInspection")
    const val YEAR_MONTH_WEEKDAY_DAY_SKELETON: String = "yMMMEEEd"

    private const val INPUT_DATE_SKELETON: String = "yyyy/MM/dd"
    private const val INPUT_DATE_DELIMITER: Char = '/'

    fun dateFormatter(
        headlineDateSkeleton: String = YEAR_MONTH_WEEKDAY_DAY_SKELETON,
    ): HijriDatePickerFormatter {
        return HijriDatePickerFormatter(
            yearMonthSelectionSkeleton = YEAR_MONTH_SKELETON,
            selectedDateSkeleton = headlineDateSkeleton,
            selectedDateDescriptionSkeleton = YEAR_ABBR_MONTH_DAY_SKELETON,
            inputDateSkeleton = INPUT_DATE_SKELETON,
            inputDateDelimiter = INPUT_DATE_DELIMITER
        )
    }

    /**
     * A default date picker title composable.
     */
    @Composable
    fun DatePickerTitle(displayMode: DisplayMode, modifier: Modifier = Modifier) {
        when (displayMode) {
            DisplayMode.Picker ->
                Text(text = stringResource(R.string.date_picker_title), modifier = modifier)

            DisplayMode.Input ->
                Text(text = stringResource(R.string.date_input_title), modifier = modifier)
        }
    }

    /**
     * Headline composable for multiple selected dates.
     *
     * If no selection, shows default text.
     * If one date, formats it.
     * If multiple, shows count (e.g. "3 dates selected").
     */

    @Composable
    fun DatePickerHeadline(
        selectedDates: List<HijrahDate>,
        displayMode: DisplayMode,
        modifier: Modifier = Modifier
    ) {
        val locale = LocalPickerLocale.current
        val decimalStyle = LocalPickerDecimalStyle.current
        val dateFormatter = LocalPickerFormatter.current

        val headlineDescription: String

        when {
            selectedDates.isEmpty() -> {
                val headlineText = when (displayMode) {
                    DisplayMode.Picker -> stringResource(R.string.date_picker_headline)
                    DisplayMode.Input -> stringResource(R.string.date_input_headline)
                    else -> ""
                }
                headlineDescription = when (displayMode) {
                    DisplayMode.Picker -> stringResource(R.string.date_picker_no_selection_description)
                    DisplayMode.Input -> stringResource(R.string.date_input_no_input_description)
                    else -> ""
                }

                Text(
                    text = headlineText,
                    modifier = modifier.semantics {
                        liveRegion = LiveRegionMode.Polite
                        contentDescription = headlineDescription
                    }
                )
            }

            else -> {
                // Format all selected dates as yyyy-MM-dd
                val formattedDates = selectedDates.map { date ->
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date)
                }

                headlineDescription = formattedDates.joinToString(", ")

                FlowRow(
                    modifier = modifier
                        .wrapContentHeight()
                        .semantics {
                            liveRegion = LiveRegionMode.Polite
                            contentDescription = headlineDescription
                        },
                    maxItemsInEachRow = Int.MAX_VALUE
                ) {
                    formattedDates.forEachIndexed { index, date ->
                        Text(
                            color = colorResource(R.color.dark_blue),
                            text = if (index == formattedDates.lastIndex) date else "$date, ",
                            fontSize = 14.sp,
                            overflow = TextOverflow.Clip
                        )
                    }
                }
            }
        }
    }
}
