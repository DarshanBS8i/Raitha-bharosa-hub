package com.raitha.bharosahub.ui.tabs.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Info
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
fun ActionPlanScreen(viewModel: ActionPlanViewModel) {
    val advisories by viewModel.advisories.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF1CA))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CalendarMonth, null, tint = Color(0xFF2D4F2B), modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                stringResource(R.string.krishi_calendar),
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF2D4F2B),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(advisories) { item ->
                ActionPlanCard(item)
            }
        }
    }
}

@Composable
fun ActionPlanCard(item: AdvisoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5EAC3)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    val dayText = when(item.day) {
                        "Today" -> stringResource(R.string.weather_today)
                        "Tomorrow" -> stringResource(R.string.weather_tomorrow)
                        "Monday" -> stringResource(R.string.monday)
                        "Tuesday" -> stringResource(R.string.tuesday)
                        "Wednesday" -> stringResource(R.string.wednesday)
                        "Thursday" -> stringResource(R.string.thursday)
                        "Friday" -> stringResource(R.string.friday)
                        "Saturday" -> stringResource(R.string.saturday)
                        "Sunday" -> stringResource(R.string.sunday)
                        else -> item.day
                    }
                    Text(dayText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D4F2B))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WaterDrop, null, tint = Color(0xFF708A58), modifier = Modifier.size(14.dp))
                        Text(" ${item.rainProb}%", fontSize = 14.sp, color = Color.Gray)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    val tempColor = when {
                        item.temp > 30 -> Color(0xFFD32F2F) // Hot Red
                        item.temp < 25 -> Color(0xFF1976D2) // Cool Blue
                        else -> Color(0xFF2D4F2B) // Normal Green
                    }
                    Text("${item.temp}°C", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = tempColor)
                    Text(item.condition, fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.width(12.dp))
                val weatherIcon = when {
                    item.condition.contains("Rain", true) -> Icons.Default.WaterDrop
                    item.condition.contains("Storm", true) -> Icons.Default.Thunderstorm
                    item.condition.contains("Sunny", true) || item.condition.contains("Clear", true) -> Icons.Default.WbSunny
                    else -> Icons.Default.Cloud
                }
                Icon(weatherIcon, null, tint = Color(0xFF2D4F2B), modifier = Modifier.size(44.dp))
            }
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = when(item.advisoryKey) {
                    "advisory_heavy_rain" -> Color(0xFFFFEBEE)
                    "advisory_hot_weather" -> Color(0xFFFFF3E0)
                    else -> Color(0xFFF1F8E9)
                }
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (item.advisoryKey.contains("rain") || item.advisoryKey.contains("hot")) Icons.Default.Warning else Icons.Default.Info,
                        null, 
                        tint = Color(0xFF5D4037), 
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        stringResource(getPlanResId(item.advisoryKey)),
                        fontSize = 13.sp,
                        color = Color(0xFF5D4037),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

fun getPlanResId(key: String): Int {
    return when(key) {
        "advisory_heavy_rain"        -> R.string.advisory_heavy_rain
        "advisory_rain_warning"      -> R.string.advisory_rain_warning
        "advisory_cloudy_humid"      -> R.string.advisory_cloudy_humid
        "advisory_hot_weather"       -> R.string.advisory_hot_weather
        "advisory_favorable"         -> R.string.advisory_favorable
        "advisory_pest_check"        -> R.string.advisory_pest_check
        "advisory_fertilizer_window" -> R.string.advisory_fertilizer_window
        else                         -> R.string.advisory_normal_conditions
    }
}
