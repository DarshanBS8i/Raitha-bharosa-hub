package com.raitha.bharosahub.ui.tabs.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
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

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val history by viewModel.historyData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF1CA))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.History, null, tint = Color(0xFF2D4F2B), modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                stringResource(R.string.history),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D4F2B)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    stringResource(R.string.no_history),
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5EAC3))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(getCropResId(item.crop)),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D4F2B),
                    fontSize = 16.sp
                )
                Text(
                    item.date.take(10),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("📍 ${item.location}", fontSize = 13.sp, color = Color(0xFF5D4037))
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricChip("💧 ${item.moisture}%", "Moisture")
                MetricChip("N:${item.nitrogen}", "Nitrogen")
                MetricChip("P:${item.phosphorus}", "Phosphorus")
                MetricChip("K:${item.potassium}", "Potassium")
            }
        }
    }
}

@Composable
fun MetricChip(value: String, label: String) {
    Surface(
        color = Color(0xFFD4E2D4),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D4F2B))
            Text(label, fontSize = 10.sp, color = Color.Gray)
        }
    }
}
