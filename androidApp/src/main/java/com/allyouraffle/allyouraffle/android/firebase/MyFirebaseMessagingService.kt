package com.allyouraffle.allyouraffle.android.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.util.Log
import androidx.core.app.NotificationCompat
import com.allyouraffle.allyouraffle.android.MainActivity
import com.allyouraffle.allyouraffle.android.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


class MyFirebaseMessagingService : FirebaseMessagingService() {

    // 메시지 수신 시 호출되는 메서드
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM Message", "From: ${remoteMessage.from}")
        Log.d("FCM MM", remoteMessage.notification.toString())
        Log.d("FCM MM", remoteMessage.messageId.toString())
        Log.d("FCM MM", remoteMessage.messageType.toString())
        // 알림이 포함된 메시지를 수신할 때
        remoteMessage.notification?.let {
            var targetPage : String? =null
            var reward : String? = null
            remoteMessage.data.isNotEmpty().let {
                targetPage = remoteMessage.data["targetPage"]
                reward = remoteMessage.data["reward"]
            }
            sendNotification(it,targetPage,reward)
        }

        // 데이터 페이로드 처리

    }


    // 토큰이 변경되었을 때 호출
    override fun onNewToken(token: String) {
        Log.d("FCM Token", "Refreshed token: $token")
        // 새로운 토큰을 서버로 전송하는 로직을 추가할 수 있습니다.
    }

    private fun sendNotification(
        message: RemoteMessage.Notification,
        targetPage: String?,
        reward: String?
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_CLEAR_TOP)
            targetPage?.let{putExtra("notification",it)}
            reward?.let{putExtra("reward",it)}
        }


        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, "channelId")
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setSmallIcon(R.drawable.main_logo)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel("channelId", "CHANNEL_NAME", NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)
        manager.notify(Random.nextInt(), notificationBuilder.build())
    }

    private fun handleDataMessage(targetPage: String?) {
        targetPage?.run {

        }
        // 데이터 페이로드 처리 및 네비게이션 로직 추가
    }
}