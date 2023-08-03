package com.example.test


import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Resources
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.test.models.Driver
import com.example.test.models.Route
import com.example.test.models.Trip
import com.example.test.models.Vehicle
import com.example.test.repository.Repository
import com.example.test.util.createChannel
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseError
import java.io.IOException


private const val TAG = "MapsActivity"
private lateinit var geoClient: GeofencingClient
private  val REQUEST_TURN_DEVICE_LOCATION_ON =20
private val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 3
private val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 4
private val REQUEST_LOCATION_PERMISSION = 10

class VehicleGeofenceActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMapLongClickListener{
    private val gadgetQ = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    //location
    private lateinit var  locationRequest:LocationRequest
    private lateinit var  locationCallback:LocationCallback
    private lateinit var  fusedLocationProviderClient: FusedLocationProviderClient
    var vehiclesList: ArrayList<Vehicle> = ArrayList<Vehicle>()
    private lateinit var mMap: GoogleMap
    lateinit var searchView: SearchView
    private lateinit var addressEdt:EditText
    private lateinit var radiusEdt:EditText
    private lateinit var incRadiusBtn:Button
    private lateinit var addBtn: Button
    private lateinit var previewBtn: Button
    private lateinit var viewModel: MainViewModel
    private  lateinit var addBox:LinearLayout
    private var  radius:Int =0
    private lateinit var promptCard:CardView
    private lateinit var operationArea:String
    private  var  geofenceRadius:Double = 0.0
    private lateinit var  headingTv:TextView
    private lateinit var location:String
    private var geoRadius:Double= 0.0
    private lateinit var vehicleId:String

    var inserted:Boolean =  false

    // private lateinit var binding: VehicleGeofenceActivityBinding
    private val geofenceList = ArrayList<Geofence>()

