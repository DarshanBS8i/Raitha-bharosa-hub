package com.raitha.bharosahub.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SoilDataDao {
    @Query("SELECT * FROM soil_data ORDER BY date DESC")
    fun getAllSoilData(): Flow<List<SoilDataEntity>>

    @Query("SELECT * FROM soil_data ORDER BY date DESC LIMIT 1")
    fun getLatestSoilData(): Flow<SoilDataEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(soilData: SoilDataEntity)
}
