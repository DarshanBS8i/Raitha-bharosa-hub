package com.raitha.bharosahub.data.local

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random
import java.time.LocalDateTime

class DataGenerator {
    fun getSoilDataPulse(crop: String = "sugarcane", location: String = "Field Alpha"): Flow<SoilDataEntity> = flow {
        while (true) {
            val data = generateRandomSoilData(crop, location)
            emit(data)
            delay(1800000L) 
        }
    }

    fun generateRandomSoilData(crop: String = "sugarcane", location: String = "Field Alpha"): SoilDataEntity {
        return SoilDataEntity(
            id = System.currentTimeMillis().toString(),
            date = LocalDateTime.now().toString(),
            crop = crop,
            moisture = Random.nextInt(10, 81),
            nitrogen = Random.nextInt(30, 221),
            phosphorus = Random.nextInt(10, 81),
            potassium = Random.nextInt(20, 251),
            location = location,
            plotSize = "3 acres"
        )
    }
}
