package com.abdulrahman_b.hijridatepicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.abdulrahman_b.hijridatepicker.datepicker.HijriDatePicker
import com.abdulrahman_b.hijridatepicker.datepicker.rememberHijriDatePickerState
import com.abdulrahman_b.hijridatepicker.multidatepicker.HijriMultiDatePicker
import com.abdulrahman_b.hijridatepicker.multidatepicker.rememberHijriMultiDatePickerState
import com.abdulrahman_b.hijridatepicker.rangedatepicker.HijriDateRangePicker
import com.abdulrahman_b.hijridatepicker.rangedatepicker.rememberHijriDateRangePickerState
import com.abdulrahman_b.hijridatepicker.sample.R
import com.abdulrahman_b.hijridatepicker.ui.theme.HijriDatePickerTheme
import java.time.DayOfWeek
import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HijriDatePickerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DatePickerFormSample(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerFormSample(
    modifier: Modifier = Modifier
) {
    // ✅ define formatter once with Hijrah chronology
    val formatter = remember {
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
            .withChronology(HijrahChronology.INSTANCE)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        // --------------------------- Single Date Picker ---------------------------
        val selectableDates = HijriSelectableDatesImpl(
            disabledDaysOfWeek = setOf(DayOfWeek.FRIDAY),     // disable all Fridays
            disabledMonths = setOf(9),                        // disable Ramadan
            disabledYears = setOf(1445),                      // disable year 1445
            disabledDates = setOf(
                HijrahDate.of(1446, 1, 1),                    // 1 Muharram 1446
                HijrahDate.of(1446, 12, 10)                   // 10 Dhu al-Hijjah 1446
            )
        )

        val datePickerState = rememberHijriDatePickerState(
            yearRange = 1445..1449,
            selectableDates = selectableDates,
        )
        var selectDateDialogOpen by remember { mutableStateOf(false) }
        var selectedDate by remember { mutableStateOf("") }

        OutlinedTextField(
            value = selectedDate,
            onValueChange = { },
            readOnly = true,
            label = { Text(stringResource(R.string.select_date)) },
            modifier = Modifier.padding(horizontal = 16.dp),
            trailingIcon = {
                IconButton(onClick = { selectDateDialogOpen = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_event),
                        contentDescription = stringResource(R.string.select_date)
                    )
                }
            }
        )

        if (selectDateDialogOpen) {
            DatePickerDialog(
                onDismissRequest = { selectDateDialogOpen = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedDate = datePickerState.selectedDate
                                ?.let { formatter.format(it) } ?: ""
                            selectDateDialogOpen = false
                        }
                    ) { Text(stringResource(R.string.ok)) }
                },
            ) {
                HijriDatePicker(state = datePickerState)
            }
        }

        // --------------------------- Range Picker ---------------------------
        val dateRangePickerState = rememberHijriDateRangePickerState(
            yearRange = 1400..1500,
            selectableDates = selectableDates
        )
        var selectDateRangeDialogOpen by remember { mutableStateOf(false) }
        var selectedDateRange by remember { mutableStateOf("") }

        OutlinedTextField(
            value = selectedDateRange,
            onValueChange = { },
            readOnly = true,
            label = { Text(stringResource(R.string.select_date_range)) },
            modifier = Modifier.padding(horizontal = 16.dp),
            trailingIcon = {
                IconButton(onClick = { selectDateRangeDialogOpen = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_date_range),
                        contentDescription = stringResource(R.string.select_date_range)
                    )
                }
            }
        )

        if (selectDateRangeDialogOpen) {
            DatePickerDialog(
                onDismissRequest = { selectDateRangeDialogOpen = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val startDate = dateRangePickerState.selectedStartDate
                                ?.let { formatter.format(it) } ?: ""
                            val endDate = dateRangePickerState.selectedEndDate
                                ?.let { formatter.format(it) } ?: ""
                            selectedDateRange =
                                if (startDate.isNotEmpty() && endDate.isNotEmpty())
                                    "$startDate - $endDate"
                                else ""
                            selectDateRangeDialogOpen = false
                        }
                    ) { Text(stringResource(R.string.ok)) }
                },
            ) {
                HijriDateRangePicker(state = dateRangePickerState)
            }
        }

        // --------------------------- Multi Date Picker ---------------------------
        val multiDatePickerState = rememberHijriMultiDatePickerState(
            yearRange = 1445..1449,
            selectableDates = selectableDates
        )
        var selectMultiDateDialogOpen by remember { mutableStateOf(false) }
        var selectedMultiDates by remember { mutableStateOf("") }

        selectedMultiDates = multiDatePickerState.selectedDates
            .joinToString(" , ") { date -> formatter.format(date) }

        OutlinedTextField(
            value = selectedMultiDates,
            onValueChange = { },
            readOnly = true,
            label = { Text("Select multiple dates") },
            modifier = Modifier.padding(horizontal = 16.dp),
            trailingIcon = {
                IconButton(onClick = { selectMultiDateDialogOpen = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_event),
                        contentDescription = "Select multiple dates"
                    )
                }
            }
        )

        if (selectMultiDateDialogOpen) {
            DatePickerDialog(
                onDismissRequest = { selectMultiDateDialogOpen = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedMultiDates =
                                multiDatePickerState.selectedDates
                                    .joinToString(" , ") { date -> formatter.format(date) }
                            selectMultiDateDialogOpen = false
                        }
                    ) { Text(stringResource(R.string.ok)) }
                },
            ) {
                HijriMultiDatePicker(
                    state = multiDatePickerState
                )
            }
        }
    }
}
