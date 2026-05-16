package com.raitha.bharosahub.ui.tabs.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raitha.bharosahub.data.local.SoilDataEntity
import com.raitha.bharosahub.data.repository.FarmRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: FarmRepository) : ViewModel() {
    val historyData: StateFlow<List<SoilDataEntity>> = repository.allHistory
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
