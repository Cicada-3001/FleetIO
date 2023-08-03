package com.example.test

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.test.models.Driver
import com.example.test.models.Route
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.firebase.database.DatabaseError
import kotlin.random.Random

class GeofenceListener(val context:Context, intent:Intent): GeoQueryEventListener {
    var id = intent.getStringExtra("vehicle_id").toString()
    var vehicleType = intent.getStringExtra("vehicleType").toString()
    var plateNumber = intent.getStringExtra("plateNumber").toString()
    var make = intent.getStringExtra("make").toString()
    var model = intent.getStringExtra("model").toString()
    var year = intent.getStringExtra("year").toString()
    var vin = intent.getStringExtra("vin").toString()
    var fuelType = intent.getStringExtra("fuelType").toString()
    var odometerReading = intent.getDoubleExtra("odometerReading", 0.0).toDouble()
    var availability = intent.getStringExtra("availability").toBoolean()
    var imageUrl = intent.getStringExtra("imageUrl").toString()
    var driverImageUrl = intent.getStringExtra("driverImageUrl").toString()
    var driverFirstName = intent.getStringExtra("driverFirstname").toString()
    var driverLastName = intent.getStringExtra("driverLastname").toString()
    var driverPhone = intent.getStringExtra("driverPhone").toString()
    var routeStartPoint = intent.getStringExtra("routeStartPoint").toString()
    var routeEndPoint = intent.getStringExtra("routeEndPoint").toString()
    var operationArea= intent.getStringExtra("operationArea").toString()
    var geofenceRadius  = intent.getDoubleExtra("geofenceRadius", 0.0)
    var fuelConsumption = intent.getDoubleExtra("fuelConsumption", 0.0)




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onKeyEntered(key: String?, location: GeoLocation?) {
        Log.d("Operation Area", "True")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onKeyExited(key: String?) {
        sendNotification()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onKeyMoved(key: String?, location: GeoLocation?) {
        Log.d("Move in Area", "True")
    }

    override fun onGeoQueryReady() {
       Log.d("Geofence ready", "True")
    }


    override fun onGeoQueryError(error: DatabaseError?) {
       //Toast.makeText(this@GeofenceListener, error?.message, Toast.LENGTH_SHORT).show()
    }


    @SuppressLint("RemoteViewLayout")
    @RequiresApi(Build.VERSION_CODES.O)
    fun sendNotification() {
        val NOTIFICATION_CHANNEL_ID = "Out of Geofence"
        val notificationManager: NotificationManager = context.getSystemService(NotificationManager::class.java) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, "Geofence Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            // Config
            notificationChannel.description = "Channel description"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val contentIntent = Intent(context, RouteDiversionActivity::class.java)
        contentIntent.putExtra("make", make)
        contentIntent.putExtra("model", model)
        contentIntent.putExtra("year", year)
        contentIntent.putExtra("vin", vin)
        contentIntent.putExtra("vehicleType",vehicleType)
        contentIntent.putExtra("odometerReading", odometerReading)
        contentIntent.putExtra("availability", availability)
        contentIntent.putExtra("imageUrl", imageUrl)
        contentIntent.putExtra("driverImageUrl", driverImageUrl)
        contentIntent.putExtra("driverFirstname", driverFirstName)
        contentIntent.putExtra("driverLastName", driverLastName)
        contentIntent.putExtra("driverPhone", driverPhone)
        contentIntent.putExtra("routeStartPoint",routeStartPoint)
        contentIntent.putExtra("routeEndPoint", routeEndPoint)
        contentIntent.putExtra("operationArea", operationArea)
        contentIntent.putExtra("geofenceRadius", geofenceRadius)
        contentIntent.putExtra("fuelConsumption",fuelConsumption )
        contentIntent.putExtra("plateNumber", plateNumber)

        val pendingIntent = PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)


      //  val contentView = RemoteViews(context.packageName, R.layout.activity_route_diversion)

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Out of Geofence")
            .setContentText("Vehicle out of Geofence")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
          //  .setCustomContentView(contentView)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notification = builder.build()
        notificationManager.notify(Random.nextInt(), notification)


    }

}