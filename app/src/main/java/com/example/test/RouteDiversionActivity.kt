package com.example.test

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import com.example.test.models.Driver
import com.example.test.models.Route
import com.example.test.models.Trip
import com.google.android.material.internal.ContextUtils
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class RouteDiversionActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var id: String
    private lateinit var vehicleType: String
    private lateinit var plateNumber: String
    private lateinit var make: String
    private lateinit var model: String
    private lateinit var year: String
    private lateinit var vin: String
    private lateinit var fuelType: String
    private lateinit var imageUrl: String
    private var odometerReading: Double = 0.0
    private var driver: Driver? = null
    private var route: Route? = null
    private var availability: Boolean = true
    var tripsList: ArrayList<Trip> = ArrayList<Trip>()
    private lateinit var startTime: String
    private lateinit var endTime: String
    private lateinit var driverImageUrl: String
    private lateinit var driverFirstName: String
    private lateinit var driverLastName: String
    private lateinit var driverPhone: String
    private lateinit var routeStartPoint: String
    private lateinit var routeEndPoint: String
    lateinit var vehicleNameTv: TextView
    lateinit var driverLastNameTv: TextView
    lateinit var driverFirstNameTv: TextView
    lateinit var vehicleNumberTv: TextView
    lateinit var startPointTv: TextView
    lateinit var endPointTv: TextView
    lateinit var vehicleImage: ImageView
    lateinit var locateTv: TextView
    lateinit var callDriverTv: TextView
    private lateinit var operationArea:String
    private lateinit var seatCapacity:String
    private  var fuelConsumptio:Double = 0.0
    private  var  geofenceRadius:Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar?
        actionBar = supportActionBar
        // with color hash code as its parameter
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))
        // Set BackgroundDrawable
        // actionBar?.setElevation(0F);
        actionBar?.setBackgroundDrawable(colorDrawable)
        setContentView(R.layout.activity_route_diversion)
        init()
    }


    fun init() {
        val intent = intent
        id = intent.getStringExtra("vehicle_id").toString()
        vehicleType = intent.getStringExtra("vehicleType").toString()
        plateNumber = intent.getStringExtra("plateNumber").toString()
        make = intent.getStringExtra("make").toString()
        model = intent.getStringExtra("model").toString()
        year = intent.getStringExtra("year").toString()
        vin = intent.getStringExtra("vin").toString()
        fuelType = intent.getStringExtra("fuelType").toString()
        odometerReading = intent.getDoubleExtra("odometerReading", 0.0).toDouble()
        availability = intent.getStringExtra("availability").toBoolean()
        imageUrl = intent.getStringExtra("imageUrl").toString()
        driverImageUrl = intent.getStringExtra("driverImageUrl").toString()
        driverFirstName = intent.getStringExtra("driverFirstname").toString()
        driverLastName = intent.getStringExtra("driverLastname").toString()
        routeStartPoint = intent.getStringExtra("routeStartPoint").toString()
        routeEndPoint = intent.getStringExtra("routeEndPoint").toString()
        operationArea= intent.getStringExtra("operationArea").toString()
        geofenceRadius  = intent.getDoubleExtra("geofenceRadius", 0.0)
        vehicleNameTv = findViewById(R.id.vehicleNameTv)
        driverLastNameTv = findViewById(R.id.driverLastNameTv)
        driverFirstNameTv = findViewById(R.id.driverFirstNameTv)
        vehicleNumberTv = findViewById(R.id.vehicleNumberTv)
        startPointTv = findViewById(R.id.startPointTv)
        endPointTv = findViewById(R.id.endPointTv)
        vehicleImage = findViewById(R.id.vehicleIv)
        locateTv = findViewById(R.id.locateTv)
        callDriverTv = findViewById(R.id.callDriverTv)

        locateTv.setOnClickListener(this)
        callDriverTv.setOnClickListener(this)


        if (imageUrl != null) {
            Picasso.get()
                .load(imageUrl)
                .into(vehicleImage)
        } else {
            Picasso.get()
                .load(imageUrl)
                .placeholder(resources.getDrawable(R.drawable.default_van))//it will show placeholder image when url is not valid.
                .networkPolicy(NetworkPolicy.OFFLINE) //for caching the image url in case phone is offline
                .into(vehicleImage)
        }

        vehicleNameTv.text = vehicleType
        driverLastNameTv.text = driverLastName
        driverFirstNameTv.text = driverFirstName
        vehicleNumberTv.text = plateNumber
        startPointTv.text = routeStartPoint
        endPointTv.text = routeEndPoint
    }


    fun animate() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun callDriver() {
        val phone_intent = Intent(Intent.ACTION_CALL)
        phone_intent.data = Uri.parse("tel:+254"+driverPhone.drop(1))

        val PERMISSION_REQUEST_CODE = 100

        if (this.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CALL_PHONE
                )
            } == PackageManager.PERMISSION_GRANTED) {
            startActivity(phone_intent);
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.CALL_PHONE),
                PERMISSION_REQUEST_CODE
            );
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.locateTv -> {
                val i = Intent(this@RouteDiversionActivity, VehicleTracking2::class.java)
                intent.putExtra("vehicle_id", id)
                intent.putExtra("vehicleType", vehicleType)
                intent.putExtra("plateNumber", plateNumber)
                intent.putExtra("make", make)
                intent.putExtra("model", model)
                intent.putExtra("year", year)
                intent.putExtra("vin", vin)
                intent.putExtra("odometerReading", odometerReading)
                intent.putExtra("availability", availability)
                intent.putExtra("imageUrl", imageUrl)
                intent.putExtra("driverImageUrl", driver?.imageUrl)
                intent.putExtra("driverFirstname", driverFirstName)
                intent.putExtra("driverLastname", driverLastName)
                intent.putExtra("driverPhone", driverPhone)
                intent.putExtra("routeStartPoint", routeStartPoint)
                intent.putExtra("routeEndPoint", routeEndPoint)
                intent.putExtra("operationArea", operationArea)
                intent.putExtra("geofenceRadius", geofenceRadius)


                startActivity(i)
                animate()
            }
            R.id.callDriverTv -> {
                callDriver()
            }


        }
    }
}