    private val geofenceIntent: PendingIntent by lazy {
        val intent = Intent(this, GeoFenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    private lateinit var vehicleType: String
    private lateinit var plateNumber: String
    private lateinit var make:String
    private lateinit var model:String
    private lateinit var year:String
    private lateinit var vin:String
    private lateinit var fuelType:String
    private lateinit var imageUrl:String
    private var odometerReading:Double = 0.0
    private lateinit var currentLocation:String
    private lateinit var availability:String
    private lateinit var driverImageUrl:String
    private lateinit var driverFirstName:String
    private lateinit var driverLastName:String
    private lateinit var routeStartPoint:String
    private lateinit var routeEndPoint:String

    private var fuelConsumption:Double = 0.0
    private lateinit var seatCapacity:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_geofence)
        getSupportActionBar()?.hide();
        // initializing our search view.
        searchView = findViewById(R.id.searchViewStart)

        createChannel(this)
        geoClient = LocationServices.getGeofencingClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getActiveIntent()

        addressEdt = findViewById(R.id.addressEdt)
        radiusEdt  = findViewById(R.id.radiusEdt)
        incRadiusBtn = findViewById(R.id.addRadiusBtn)
        addBtn = findViewById(R.id.addFenceBtn)
        previewBtn = findViewById(R.id.previewFenceBtn)
        addBox = findViewById(R.id.addFenceBox)
        promptCard = findViewById(R.id.promptCard)
        headingTv =  findViewById(R.id.headingTv)

        previewBtn.setOnClickListener(this)
        addBtn.setOnClickListener(this)
        incRadiusBtn.setOnClickListener(this)


        locationRequest= LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setFastestInterval(15000)  //15 seconds
        locationRequest.interval= 10000  //10 seconds
        locationRequest.setSmallestDisplacement(50f) //50second

        val locationCallback = object: LocationCallback(){
            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                super.onLocationAvailability(locationAvailability)
            }

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val newPos = LatLng(
                    locationResult.lastLocation!!.latitude,
                    locationResult.lastLocation!!.longitude
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPos, 18f))
            }
        }
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this@VehicleGeofenceActivity)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel= ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)
        findViewById<ImageView>(R.id.backImg).setOnClickListener(this)

    }

    fun incrementRadiusValue(){
        radius= radiusEdt.text.toString().toInt()
        radius++
        radiusEdt.setText(radius)

    }

    override fun onMapReady(googleMap: GoogleMap) {
     mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this,
                    R.raw.expresso_maps_style))
            if(!success)
                Log.e("EDMT_ERROR", "style parsing error")
        }catch (e: Resources.NotFoundException){
            Log.e("EDMT_ERROR", e.message!!)
        }

     //   startLocation()
        if(geofenceRadius > 0.0 && operationArea != null ){
            markGeofence(operationArea, geofenceRadius)
            radiusEdt.setText(geofenceRadius.toString())
            addressEdt.setText(operationArea)
            headingTv.text= "Edit Geofence"

        }




    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) === PackageManager.PERMISSION_GRANTED
    }

    private fun startLocation() {
        if (isPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    //specify the geofence to monitor and the initial trigger
    private fun seekGeofencing(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }


    //adding a geofence

    /*
    private fun addGeofence(){
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        geoClient?.addGeofences(seekGeofencing(), geofenceIntent)?.run {
            addOnSuccessListener {
                Toast.makeText(this@VehicleGeofenceActivity, "Geofences added", Toast.LENGTH_SHORT).show()
            }
            addOnFailureListener {
                Toast.makeText(this@VehicleGeofenceActivity, "Failed to add geofences", Toast.LENGTH_SHORT).show()

            }
        }
    }

     */

    //removing a geofence
   private fun removeGeofence(){
        geoClient?.removeGeofences(geofenceIntent)?.run {
            addOnSuccessListener {
                Toast.makeText(this@VehicleGeofenceActivity, "Geofences removed", Toast.LENGTH_SHORT).show()

            }
            addOnFailureListener {
                Toast.makeText(this@VehicleGeofenceActivity, "Failed to remove geofences", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun examinePermisionAndinitiatGeofence() {
        if (authorizedLocation()) {
            validateGadgetAreaInitiateGeofence()
        } else {
            askLocationPermission()
        }
    }

    // check if background and foreground permissions are approved
    @TargetApi(29)
    private fun authorizedLocation(): Boolean {
        val formalizeForeground = (
                PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_FINE_LOCATION
                ))
        val formalizeBackground =
            if (gadgetQ) {
                PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            } else {
                true
            }
        return formalizeForeground && formalizeBackground
    }

    //requesting background and foreground permissions
    @TargetApi(29)
    private fun askLocationPermission() {
        if (authorizedLocation())
            return
        var grantingPermission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        val customResult = when {
            gadgetQ -> {
                grantingPermission += android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        }
        Log.d(TAG, "askLocationPermission: ")
        ActivityCompat.requestPermissions(
            this,
            grantingPermission,
            customResult
        )

    }

    private fun validateGadgetAreaInitiateGeofence(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val locationResponses =
            client.checkLocationSettings(builder.build())

        locationResponses.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    exception.startResolutionForResult(
                        this,
                        REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error geting location settings resolution: " + sendEx.message)
                }
            } else {
                Toast.makeText(this, "Enable your location", Toast.LENGTH_SHORT).show()
            }
        }
        locationResponses.addOnCompleteListener {
            if (it.isSuccessful) {
                //addGeofence()
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        validateGadgetAreaInitiateGeofence(false)
    }

    override fun onStart() {
        super.onStart()
        examinePermisionAndinitiatGeofence()
    }

    override fun onDestroy() {
        super.onDestroy()
        //removeGeofence()
    }

    fun markGeofence(operationArea:String,  geofenceRadius:Double) {
        var addressList: List<Address>? = null
        // checking if the entered location is null or not.
        if (operationArea != null || operationArea != "") {
            // on below line we are creating and initializing a geo coder.
            val geocoder = Geocoder(this@VehicleGeofenceActivity)
            try {
                // on below line we are getting location from the
                // location name and adding that location to address list.
                addressList = geocoder.getFromLocationName(operationArea, 1)

            } catch (e: IOException) {
                e.printStackTrace()
            }
            // on below line we are getting the location
            // from our list a first position.
            val address: Address = addressList!![0]
            // on below line we are creating a variable for our location
            // where we will add our locations latitude and longitude.
            mMap.clear()
            val latlng = LatLng(address.getLatitude(), address.getLongitude())
            var zoom = 10.0

            if(geofenceRadius > 40)
                zoom= 9.5
            if(geofenceRadius > 60)
                zoom= 9.0

            val circleOptions = CircleOptions()
                .center(latlng)
                .radius(geofenceRadius * 1000)
                .fillColor(0x30ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(4f)

            val INIT = CameraPosition.Builder()
                .target(latlng)
                .zoom(zoom.toFloat())
                .bearing(300f) // orientation
                .tilt(50f) // viewing angle
                .build()

            mMap.addCircle(circleOptions)
            val zoomLevel = 10f
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(INIT))
            mMap.addMarker(MarkerOptions().position(latlng).draggable(true))
        }else{
            Toast.makeText(this, "The address of the center point is missing", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.previewFenceBtn -> {
                    geoRadius= radiusEdt.text.toString().toDouble()
                    location = addressEdt.text.toString()
                    markGeofence(location, geoRadius)
                }

                R.id.addFenceBtn ->{
                    geoRadius= radiusEdt.text.toString().toDouble()
                    location = addressEdt.text.toString()
                    addGeofence(location, geoRadius)
                }

                R.id.addRadiusBtn -> {
                    incrementRadiusValue()
                }

                R.id.backImg -> {
                    val i = Intent(this@VehicleGeofenceActivity,VehicleInfoActivity::class.java)
                    intentPutExtra(i)
                    startActivity(i)
                }


            }
        }
    }

    fun intentPutExtra(i:Intent){
        i.putExtra("vehicle_id", vehicleId)
        i.putExtra("vehicleType",vehicleType)
        i.putExtra("plateNumber",plateNumber)
        i.putExtra("make", make)
        i.putExtra("model",model)
        i.putExtra("year", year)
        i.putExtra("vin", vin)
        i.putExtra("fuelType", fuelType)
        i.putExtra("odometerReading",odometerReading)
        i.putExtra("currentLocation",currentLocation)
        i.putExtra("availability",availability)
        i.putExtra("imageUrl",imageUrl)
        i.putExtra("driverImageUrl", driverImageUrl)
        i.putExtra("driverFirstname",driverFirstName)
        i.putExtra("driverLastname",driverLastName)
        i.putExtra("routeStartPoint", routeStartPoint)
        i.putExtra("routeEndPoint",routeEndPoint)
        i.putExtra("fuelType", fuelType)
        i.putExtra("operationArea", operationArea)
        i.putExtra("geofenceRadius", geofenceRadius)
        i.putExtra("seatCapacity", seatCapacity)
        i.putExtra("fuelConsumption", fuelConsumption)
    }


    fun getActiveIntent(){
        vehicleId = intent.getStringExtra("vehicle_id").toString()
        vehicleType= intent.getStringExtra("vehicleType").toString()
        plateNumber= intent.getStringExtra("plateNumber").toString()
        make= intent.getStringExtra("make").toString()
        model = intent.getStringExtra("model").toString()
        year = intent.getStringExtra("year").toString()
        vin = intent.getStringExtra("vin").toString()
        fuelType = intent.getStringExtra("fuelType").toString()
        odometerReading = intent.getDoubleExtra("odometerReading",0.0).toDouble()
        currentLocation= intent.getStringExtra("currentLocation").toString()
        availability= intent.getStringExtra("availability").toString()
        imageUrl = intent.getStringExtra("imageUrl").toString()
        driverImageUrl = intent.getStringExtra("driverImageUrl").toString()
        driverFirstName = intent.getStringExtra("driverFirstname").toString()
        driverLastName = intent.getStringExtra("driverLastname").toString()
        routeStartPoint = intent.getStringExtra("routeStartPoint").toString()
        routeEndPoint = intent.getStringExtra("routeEndPoint").toString()
        operationArea= intent.getStringExtra("operationArea").toString()
        geofenceRadius  = intent.getDoubleExtra("geofenceRadius", 0.0)
        fuelConsumption = intent.getDoubleExtra("fuelConsumption",0.0)
        seatCapacity = intent.getStringExtra("seatCapacity").toString()
        fuelType = intent.getStringExtra("fuelType").toString()
    }
    override fun onMapLongClick(p0: LatLng) {
        Toast.makeText(this@VehicleGeofenceActivity, "I have been long pressed", Toast.LENGTH_SHORT).show()
        if(addBox.visibility == GONE)
            addBox.visibility == VISIBLE
        else
            addBox.visibility == GONE

    }



    private fun addGeofence(operationArea:String, geofenceRadius:Double){
        showProgressDialog()
        viewModel.markGeofence(vehicleId, operationArea, geofenceRadius)
        viewModel.vehicleResponse2.observe(this, Observer{
            val vehicle:Vehicle
            if(it.isSuccessful){
                vehicle= it.body() as Vehicle
                Log.d("Marked Geofence", vehicle.toString())
                inserted = true
            }else{
                Toast.makeText(this,it.errorBody()?.string(),Toast.LENGTH_SHORT).show()
                Log.d("vehicle ADD ERROR", it.errorBody()?.string()!!)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun showProgressDialog() {
        // Creating a Linear Layout
        val linearPadding = 30
        val linear = LinearLayout(this)
        linear.orientation = LinearLayout.HORIZONTAL
        linear.setPadding(linearPadding, linearPadding, linearPadding, linearPadding)
        linear.gravity = Gravity.CENTER
        var linearParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        linearParam.gravity = Gravity.CENTER
        linear.layoutParams = linearParam

        // Creating a ProgressBar inside the layout
        val progressBar = ProgressBar(this)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, linearPadding, 0)
        progressBar.layoutParams = linearParam
        linearParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        linearParam.gravity = Gravity.CENTER

        // Creating a TextView inside the layout
        val tvText = TextView(this)
        tvText.text = "Updating ..."
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20f
        tvText.layoutParams = linearParam
        linear.addView(progressBar)
        linear.addView(tvText)

        // Setting the AlertDialog Builder view
        // as the Linear layout created above
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setView(linear)

        // Displaying the dialog
        val dialog=builder.create()
        dialog.show()

        Handler().postDelayed( {
            dialog.dismiss()
            if(inserted)
                Toast.makeText(applicationContext,"Geofence marked successfully!", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(applicationContext,"Something went wrong !", Toast.LENGTH_SHORT).show()

        }, 4000.toLong() )

        val window: Window? = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
            // Disabling screen touch to avoid exiting the Dialog
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }


}












































































































































































































































































