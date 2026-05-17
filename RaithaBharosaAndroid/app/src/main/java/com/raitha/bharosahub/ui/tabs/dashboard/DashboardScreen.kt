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
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5EAC3)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(R.string.sowing_index),
                    color = Color(0xFF2D4F2B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = uiState.recommendation.index / 100f,
                        modifier = Modifier.size(180.dp),
                        strokeWidth = 12.dp,
                        color = Color(0xFF1B9D4E),
                        trackColor = Color(0xFFE0E0E0)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${uiState.recommendation.index}",
                            color = Color(0xFF2D4F2B),
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Light
                        )
                        Text(text = "%", color = Color(0xFF2D4F2B), fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                SuggestionChip(
                    onClick = {},
                    label = { 
                        val cropId = uiState.profile?.primaryCrop ?: "paddy"
                        Text(stringResource(getCropResId(cropId)), color = Color(0xFF2D4F2B)) 
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFD4E2D4))
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Large Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (uiState.recommendation.canSow) Color(0xFF708A58) else Color(0xFFD32F2F)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (uiState.recommendation.canSow) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        stringResource(if (uiState.recommendation.canSow) R.string.advisory_go else R.string.advisory_wait),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        stringResource(getResId(uiState.recommendation.messageKey)),
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Weather and Moisture Small Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SmallMetricCard(
                title = stringResource(R.string.weather),
                value = "${uiState.currentTemp}°C",
                subtitle = stringResource(R.string.weather_cloudy),
                icon = Icons.Default.Cloud,
                modifier = Modifier.weight(1f)
            )
            SmallMetricCard(
                title = stringResource(R.string.moisture),
                value = "${uiState.latestMoisture}%",
                subtitle = stringResource(R.string.current_field),
                icon = Icons.Default.WaterDrop,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Yield Suggestions Section
        Text(
            stringResource(R.string.yield_suggestions),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D4F2B)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Info, null, tint = Color(0xFF2D2D2D), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = uiState.yieldSuggestion,
                    fontSize = 14.sp,
                    color = Color(0xFF2D2D2D),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun SmallMetricCard(title: String, value: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5EAC3)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Icon(icon, null, tint = Color(0xFF2D4F2B), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Normal, color = Color(0xFF2D4F2B))
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
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
