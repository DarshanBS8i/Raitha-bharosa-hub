package com.raitha.bharosahub.ui.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raitha.bharosahub.R

@Composable
fun LanguageSelectionScreen(currentLang: String, onLanguageSelected: (String) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                stringResource(R.string.choose_language),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D4F2B)
            )
            Text(
                stringResource(R.string.choose_language_subtitle),
                fontSize = 16.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            LanguageCard("English", "en", currentLang == "en") { onLanguageSelected("en") }
            Spacer(modifier = Modifier.height(16.dp))
            LanguageCard("ಕನ್ನಡ (Kannada)", "kn", currentLang == "kn") { onLanguageSelected("kn") }
        }
    }
}

@Composable
fun LanguageCard(label: String, code: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFD4E2D4) else Color.White
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2D4F2B)) else null,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.padding(20.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = Color(0xFF2D4F2B))
        }
    }
}
