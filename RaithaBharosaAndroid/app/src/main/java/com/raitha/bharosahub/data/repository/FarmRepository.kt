package com.raitha.bharosahub.data.repository

import com.raitha.bharosahub.data.local.ProfileDataStore
import com.raitha.bharosahub.data.local.SoilDataDao
import com.raitha.bharosahub.data.local.SoilDataEntity
import com.raitha.bharosahub.ui.onboarding.UserProfile
import kotlinx.coroutines.flow.Flow

class FarmRepository(
    private val soilDataDao: SoilDataDao,
    private val profileDataStore: ProfileDataStore
) {
    val latestSoilData: Flow<SoilDataEntity?> = soilDataDao.getLatestSoilData()
    val allHistory: Flow<List<SoilDataEntity>> = soilDataDao.getAllSoilData()
    val userProfile: Flow<UserProfile?> = profileDataStore.userProfile

    suspend fun insertSoilData(data: SoilDataEntity) {
        soilDataDao.insert(data)
    }

    suspend fun saveProfile(profile: UserProfile) {
        profileDataStore.saveProfile(profile)
    }

    suspend fun clearProfile() {
        profileDataStore.clearProfile()
    }
}
