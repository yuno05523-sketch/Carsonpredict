package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prediction_history")
data class HistoryEntity(
    @PrimaryKey
    val id: String,
    val date: String,
    val type: String, // "Combiné Cote 3", "Combiné Cote 4", "Combiné Cote 5", "Match Solo"
    val description: String,
    val selectionsSummary: String,
    val totalOdds: Double,
    val status: String, // "GAGNE", "PERDU", "EN_ATTENTE", "REPORTE"
    val netRoiPercentage: Double
)
