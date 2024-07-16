package com.example.dosediary.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface MedicationDao {
    @Upsert
    suspend fun upsertMedication(medication: Medication)

    @Delete
    suspend fun deleteMedication(medication: Medication)

    @Query("SELECT * FROM medication ORDER BY medicationName ASC")
    fun getMedicationOrderedByFirstName(): Flow<List<Medication>>

    @Query("SELECT * FROM medication WHERE owner = :owner ORDER BY medicationName ASC")
    fun getMedicationsByOwner(owner: String): Flow<List<Medication>>

    @Query("SELECT * FROM medication ORDER BY refillDays ASC")
    fun getMedicationByRefill(): Flow<List<Medication>>
    @Query("SELECT * FROM medication WHERE id = :medID ORDER BY medicationName ASC LIMIT 1")
    fun getMedicationByID(medID: Int): Flow<Medication>
}