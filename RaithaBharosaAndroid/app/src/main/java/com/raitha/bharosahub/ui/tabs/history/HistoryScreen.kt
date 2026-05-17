package com.raitha.bharosahub.ui.tabs.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.LocationOn
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
import com.raitha.bharosahub.data.local.SoilDataEntity
import com.raitha.bharosahub.ui.tabs.dashboard.getCropResId

fun formatHistoryDate(rawDate: String): String {
    return try {
        val ldt = java.time.LocalDateTime.parse(rawDate)
        val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm", java.util.Locale.ENGLISH)
        ldt.format(formatter)
    } catch (e: Exception) {
        try {
            if (rawDate.length >= 10) {
                val datePart = rawDate.take(10)
                val ld = java.time.LocalDate.parse(datePart)
                val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy", java.util.Locale.ENGLISH)
                ld.format(formatter)
            } else {
                rawDate
            }
        } catch (ex: Exception) {
            rawDate
        }
    }
}

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val history by viewModel.historyData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF1CA))
            .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List, 
                contentDescription = null, 
                tint = Color(0xFF2D4F2B), 
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                stringResource(R.string.history),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D4F2B)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    stringResource(R.string.no_history),
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(history) { item ->
                    HistoryCard(item)
                }
            }
        }
    }
}

@Composable
fun HistoryCard(item: SoilDataEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEAD8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Row: Crop Badge & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Crop Badge
                Box(
                    modifier = Modifier
                        .background(Color(0xFF67855C), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(getCropResId(item.crop)).uppercase(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Date & Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Color(0xFF67855C),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formatHistoryDate(item.date),
                        color = Color(0xFF67855C),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Middle section: Moisture and N-P-K columns
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Moisture Column
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Color(0xFF4A5D4E),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "MOISTURE",
                            color = Color(0xFF7A8B7B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${item.moisture}%",
                            color = Color(0xFF2E3D30),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // NPK Column
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = Color(0xFFE5A93C),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "N-P-K",
                            color = Color(0xFF7A8B7B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${item.nitrogen}-${item.phosphorus}-${item.potassium}",
                            color = Color(0xFF2E3D30),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Divider
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color(0xFFDCD8C0),
                thickness = 1.dp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Bottom section: Location & Plot size
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFFC62828),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${item.location.lowercase()} • ${item.plotSize.lowercase()}",
                    color = Color(0xFF5D6B59),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

