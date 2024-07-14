package com.example.dosediary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dosediary.model.DoseDiaryDatabase
import com.example.dosediary.model.Medication

import com.example.dosediary.ui.theme.DoseDiaryTheme
import com.example.dosediary.navigation.BottomNavigationBar
import com.example.dosediary.ui.theme.Background
import com.example.dosediary.view.AddMedicationPage
import com.example.dosediary.view.EditMedication
import com.example.dosediary.view.MedicationHistory
import com.example.dosediary.view.MedicationListScreen
import com.example.dosediary.view.Profile
import com.example.dosediary.view.MedicationRefillScreen
import com.example.dosediary.view.MedicationRefillDetailScreen
import com.example.dosediary.viewmodel.MedRefillViewModel
import com.example.dosediary.viewmodel.MedRefillViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class MainActivity : ComponentActivity() {

    private val medRefillViewModel by viewModels<MedRefillViewModel> {
        MedRefillViewModelFactory(application)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        lifecycleScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")

            val start_calendar = Calendar.getInstance()
            val end_calendar = Calendar.getInstance()
            start_calendar.add(Calendar.DAY_OF_YEAR, 2)
            end_calendar.add(Calendar.DAY_OF_YEAR, 30)


            val sampleMedications = listOf(
                Medication(
                    medicationName = "Medication 1",
                    startDate = dateFormat.parse(dateFormat.format(start_calendar.time)) ?: Date(),
                    endDate = dateFormat.parse(dateFormat.format(end_calendar.time)) ?: Date(),
                    refillDays = 10,
                    dosage = 1,
                    frequency = "Daily",
                    owner = "User 1"
                ),
                Medication(
                    medicationName = "Medication 2",
                    startDate = dateFormat.parse(dateFormat.format(start_calendar.time)) ?: Date(),
                    endDate = dateFormat.parse(dateFormat.format(start_calendar.time)) ?: Date(),
                    refillDays = 10,
                    dosage = 1,
                    frequency = "Daily",
                    owner = "User 1"
                ),
                Medication(
                    medicationName = "Medication 3",
                    startDate = dateFormat.parse(dateFormat.format(start_calendar.time)) ?: Date(),
                    endDate = dateFormat.parse(dateFormat.format(start_calendar.time)) ?: Date(),
                    refillDays = 10,
                    dosage = 1,
                    frequency = "Daily",
                    owner = "User 1"
                ),
            )

//            val medicationDao = DoseDiaryDatabase.getInstance(application).medicationDao
//
//            sampleMedications.forEach() {
//                medicationDao.upsertMedication(it)
//            }
        }

        setContent {
            DoseDiaryTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Background) {
                    HomeScreen(medRefillViewModel)
                }
            }
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(medRefillViewModel: MedRefillViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Navigation(navController, medRefillViewModel)
        }
    }
}

@Composable
fun Navigation (navController: NavHostController, medRefillViewModel: MedRefillViewModel){

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { MedicationListScreen(navController, medRefillViewModel)}
        composable("refill") { MedicationRefillScreen(navController, medRefillViewModel) }
        composable("history") { MedicationHistory(navController) }
        composable("profile") { Profile() }
        composable("Add Medication") { AddMedicationPage(navController) }
        composable("refillDetails") { MedicationRefillDetailScreen(navController) }
        composable("editMedication") { EditMedication(navController) }
    }
}

