package com.abdulrahman_b.hijridatepicker.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abdulrahman_b.hijridatepicker.HijriSelectableDates
import com.abdulrahman_b.hijridatepicker.R
import com.abdulrahman_b.hijridatepicker.toLocalString
import com.abdulrahman_b.hijridatepicker.tokens.DatePickerModalTokens
import com.abdulrahman_b.hijridatepicker.yearContainerColor
import com.abdulrahman_b.hijridatepicker.yearContentColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max

private const val YearsInRow: Int = 3
private val YearsVerticalPadding = 16.dp

@Composable
internal fun YearPickerMenuButton(
    onClick: () -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        colors = ButtonDefaults.textButtonColors(contentColor = LocalContentColor.current),
        elevation = null,
        border = null,
    ) {
        content()
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Icon(
            Icons.Filled.ArrowDropDown,
            contentDescription =
                if (expanded) {
                    stringResource(R.string.date_picker_switch_to_day_selection)
                } else {
                    stringResource(R.string.date_picker_switch_to_year_selection)
                },
            Modifier.rotate(if (expanded) 180f else 0f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun YearPicker(
    modifier: Modifier,
    currentYear: Int,
    displayedYear: Int,
    onYearSelected: (year: Int) -> Unit,
    selectableDates: HijriSelectableDates,
    yearRange: IntRange,
    colors: DatePickerColors
) {
    ProvideTextStyle(value = DatePickerModalTokens.SelectionYearLabelTextFont) {
        val lazyGridState = rememberLazyGridState(
            initialFirstVisibleItemIndex = max(0, displayedYear - yearRange.first - YearsInRow)
        )

        val containerColor = colors.containerColor
        val coroutineScope = rememberCoroutineScope()
        val scrollToEarlierYearsLabel = stringResource(R.string.date_picker_scroll_to_earlier_years)
        val scrollToLaterYearsLabel = stringResource(R.string.date_picker_scroll_to_later_years)

        // Keep grid attached at all times, use alpha to hide if needed
        LazyVerticalGrid(
            columns = GridCells.Fixed(YearsInRow),
            state = lazyGridState,
            modifier = modifier
                .background(containerColor)
                .alpha(1f), // always in composition to prevent unattached node crash
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(YearsVerticalPadding)
        ) {
            items(yearRange.count()) { index ->
                val yearEntry = index + yearRange.first
                val localizedYear = yearEntry.toLocalString()
                Year(
                    modifier = Modifier.requiredSize(
                        width = DatePickerModalTokens.SelectionYearContainerWidth,
                        height = DatePickerModalTokens.SelectionYearContainerHeight
                    ).semantics {
                        customActions =
                            if (
                                lazyGridState.firstVisibleItemIndex == index ||
                                lazyGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == index
                            ) {
                                customScrollActions(
                                    state = lazyGridState,
                                    coroutineScope = coroutineScope,
                                    scrollUpLabel = scrollToEarlierYearsLabel,
                                    scrollDownLabel = scrollToLaterYearsLabel
                                )
                            } else emptyList()
                    },
                    selected = yearEntry == displayedYear,
                    currentYear = yearEntry == currentYear,
                    onClick = { onYearSelected(yearEntry) },
                    enabled = selectableDates.isSelectableYear(yearEntry),
                    description =
                        stringResource(R.string.date_picker_navigate_to_year_description)
                            .format(localizedYear),
                    colors = colors
                ) {
                    Text(text = localizedYear, textAlign = TextAlign.Center)
                }
            }
        }

        // Optional: reset scroll safely when needed
        LaunchedEffect(displayedYear) {
            lazyGridState.scrollToItem(max(0, displayedYear - yearRange.first - YearsInRow))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Year(
    modifier: Modifier,
    selected: Boolean,
    currentYear: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    description: String,
    colors: DatePickerColors,
    content: @Composable () -> Unit
) {
    val border = remember(currentYear, selected) {
        if (currentYear && !selected) {
            BorderStroke(DatePickerModalTokens.DateTodayContainerOutlineWidth, colors.todayDateBorderColor)
        } else null
    }
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = modifier.semantics(mergeDescendants = true) {
            text = AnnotatedString(description)
            role = Role.Button
        },
        enabled = enabled,
        shape = DatePickerModalTokens.SelectionYearStateLayerShape,
        color = colors.yearContainerColor(selected = selected, enabled = enabled).value,
        contentColor = colors.yearContentColor(currentYear = currentYear, selected = selected, enabled = enabled).value,
        border = border,
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { content() }
    }
}

private fun customScrollActions(
    state: LazyGridState,
    coroutineScope: CoroutineScope,
    scrollUpLabel: String,
    scrollDownLabel: String
): List<CustomAccessibilityAction> {
    val scrollUpAction = {
        if (!state.canScrollBackward) false
        else {
            coroutineScope.launch { state.scrollToItem(state.firstVisibleItemIndex - YearsInRow) }
            true
        }
    }
    val scrollDownAction = {
        if (!state.canScrollForward) false
        else {
            coroutineScope.launch { state.scrollToItem(state.firstVisibleItemIndex + YearsInRow) }
            true
        }
    }
    return listOf(
        CustomAccessibilityAction(label = scrollUpLabel, action = scrollUpAction),
        CustomAccessibilityAction(label = scrollDownLabel, action = scrollDownAction)
    )
}
