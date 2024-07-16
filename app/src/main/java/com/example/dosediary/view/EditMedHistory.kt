@file:Suppress("UNUSED_EXPRESSION")

package com.example.dosediary.view

import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
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
import com.example.dosediary.components.TimePicker
import com.example.dosediary.components.DatePicker
import com.example.dosediary.ui.theme.Primary
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMedication(navController: NavHostController) {
    val medicationName = remember { mutableStateOf("Ibuprofen") }
    val effectivenessOptions = listOf("Effective", "Moderate", "Marginal", "Ineffective")
    val selectedEffectiveness = remember { mutableStateOf(effectivenessOptions[0]) }
    val calendar = Calendar.getInstance()
    calendar.set(2023, Calendar.MAY, 10, 10, 30)
    val date = remember { mutableStateOf(calendar.time)}
    val time = remember { mutableStateOf(calendar.time)}
    val text = remember { mutableStateOf("Enter Information") }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                header = "Edit Medication History",
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
            DatePicker(date, "Select Date")
            Spacer(modifier = Modifier.height(16.dp))
            TimePicker(time, "Select Time")
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
fun TextField(text: MutableState<String>) {
    OutlinedTextField(
        value = text.value,
        onValueChange = { text.value = it },
        label = { Text("Other Information") },
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
            Text("Save")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = {navController.navigate("history")},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7676)),
            modifier = Modifier.weight(1f)
        ) {
            Text("Delete")
        }
    }
}


@Preview(showBackground =true, name = "EditMedication Preview")
@Composable
fun EditMedPreview(){
    val navController = rememberNavController()
    EditMedication(navController = navController);
}
