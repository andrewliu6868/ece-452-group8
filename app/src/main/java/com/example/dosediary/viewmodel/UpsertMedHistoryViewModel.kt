package com.example.dosediary.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dosediary.event.UpsertMedHistoryEvent
import com.example.dosediary.model.dao.MedicationHistoryDao
import com.example.dosediary.model.entity.Medication
import com.example.dosediary.model.entity.MedicationHistory
import com.example.dosediary.state.UpsertMedHistoryState
import com.example.dosediary.state.UpsertMedicationState
import com.example.dosediary.state.UserState
import com.example.dosediary.utils.DoseDiaryDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpsertMedHistoryViewModel @Inject constructor(
    private val userState: UserState,
    application: Application
) : ViewModel() {
    private val medicationHistoryDao: MedicationHistoryDao = DoseDiaryDatabase.getInstance(application).medicationHistoryDao
    private val _state = MutableStateFlow(UpsertMedHistoryState())
    val state = _state.asStateFlow()

    fun initialize(medication: MedicationHistory?) {
        viewModelScope.launch {
            val currentUser = userState.currentUser.value
            if (currentUser != null) {
                if (medication != null) {
                    _state.value = _state.value.copy(
                        id = medication.id,
                        name = medication.name,
                        timeTaken = medication.timeTaken,
                        dateTaken = medication.dateTaken,
                        effectiveness = medication.effectiveness,
                        ownerId = medication.ownerId,
                        additionalDetails = medication.additionalDetails
                    )
                } else {
                    _state.value = UpsertMedHistoryState(ownerId = currentUser.id)
                }
            }
        }
    }

    fun onEvent(event: UpsertMedHistoryEvent) {
        when (event) {
            is UpsertMedHistoryEvent.OnMedicationNameChanged -> {
                _state.value = _state.value.copy(name = event.name)
            }

            is UpsertMedHistoryEvent.OnEffectivenessChanged -> {
                _state.value = _state.value.copy(effectiveness = event.effectiveness)
            }

            is UpsertMedHistoryEvent.OnDateChanged -> {
                _state.value = _state.value.copy(dateTaken = event.date)
            }

            is UpsertMedHistoryEvent.OnTimeChanged -> {
                _state.value = _state.value.copy(timeTaken = event.time)
            }

            is UpsertMedHistoryEvent.OnAdditionalDetailsChanged -> {
                _state.value = _state.value.copy(additionalDetails = event.details)
            }

            is UpsertMedHistoryEvent.OnSaveClicked -> {
                _state.value = _state.value.copy(showConfirmDialog = true)
            }

            is UpsertMedHistoryEvent.OnConfirmClicked -> {
                saveMedicationHistory()
                _state.value = _state.value.copy(showConfirmDialog = false)
            }

            is UpsertMedHistoryEvent.OnConfirmDialogDismissed -> {
                _state.value = _state.value.copy(showConfirmDialog = false)
            }
        }
    }

    private fun saveMedicationHistory() {
        viewModelScope.launch {
            val medicationHistory = _state.value.toMedicationHistory()
            medicationHistoryDao.upsertMedicationHistory(medicationHistory)
            fetchMedicationHistories()
        }
    }

    private fun fetchMedicationHistories() {
        viewModelScope.launch {
            val currentUser = userState.currentUser.value
            if (currentUser != null) {
                medicationHistoryDao.getMedicationHistoriesByOwner(currentUser.id).collect { histories ->
                }
            }
        }
    }

    private fun UpsertMedHistoryState.toMedicationHistory(): MedicationHistory {
        return MedicationHistory(
            id = this.id,
            name = this.name,
            dateTaken = this.dateTaken,
            timeTaken = this.timeTaken,
            effectiveness = this.effectiveness,
            ownerId = this.ownerId,
            additionalDetails = this.additionalDetails
        )
    }
}

class UpsertMedHistoryViewModelFactory(private val application: Application, private val userState: UserState) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpsertMedHistoryViewModel::class.java)) {
            return UpsertMedHistoryViewModel(userState, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
