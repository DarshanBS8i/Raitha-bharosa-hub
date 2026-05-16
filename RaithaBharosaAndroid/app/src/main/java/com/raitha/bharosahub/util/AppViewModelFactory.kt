package com.raitha.bharosahub.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raitha.bharosahub.data.remote.WeatherApi
import com.raitha.bharosahub.data.repository.FarmRepository
import com.raitha.bharosahub.ui.onboarding.OnboardingViewModel
import com.raitha.bharosahub.ui.tabs.dashboard.DashboardViewModel
import com.raitha.bharosahub.ui.tabs.history.HistoryViewModel
import com.raitha.bharosahub.ui.tabs.input.InputViewModel
import com.raitha.bharosahub.ui.tabs.plan.ActionPlanViewModel

class AppViewModelFactory(
    private val repository: FarmRepository,
    private val weatherApi: WeatherApi
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> DashboardViewModel(repository, weatherApi) as T
            modelClass.isAssignableFrom(InputViewModel::class.java) -> InputViewModel(repository) as T
            modelClass.isAssignableFrom(ActionPlanViewModel::class.java) -> ActionPlanViewModel(repository, weatherApi) as T
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> HistoryViewModel(repository) as T
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> OnboardingViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
