package com.raitha.bharosahub.ui.tabs.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raitha.bharosahub.R

@Composable
fun InputScreen(viewModel: InputViewModel) {
    val profile by viewModel.profile.collectAsState()
    
    // Local state for editable fields initialized from profile
    var editableCrop by remember(profile) { mutableStateOf(profile?.primaryCrop ?: "sugarcane") }
    var editableLocation by remember(profile) { mutableStateOf(profile?.location ?: "") }
    var editablePlotSize by remember(profile) { mutableStateOf(profile?.plotSize ?: "") }

    var moisture by remember { mutableStateOf(25f) }
    var nitrogen by remember { mutableStateOf(32f) }
    var phosphorus by remember { mutableStateOf(20f) }
    var potassium by remember { mutableStateOf(30f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF1CA))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Build, null, tint = Color(0xFF2D4F2B), modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.simulateData() },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D4F2B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(R.string.generate_simulated_data), color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        InputCard(title = stringResource(R.string.crop_details)) {
            Text(stringResource(R.string.select_crop), fontSize = 12.sp, color = Color.Gray)
            OutlinedTextField(
                value = editableCrop,
                onValueChange = { editableCrop = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        InputCard(title = stringResource(R.string.location_plot_size), icon = Icons.Default.LocationOn) {
            Text(stringResource(R.string.location), fontSize = 12.sp, color = Color.Gray)
            OutlinedTextField(
                value = editableLocation,
                onValueChange = { editableLocation = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.plot_size), fontSize = 12.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = editablePlotSize,
                        onValueChange = { editablePlotSize = it },
                        singleLine = true
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.unit), fontSize = 12.sp, color = Color.Gray)
                    OutlinedTextField(value = stringResource(R.string.acres), onValueChange = {}, readOnly = true)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("${stringResource(R.string.soil_moisture)}: ${moisture.toInt()}%", fontWeight = FontWeight.Bold)
        Slider(
            value = moisture,
            onValueChange = { moisture = it },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(thumbColor = Color(0xFF2D4F2B), activeTrackColor = Color(0xFF2D4F2B))
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(stringResource(R.string.soil_nutrients_npk), fontWeight = FontWeight.Bold)
        
        NPKSlider("${stringResource(R.string.nitrogen)}: ${nitrogen.toInt()}", nitrogen, { nitrogen = it })
        NPKSlider("${stringResource(R.string.phosphorus)}: ${phosphorus.toInt()}", phosphorus, { phosphorus = it })
        NPKSlider("${stringResource(R.string.potassium)}: ${potassium.toInt()}", potassium, { potassium = it })

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { 
                viewModel.saveManualData(
                    moisture = moisture.toInt(),
                    n = nitrogen.toInt(),
                    p = phosphorus.toInt(),
                    k = potassium.toInt(),
                    crop = editableCrop,
                    location = editableLocation,
                    plotSize = editablePlotSize
                )
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D4F2B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(R.string.save_data), style = MaterialTheme.typography.titleMedium, color = Color.White)
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun NPKSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, fontSize = 14.sp)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..250f,
            colors = SliderDefaults.colors(thumbColor = Color(0xFF708A58), activeTrackColor = Color(0xFF708A58))
        )
    }
}

@Composable
fun InputCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector? = null, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5EAC3)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF2D4F2B))
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
