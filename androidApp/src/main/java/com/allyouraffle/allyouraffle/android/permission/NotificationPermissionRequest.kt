package com.allyouraffle.allyouraffle.android.permission

import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun NotificationPermissionRequest() {
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 권한이 승인된 경우
        } else {

        }
    }

    val permission = android.Manifest.permission.POST_NOTIFICATIONS
    val isPermissionGranted = ContextCompat.checkSelfPermission(
        context, permission
    ) == PackageManager.PERMISSION_GRANTED

    if (!isPermissionGranted) {
        // 권한이 부여되지 않았다면 버튼을 통해 요청
        SideEffect {
            notificationPermissionLauncher.launch(permission)
        }
    }
}

@Composable
fun NotificationPermissionRequestAlways(onFinished: () -> Unit) {
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 권한이 승인된 경우
            Toast.makeText(context, "알림 권한이 승인되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            // 권한이 거부된 경우
            Toast.makeText(context, "알림 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    val permission = android.Manifest.permission.POST_NOTIFICATIONS
    val isPermissionGranted = ContextCompat.checkSelfPermission(
        context, permission
    ) == PackageManager.PERMISSION_GRANTED


    SideEffect {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        context.startActivity(intent)
        onFinished()
    }

}