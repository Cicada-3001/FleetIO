package com.example.test

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import com.example.test.models.AnimationModel
import com.example.test.models.VehicleGeoModel
import com.example.test.models.User
import com.example.test.models.Vehicle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import java.util.*
import kotlin.collections.HashSet

object Common {

    val driversSubscribe:MutableMap<String, AnimationModel> = HashMap<String, AnimationModel> ()
    val markerList: MutableMap<String, Marker> = HashMap<String, Marker>()
    val VEHICLE_INFO_REFERENCE: String = "VehicleInfo"
    val VEHICLE_LOCATION_REFERENCE:String = "VehicleLocation"
    val driversFound: MutableSet<VehicleGeoModel> = HashSet<VehicleGeoModel>()
    val VEHICLE_LOCATION_REFERENCES:String = "VehicleLocation" //same as server app
    val TOKEN_REFERENCE: String = "Token"
    val  USER_LOCATION_REFERENCE: String="UserLocation"
    var currentUser: User?=null
    const val USER_INFO_REFERENCE: String= "UserInfo"
    val NOTI_TITLE: String ="title"
    val NOTI_BODY: String ="body"
    var hideNotification:Boolean= false;

    val geofenceVehicle: Vehicle? = null


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

    fun buildName(name: String?, ): String? {
            return java.lang.StringBuilder(name).toString()
    }


    //GET BEARING
    fun getBearing(begin: LatLng, end: LatLng): Float {
        //You can copy this function by link at description
        val lat: Double = Math.abs(begin.latitude - end.latitude)
        val lng: Double = Math.abs(begin.longitude - end.longitude)
        if (begin.latitude < end.latitude && begin.longitude < end.longitude) return Math.toDegrees(
            Math.atan(lng / lat)
        )
            .toFloat() else if (begin.latitude >= end.latitude && begin.longitude < end.longitude) return (90 - Math.toDegrees(
            Math.atan(lng / lat)
        ) + 90).toFloat() else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude) return (Math.toDegrees(
            Math.atan(lng / lat)
        ) + 180).toFloat() else if (begin.latitude < end.latitude && begin.longitude >= end.longitude) return (90 - Math.toDegrees(
            Math.atan(lng / lat)
        ) + 270).toFloat()
        return (-1).toFloat()
    }

    //DECODE POLY
    fun decodePoly(encoded: String): ArrayList<LatLng>? {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = (encoded[index++] - 63).toInt()
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = (encoded[index++] - 63).toInt()
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }


}
