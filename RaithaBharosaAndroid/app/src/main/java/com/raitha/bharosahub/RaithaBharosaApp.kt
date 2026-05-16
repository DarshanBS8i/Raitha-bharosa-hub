package com.raitha.bharosahub

import android.app.Application
import com.raitha.bharosahub.data.local.AppDatabase
import com.raitha.bharosahub.data.local.ProfileDataStore
import com.raitha.bharosahub.data.repository.FarmRepository

class RaithaBharosaApp : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { FarmRepository(database.soilDataDao(), ProfileDataStore(this)) }
}
