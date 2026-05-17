package com.raitha.bharosahub.ui.tabs.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raitha.bharosahub.R

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF1CA)) // Cream background
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Sowing Index Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEAD8)),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.sowing_index).uppercase(),
                    color = Color(0xFF7A8B7B),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { uiState.recommendation.index / 100f },
                        modifier = Modifier.size(180.dp),
                        strokeWidth = 14.dp,
                        color = Color(0xFFFF9800), // Orange arc exactly like screenshot
                        trackColor = Color(0xFFE6E2C8) // Darker cream track
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${uiState.recommendation.index}",
                            color = Color(0xFF2D4F2B),
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = "%", 
                            color = Color(0xFF2D4F2B), 
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                SuggestionChip(
                    onClick = {},
                    label = { 
                        val cropId = uiState.profile?.primaryCrop ?: "paddy"
                        Text(
                            text = stringResource(getCropResId(cropId)), 
                            color = Color(0xFF2D4F2B),
                            fontWeight = FontWeight.Medium
                        ) 
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFD4E2D4)),
                    border = null,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // Status Card: Wait / Go Alert Panel
        val isGo = uiState.recommendation.canSow
        val alertBgColor = if (isGo) Color(0xFFE2EDE2) else Color(0xFFFCEEEF)
        val alertContentColor = if (isGo) Color(0xFF2D4F2B) else Color(0xFF6A1B1B)
        val alertIcon = if (isGo) Icons.Default.CheckCircle else Icons.Default.Warning

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = alertBgColor),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp), 
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = alertIcon,
                    contentDescription = null,
                    tint = alertContentColor,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(if (isGo) R.string.advisory_go else R.string.advisory_wait),
                        color = alertContentColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(getResId(uiState.recommendation.messageKey)),
                        color = alertContentColor.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Weather and Moisture Small Cards
        Row(
            modifier = Modifier.fillMaxWidth(), 
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val isHot = uiState.currentTemp > 25
            SmallMetricCard(
                title = stringResource(R.string.weather),
                value = "${uiState.currentTemp}°C",
                subtitle = stringResource(if (isHot) R.string.weather_sunny else R.string.weather_cloudy),
                icon = if (isHot) Icons.Default.WbSunny else Icons.Default.Cloud,
                iconColor = if (isHot) Color(0xFFE5A93C) else Color(0xFF78909C),
                modifier = Modifier.weight(1f)
            )
            SmallMetricCard(
                title = stringResource(R.string.moisture),
                value = "${uiState.latestMoisture}%",
                subtitle = stringResource(R.string.current_field),
                icon = Icons.Default.WaterDrop,
                iconColor = Color(0xFF4A5D4E),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Yield Suggestions Section
        Text(
            text = stringResource(R.string.yield_suggestions).uppercase(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7A8B7B)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEAD8)),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Info, 
                    contentDescription = null, 
                    tint = Color(0xFF67855C), 
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = uiState.yieldSuggestion,
                    fontSize = 14.sp,
                    color = Color(0xFF2E3D30),
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SmallMetricCard(
    title: String, 
    value: String, 
    subtitle: String, 
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    iconColor: Color,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEAD8)),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title.uppercase(), 
                fontSize = 11.sp, 
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7A8B7B)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = iconColor, 
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value, 
                fontSize = 24.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color(0xFF2E3D30)
            )
            Text(
                text = subtitle, 
                fontSize = 12.sp, 
                fontWeight = FontWeight.Medium,
                color = Color(0xFF7A8B7B)
            )
        }
    }
}

fun getResId(key: String): Int {
    return when(key) {
        "advisory_heavy_rain" -> R.string.advisory_heavy_rain
        "advisory_soil_too_wet" -> R.string.advisory_soil_too_wet
        "advisory_soil_too_dry" -> R.string.advisory_soil_too_dry
        "advisory_optimal_moisture" -> R.string.advisory_optimal_moisture
        "yield_npk_low" -> R.string.yield_npk_low
        "yield_high_moisture" -> R.string.yield_high_moisture
        "yield_low_moisture" -> R.string.yield_low_moisture
        "yield_weeding" -> R.string.yield_weeding
        else -> R.string.advisory_normal_conditions
    }
}

fun getCropResId(crop: String): Int {
    return when(crop.lowercase()) {
        "sugarcane" -> R.string.sugarcane
        "ragi" -> R.string.ragi
        "paddy" -> R.string.paddy
        else -> R.string.other_crop
    }
}

