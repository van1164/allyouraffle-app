package com.allyouraffle.allyouraffle.android

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = Color.White,
            onPrimary = Color(0xFF1D1D1D),
            secondary = Color(0xFF5FD855),
            onSecondary = Color.LightGray,
            tertiary = Color(0xFF60A5FA),
            onTertiary = Color.DarkGray,
            background = Color.Black,
            onBackground = Color.DarkGray
        )
    } else {
        lightColorScheme(
            primary = Color.Black,
            onPrimary = Color.White,
            secondary = Color(0xFF5FD855),
            onSecondary = Color.DarkGray,
            tertiary = Color(0xFF60A5FA),
            onTertiary = Color.LightGray,
            background = Color.White,
            onBackground = Color.White
        )
    }
    val myFontFamily = FontFamily(
        Font(R.font.fontdefault, FontWeight.Normal)
    )

    val typography = androidx.compose.material3.Typography(
        titleLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            color = Color.Black
        ),
        bodyLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = Color.Black
        ),
        bodyMedium = TextStyle(
            fontFamily = myFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Color.Black
        ),
        bodySmall = TextStyle(
            fontFamily = myFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = Color.Black
        ),
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )


    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
