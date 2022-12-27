package com.sanenchen.janeweather.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * @author sanenchen
 * @since 2022/12/27
 * @description 天气质量
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AQIBadge(AQI: Int, category: String?, modifier: Modifier) {
    Text("$category")
}