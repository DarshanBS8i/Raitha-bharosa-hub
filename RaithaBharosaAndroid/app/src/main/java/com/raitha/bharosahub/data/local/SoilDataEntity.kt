package com.raitha.bharosahub.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "soil_data")
data class SoilDataEntity(
    @PrimaryKey val id: String,
    val date: String,
    val crop: String,
    val moisture: Int,
    val nitrogen: Int,
    val phosphorus: Int,
    val potassium: Int,
    val location: String = "",
    val plotSize: String = "",
    val observations: String = ""
)
