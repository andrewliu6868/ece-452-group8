@file:Suppress("UNUSED_EXPRESSION")

package com.example.dosediary.view

import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.dosediary.R
import com.example.dosediary.components.CustomTopAppBar
import com.example.dosediary.ui.theme.Primary
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMedication(navController: NavHostController) {
    val medicationName = remember { mutableStateOf("") }
    val effectivenessOptions = listOf("Effective", "Moderate", "Marginal", "Ineffective")
    val selectedEffectiveness = remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()
    calendar.set(2023, Calendar.MAY, 10, 10, 30)
    val date = remember { mutableStateOf(calendar.time)}
    val time = remember { mutableStateOf(calendar.time)}
    val text = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                header = "Add Medication History",
                showNavigationIcon = true,
                navController = navController,
                imageResId = R.drawable.icon,  // Customizable icon
                imageDescription = "App Icon"
            )
        }
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            MedicationNameField(medicationName)
            Spacer(modifier = Modifier.height(16.dp))
            EffectivenessDropdown(selectedEffectiveness, effectivenessOptions)
            Spacer(modifier = Modifier.height(16.dp))
            DateField(date)
            Spacer(modifier = Modifier.height(16.dp))
            TimeField(time)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(text)
            Spacer(modifier = Modifier.height(16.dp))
            ButtonRow(navController)
        }
    }
}


@Composable
fun MedicationNameField(medicationName: MutableState<String>) {
    OutlinedTextField(
        value = medicationName.value,
        onValueChange = { medicationName.value = it },
        label = { Text("Medication Name") },
        placeholder = {Text ("Medication Name")},
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EffectivenessDropdown(
    selectedEffectiveness: MutableState<String>,
    effectivenessOptions: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedEffectiveness.value,
            onValueChange = { },
            readOnly = true,
            label = { Text("Effectiveness") },
            placeholder = { Text("Select Effectiveness") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .clickable { expanded = !expanded }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            effectivenessOptions.forEach { effectiveness ->
                DropdownMenuItem(
                    text = { Text(effectiveness) },
                    onClick = {
                        selectedEffectiveness.value = effectiveness
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun DateField(date: MutableState<Date>) {
    val dateFormat = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()) }
    val calendar = Calendar.getInstance().apply { time = date.value }

    val context = LocalContext.current

    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(year, month, dayOfMonth)
                date.value = calendar.time
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }


    OutlinedTextField(
        value = dateFormat.format(date.value),
        onValueChange = { },
        label = { Text("Select Date") },
        placeholder = { Text("MM/DD/YYYY") },
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            datePickerDialog.show()
                        }
                    }
                }
            }
    )

}

@Composable
fun TimeField(time: MutableState<Date>) {
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val calendar = Calendar.getInstance()

    val context = LocalContext.current

    val timePickerDialog = remember {
        android.app.TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                time.value = calendar.time
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
    }

    OutlinedTextField(
        value = timeFormat.format(time.value),
        onValueChange = { },
        label = { Text("Select Time") },
        placeholder = { Text("HH:MM AM/PM") },
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            timePickerDialog.show()
                        }
                    }
                }
            }
    )
}

@Composable
fun TextField(text: MutableState<String>) {
    OutlinedTextField(
        value = text.value,
        onValueChange = { text.value = it },
        label = { Text("Other Information") },
        placeholder = { Text("Enter Additional Information") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ButtonRow(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = {navController.navigate("history")},
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            modifier = Modifier.weight(1f)
        ) {
            Text("Create")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = {navController.navigate("history")},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7676)),
            modifier = Modifier.weight(1f)
        ) {
                Text("Discard")
        }
    }
}


@Preview(showBackground =true, name = "EditMedication Preview")
@Composable
fun EditMedPreview(){
    val navController = rememberNavController()
    EditMedication(navController = navController);
}
