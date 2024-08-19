package com.allyouraffle.allyouraffle.android.login

import android.telephony.PhoneNumberUtils
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.allyouraffle.allyouraffle.android.util.LogoutButton
import com.allyouraffle.allyouraffle.network.UserInfoResponse


@Composable
fun UserPhoneNumberView(navController: NavHostController, userInfoResponse: UserInfoResponse) {
    if (userInfoResponse.phoneNumber != null) {
        navController.navigate("main")
    } else {
        UserPhoneNumberMain(navController = navController, userInfoResponse = userInfoResponse)
    }
}

@Composable
fun UserPhoneNumberMain(navController: NavHostController, userInfoResponse: UserInfoResponse) {
    var phoneNumber by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "휴대폰 인증",
                fontSize = 40.sp,
                color = Color(0xFF1E88E5),
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "본인인증을 진행해주세요.",
                fontSize = 16.sp,
                color = Color(0xFF424242),
                modifier = Modifier.padding(bottom = 32.dp),
                textAlign = TextAlign.Center
            )

            PhoneField(phoneNumber,
                mask = "000-0000-0000",
                maskNumber = '0',
                onPhoneChanged = { phoneNumber = it })

            Button(
                onClick = {
                    //TODO: 인증 로직구현 해야함.
                    navController.navigate("main")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1E88E5)),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFF1E88E5))
            ) {
                Text(
                    text = "휴대폰 번호 인증하기",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            LogoutButton()
        }
    }
}

@Composable
fun PhoneField(
    phone: String,
    modifier: Modifier = Modifier,
    mask: String = "000 000 00 00",
    maskNumber: Char = '0',
    onPhoneChanged: (String) -> Unit
) {
    TextField(
        value = phone,
        onValueChange = { it ->
            onPhoneChanged(it.take(mask.count { it == maskNumber }))
        },
        label = {
            Text(text = "휴대폰 번호")
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        visualTransformation = PhoneVisualTransformation(mask, maskNumber),
        modifier = modifier.fillMaxWidth().padding(16.dp),
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
            focusedLabelColor = Color(0xFF424242),
            cursorColor = Color(0xFF424242)
        ),
        textStyle = TextStyle(fontSize = 20.sp)
    )
}

class PhoneVisualTransformation(val mask: String, val maskNumber: Char) : VisualTransformation {

    private val maxLength = mask.count { it == maskNumber }

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.length > maxLength) text.take(maxLength) else text

        val annotatedString = buildAnnotatedString {
            if (trimmed.isEmpty()) return@buildAnnotatedString

            var maskIndex = 0
            var textIndex = 0
            while (textIndex < trimmed.length && maskIndex < mask.length) {
                if (mask[maskIndex] != maskNumber) {
                    val nextDigitIndex = mask.indexOf(maskNumber, maskIndex)
                    append(mask.substring(maskIndex, nextDigitIndex))
                    maskIndex = nextDigitIndex
                }
                append(trimmed[textIndex++])
                maskIndex++
            }
        }

        return TransformedText(annotatedString, PhoneOffsetMapper(mask, maskNumber))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PhoneVisualTransformation) return false
        if (mask != other.mask) return false
        if (maskNumber != other.maskNumber) return false
        return true
    }

    override fun hashCode(): Int {
        return mask.hashCode()
    }
}

private class PhoneOffsetMapper(val mask: String, val numberChar: Char) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int {
        var noneDigitCount = 0
        var i = 0
        while (i < offset + noneDigitCount) {
            if (mask[i++] != numberChar) noneDigitCount++
        }
        return offset + noneDigitCount
    }

    override fun transformedToOriginal(offset: Int): Int =
        offset - mask.take(offset).count { it != numberChar }
}