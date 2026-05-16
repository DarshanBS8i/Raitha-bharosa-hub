package com.raitha.bharosahub.ui.onboarding

data class UserProfile(
    val name: String,
    val primaryCrop: String,
    val lang: String,
    val location: String = "",
    val plotSize: String = "1"
)
