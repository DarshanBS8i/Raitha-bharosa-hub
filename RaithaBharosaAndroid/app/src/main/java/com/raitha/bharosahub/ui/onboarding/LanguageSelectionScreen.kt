package com.raitha.bharosahub.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LanguageSelectionScreen(currentLang: String, onLanguageSelected: (String) -> Unit) {
    var selectedLang by remember { mutableStateOf(currentLang) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF1CA)) // Cream background
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top and Middle Section (emoji, titles, language cards)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🌾",
                fontSize = 110.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Text(
                text = "Choose Your Language",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2D4F2B)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "ನಿಮ್ಮ ಭಾಷೆಯನ್ನು ಆಯ್ಕೆಮಾಡಿ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF5D6B59)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // English Card
                val isEnSelected = selectedLang == "en"
                LanguageCard(
                    title = "English",
                    subtitle = null,
                    isSelected = isEnSelected,
                    onClick = { selectedLang = "en" },
                    modifier = Modifier.weight(1f)
                )
                
                // Kannada Card
                val isKnSelected = selectedLang == "kn"
                LanguageCard(
                    title = "ಕನ್ನಡ",
                    subtitle = "Kannada",
                    isSelected = isKnSelected,
                    onClick = { selectedLang = "kn" },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Bottom Section (Continue Button)
        Button(
            onClick = { onLanguageSelected(selectedLang) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF233E21)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun LanguageCard(
    title: String,
    subtitle: String?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) Color(0xFF67855C) else Color(0xFFEDEAD8)
    val titleColor = if (isSelected) Color.White else Color(0xFF2D4F2B)
    val subtitleColor = if (isSelected) Color.White.copy(alpha = 0.8f) else Color(0xFF5D6B59)
    val borderStroke = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCD8C0))

    Card(
        modifier = modifier
            .height(140.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = borderStroke,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = subtitleColor
                )
            }
        }
    }
}

