package com.gigaworks.tech.calculator.compose.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gigaworks.tech.calculator.R

val GoogleSans = FontFamily(Font(R.font.google_sans))

private fun baseStyle(weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = GoogleSans,
    fontWeight = weight,
)

val CalculatorTypography = Typography(
    displayLarge = baseStyle().copy(fontSize = 57.sp),
    displayMedium = baseStyle().copy(fontSize = 45.sp),
    displaySmall = baseStyle().copy(fontSize = 36.sp),
    headlineLarge = baseStyle().copy(fontSize = 32.sp),
    headlineMedium = baseStyle().copy(fontSize = 28.sp),
    headlineSmall = baseStyle().copy(fontSize = 24.sp),
    titleLarge = baseStyle(FontWeight.Medium).copy(fontSize = 22.sp),
    titleMedium = baseStyle(FontWeight.Medium).copy(fontSize = 16.sp),
    titleSmall = baseStyle(FontWeight.Medium).copy(fontSize = 14.sp),
    bodyLarge = baseStyle().copy(fontSize = 16.sp),
    bodyMedium = baseStyle().copy(fontSize = 14.sp),
    bodySmall = baseStyle().copy(fontSize = 12.sp),
    labelLarge = baseStyle(FontWeight.Medium).copy(fontSize = 14.sp),
    labelMedium = baseStyle(FontWeight.Medium).copy(fontSize = 12.sp),
    labelSmall = baseStyle(FontWeight.Medium).copy(fontSize = 11.sp),
)
