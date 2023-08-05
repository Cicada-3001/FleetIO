package com.example.spy
import Model.VehicleInfoModel
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build

object Common {
    const val BASE_URL = "http://192.168.0.14:8080/"
    val TOKEN_REFERENCE: String = "Token"
    val VEHICLE_LOCATION_REFERENCE: String="VehicleLocation"
    var currentUser: VehicleInfoModel?=null
    const val VEHICLE_INFO_REFERENCE: String= "VehicleInfo"
    val NOTI_TITLE: String ="title"
    val NOTI_BODY: String ="body"
    var vehicle_id: String = "64184636d8885827edcc9048"

    fun showNotification(context:Context, id:Int, title:String, body:String, intent: Intent?){
        var pendingIntent: PendingIntent? = null
        if(intent !=null){
            pendingIntent= PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT)
            val NOTIFICATION_CHANNEL_ID= "spy_kenya"
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "spy kenya",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel.description = "Spy Kenya"
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                notificationChannel.enableVibration(true)
                notificationManager.createNotificationChannel(notificationChannel)
                val builder = Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
                builder.setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(false)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setSmallIcon(R.drawable.ic_baseline_directions_car_24)
                if(pendingIntent != null)
                    builder.setContentIntent(pendingIntent)

                val notification= builder.build()
                notificationManager.notify(id,notification)
            }
        }
    }










}
