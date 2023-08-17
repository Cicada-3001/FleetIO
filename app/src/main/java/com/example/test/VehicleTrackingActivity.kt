package com.example.test

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.Driver
import com.example.test.models.Route
import com.example.test.models.Trip
import com.example.test.models.Vehicle
import com.example.test.repository.Repository
import com.example.test.util.ActiveVehicleRecyclerAdapter
import com.example.test.util.AnimationUtils
import com.example.test.util.Constants.Companion.userId
import com.example.test.util.MapUtils
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class VehicleTrackingActivity : AppCompatActivity(), View.OnClickListener, OnMapReadyCallback {
    private lateinit var moreTv: TextView
    private lateinit var vehicleName:TextView
    private lateinit var vehicleNumber:TextView
    private lateinit var vehicleImg: ImageView
    private lateinit var driverImg: ImageView
    private lateinit var viewModel: MainViewModel
    // on below line we are creating variables for
    private lateinit var vehicleRV: RecyclerView
    private lateinit var routeRV:RecyclerView

    private lateinit var backButtonImg: ImageView
    var vehiclesList: ArrayList<Vehicle> = ArrayList<Vehicle>()
    var  vehicleRecyclerAdapter: ActiveVehicleRecyclerAdapter = ActiveVehicleRecyclerAdapter(vehiclesList,0,this)
    private lateinit var mMap: GoogleMap
    // This property is only valid between onCreateView and
    // onDestroyView.
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var defaultLocation: LatLng

    private var vehicle: Vehicle? =null
    private lateinit var id: String
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
    private  var driver: Driver? = null
    private  var route: Route? = null
    private  var availability:Boolean = true
    var tripsList: ArrayList<Trip> = ArrayList<Trip>()
    private lateinit var startTime:String
    private lateinit var endTime:String
    private lateinit var driverImageUrl:String
    private lateinit var driverFirstName:String
    private lateinit var driverLastName:String
    private lateinit var routeStartPoint:String
    private lateinit var routeEndPoint:String

    //location
    /* private lateinit var  locationRequest: LocationRequest
    private lateinit var  locationCallback: LocationCallback
    private lateinit var  fusedLocationProviderClient: FusedLocationProviderClient

    //Online system
    private lateinit var onlineRef: DatabaseReference
    private lateinit var currentUser: DatabaseReference
    private lateinit var userLocationRef: DatabaseReference
    private lateinit var geoFire: GeoFire

    private val onlineValueEventListener= object: ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            Snackbar.make(mapFragment.requireView(), error.message, Snackbar.LENGTH_SHORT).show()
        }
        override fun onDataChange(snapshot: DataSnapshot) {
            if(snapshot.exists())
                currentUser.onDisconnect().removeValue()
        }
    }*/

    @RequiresApi(33)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_vehicle_tracking)
        moreTv = findViewById(R.id.moreTv)
        backButtonImg = findViewById(R.id.back_button_img)
        val intent = intent
        id= intent.getStringExtra("vehicle_id").toString()
        vehicleType= intent.getStringExtra("vehicleType").toString()
        plateNumber= intent.getStringExtra("plateNumber").toString()
        make= intent.getStringExtra("make").toString()
        model = intent.getStringExtra("model").toString()
        year = intent.getStringExtra("year").toString()
        vin = intent.getStringExtra("vin").toString()
        fuelType = intent.getStringExtra("fuelType").toString()
        odometerReading = intent.getDoubleExtra("odometerReading",0.0).toDouble()
        currentLocation= intent.getStringExtra("currentLocation").toString()
        availability= intent.getStringExtra("availability").toBoolean()
        imageUrl = intent.getStringExtra("imageUrl").toString()
        driverImageUrl = intent.getStringExtra("driverImageUrl").toString()
        driverFirstName = intent.getStringExtra("driverFirstname").toString()
        driverLastName = intent.getStringExtra("driverLastname").toString()
        routeStartPoint = intent.getStringExtra("routeStartPoint").toString()
        routeEndPoint = intent.getStringExtra("routeEndPoint").toString()

        driverImg = findViewById(R.id.profile_image)
        if(driverImageUrl != null){
            Picasso.get()
                .load(driverImageUrl)
                .into(driverImg)
        }else{
            Picasso.get()
                .load(driverImageUrl)
                .placeholder(this.resources.getDrawable(R.drawable.default_user))//it will show placeholder image when url is not valid.
                .networkPolicy(NetworkPolicy.OFFLINE) //for caching the image url in case phone is offline
                .into(driverImg)
        }

        vehicleName = findViewById(R.id.vehicleNameTv)
        vehicleImg =findViewById(R.id.vehImg)

        if(imageUrl != null){
            Picasso.get()
                .load(imageUrl)
                .into(vehicleImg)
        }else{
            Picasso.get()
                .load(imageUrl)
                .placeholder(this.resources.getDrawable(R.drawable.default_user))//it will show placeholder image when url is not valid.
                .networkPolicy(NetworkPolicy.OFFLINE) //for caching the image url in case phone is offline
                .into(vehicleImg)
        }
        vehicleNumber = findViewById(R.id.vehicleNoTv)

        vehicleName.text = vehicleType+ " "+ model
        vehicleNumber.text= plateNumber

        moreTv.setOnClickListener(this)
        backButtonImg.setOnClickListener(this)
        vehicleImg.setOnClickListener(this)
        // on below line we are setting adapter to our recycler view.
        vehicleRecyclerAdapter.notifyDataSetChanged()
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

    }


/*
    @SuppressLint("MissingPermission")
    private fun init() {
        onlineRef= FirebaseDatabase.getInstance().getReference().child("info/connected")
        userLocationRef= FirebaseDatabase.getInstance().getReference(Common.USER_LOCATION_REFERENCE)
        currentUser= FirebaseDatabase.getInstance().getReference(Common.USER_INFO_REFERENCE).child(
            FirebaseAuth.getInstance().currentUser!!.uid
        )
        geoFire= GeoFire(userLocationRef)

       // registerOnLineSystem()

        locationRequest= LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setFastestInterval(3000)
        locationRequest.interval=5000
        locationRequest.setSmallestDisplacement(10f)

        locationCallback= object: LocationCallback(){
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

                //update location
                geoFire.setLocation(
                    FirebaseAuth.getInstance().currentUser!!.uid,
                    GeoLocation(locationResult.lastLocation!!.latitude, locationResult.lastLocation!!.longitude)
                ){ key:String?, error: DatabaseError? ->
                    if(error != null)
                        Snackbar.make(mapFragment.requireView(), error.message, Snackbar.LENGTH_SHORT).show()
                    else
                        if (error != null) {
                            Snackbar.make(mapFragment.requireView(), "You are online", Snackbar.LENGTH_SHORT).show()
                        }
                }
            }
        }
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
    }

    override fun onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        geoFire.removeLocation(FirebaseAuth.getInstance().currentUser!!.uid)
        onlineRef.removeEventListener(onlineValueEventListener)
        super.onDestroy()
    }

    override fun onResume(){
        super.onResume()
        registerOnLineSystem()
    }

    private fun registerOnLineSystem(){
        onlineRef.addValueEventListener(
            onlineValueEventListener
        )
    }*/

    fun animate(){
        overridePendingTransition(R.anim.slide_in_right,
            R.anim.slide_out_left);
    }

    fun animate2(){
        overridePendingTransition(R.anim.slide_out_left,
            R.anim.slide_in_right);
    }

    fun showBottomSheet(){
        // on below line we are creating a new bottom sheet dialog.
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.active_vehicle_bottom_sheet, null)
        vehicleRV= view.findViewById(R.id.activeVehiclesRv)

        vehicleRV.layoutManager =  LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel= ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)

        // on below line we are initializing our list
        vehiclesList = ArrayList()
        viewModel.getVehicles()

        viewModel.vehiclesResponse.observe(this, Observer{
            if(it.isSuccessful){
                vehiclesList= it.body() as ArrayList<Vehicle>
                vehicleRecyclerAdapter = ActiveVehicleRecyclerAdapter(vehiclesList,0,this)
                vehicleRV.adapter = vehicleRecyclerAdapter
                vehicleRV.layoutManager =  LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

            }else{
                Toast.makeText(this,it.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
        })
        // closing of dialog box when clicking on the screen.
        dialog.setCancelable(true)
        dialog.behavior.maxHeight = 1000
        dialog.behavior.peekHeight = 1000
        // content view to our view.
        dialog.setContentView(view)
        // on below line we are calling
        // a show method to display a dialog.
        dialog.show()
    }

    private fun showDefaultLocationOnMap(latLng: LatLng) {
        moveCamera(latLng)
        animateCamera(latLng)
    }


    //move camera to the default location
    private fun moveCamera(latLng: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    //animate the zooming
    private fun animateCamera(latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(15.5f).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }


    private var grayPolyline: Polyline? = null
    private var blackPolyline: Polyline? = null
    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var movingCabMarker: Marker? = null
    private var previousLatLng: LatLng? = null
    private var currentLatLng: LatLng? = null

    private fun addCarMarkerAndGet(latLng: LatLng): Marker? {
        val bitmapDescriptor =
            BitmapDescriptorFactory.fromBitmap(MapUtils.getCarBitmap(this))
        return mMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }


    private fun updateCarLocation(latLng: LatLng) {
        if (movingCabMarker == null) {
            movingCabMarker = addCarMarkerAndGet(latLng)
        }
        if (previousLatLng == null) {
            currentLatLng = latLng
            previousLatLng = currentLatLng
            movingCabMarker?.position = currentLatLng as LatLng
            movingCabMarker?.setAnchor(0.5f, 0.5f)
            animateCamera(currentLatLng!!)
        } else {
            previousLatLng = currentLatLng
            currentLatLng = latLng
            val valueAnimator = AnimationUtils.carAnimator()
            valueAnimator.addUpdateListener { va ->
                if (currentLatLng != null && previousLatLng != null) {
                    val multiplier = va.animatedFraction
                    val nextLocation = LatLng(
                        multiplier * currentLatLng!!.latitude + (1 - multiplier) * previousLatLng!!.latitude,
                        multiplier * currentLatLng!!.longitude + (1 - multiplier) * previousLatLng!!.longitude
                    )
                    movingCabMarker?.position = nextLocation
                    movingCabMarker?.setAnchor(0.5f, 0.5f)
                    animateCamera(nextLocation)
                }
            }
            valueAnimator.start()
        }
    }


    private lateinit var handler: Handler
    private lateinit var runnable: Runnable


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showMovingCab(cabLatLngList: ArrayList<LatLng>) {
        handler = Handler()
        var index = 0
        runnable = Runnable {
            run {
                if (index < 10) {
                    updateCarLocation(cabLatLngList[index])
                    handler.postDelayed(runnable, 3000)
                    ++index
                } else {
                    handler.removeCallbacks(runnable)
                    Toast.makeText(this@VehicleTrackingActivity, "Trip Ends", Toast.LENGTH_LONG).show()

                    val current = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS")
                    val formatted = current.format(formatter)
//                    endTime = formatted

                  //  addTripInformation()
                    sendTripCompleteNofitication()
                }
            }
        }
        handler.postDelayed(runnable, 5000)
    }


    private fun showPath(latLngList: ArrayList<LatLng>) {
        val builder = LatLngBounds.Builder()
        for (latLng in latLngList) {
            builder.include(latLng)
        }
        // this is used to set the bound of the Map
        val bounds = builder.build()
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))

        val polylineOptions = PolylineOptions()
        polylineOptions.color(Color.GRAY)
        polylineOptions.width(5f)
        polylineOptions.addAll(latLngList)
        grayPolyline = mMap.addPolyline(polylineOptions)

        val blackPolylineOptions = PolylineOptions()
        blackPolylineOptions.color(Color.BLACK)
        blackPolylineOptions.width(5f)
        blackPolyline = mMap.addPolyline(blackPolylineOptions)
        originMarker = addOriginDestinationMarkerAndGet(latLngList[0])
        originMarker?.setAnchor(0.5f, 0.5f)
        destinationMarker = addOriginDestinationMarkerAndGet(latLngList[latLngList.size - 1])
        destinationMarker?.setAnchor(0.5f, 0.5f)
        val polylineAnimator = AnimationUtils.polylineAnimator()
        polylineAnimator.addUpdateListener { valueAnimator ->
            val percentValue = (valueAnimator.animatedValue as Int)
            val index = (grayPolyline?.points!!.size) * (percentValue / 100.0f).toInt()
            blackPolyline?.points = grayPolyline?.points!!.subList(0, index)
        }
        polylineAnimator.start()
    }

    private fun addOriginDestinationMarkerAndGet(latLng: LatLng): Marker? {
        val bitmapDescriptor =
            BitmapDescriptorFactory.fromBitmap(MapUtils.getOriginDestinationMarkerBitmap())
        return mMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        defaultLocation = LatLng(28.435350000000003, 77.11368)

        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS")
        val formatted = current.format(formatter)
        startTime = formatted
        showDefaultLocationOnMap(defaultLocation)

        Handler().postDelayed(Runnable {
            showPath(MapUtils.getListOfLocations())
            showMovingCab(MapUtils.getListOfLocations())
        }, 3000)

    }
/*
    private  fun addTripInformation(){
        val newTrip= Trip(userId!!,vehicle!!,startTime, endTime,)
        Log.d("add state", newTrip.toString())
        viewModel.addTrip(newTrip)
        viewModel.tripsResponse.observe(this, Observer{
            if(it.isSuccessful){
                tripsList= it.body() as ArrayList<Trip>
                Log.d("TRIP",tripsList.size.toString())
            }else{
                Toast.makeText(this,it.errorBody()!!.string(),Toast.LENGTH_LONG).show()
                Log.d("ERROR", it.errorBody()!!.string())
            }
        })
    }*/


    @SuppressLint("RemoteViewLayout")
    private fun sendTripCompleteNofitication(){
        // declaring variables
      var notificationManager: NotificationManager
      var notificationChannel: NotificationChannel
      var builder: Notification.Builder
      val channelId = "i.apps.notifications"
        val description = "Test notification"
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

         // pendingIntent is an intent for future use i.e after
        // the notification is clicked, this intent will come into action
        val intent = Intent(this, TripCompleteNotificationActivity::class.java)


        // intent passed here is to our afterNotification class
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // RemoteViews are used to use the content of
        // some different layout apart from the current activity layout
        val contentView = RemoteViews(packageName, R.layout.activity_trip_complete_notification)

        // checking if android version is greater than oreo(API 26) or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
        } else {

            builder = Notification.Builder(this)
                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234, builder.build())
    }

    override fun onClick(v: View?) {
        var i: Intent
        if (v != null) {
            when (v.id) {
                R.id.back_button_img -> {
                    i= Intent(this@VehicleTrackingActivity, VehiclesActivity::class.java)
                    startActivity(i)
                    animate()
                }
                R.id.moreTv-> {
                    showBottomSheet()
                }
                R.id.vehImg-> {
                    i= Intent(this@VehicleTrackingActivity, VehicleInfoActivity::class.java)
                    i.putExtra("vehicle_id", id)
                    i.putExtra("vehicleType",vehicleType)
                    i.putExtra("plateNumber", plateNumber)
                    i.putExtra("make", make)
                    i.putExtra("model", model)
                    i.putExtra("year", year)
                    i.putExtra("vin", vin)
                    i.putExtra("odometerReading",odometerReading)
                    i.putExtra("currentLocation", currentLocation)
                    i.putExtra("availability", availability)
                    i.putExtra("imageUrl", imageUrl)
                    i.putExtra("driverImageUrl", driverImageUrl)
                    i.putExtra("driverFirstname", driverFirstName)
                    i.putExtra("driverLastName",  driverLastName)
                    i.putExtra("routeStartPoint", routeStartPoint)
                    i.putExtra("routeEndPoint", routeEndPoint)
                    startActivity(i)
                    animate2()
                }

                else -> {
                    print("Error")
                }
            }
        }

    }

    fun passVehicleValues(intent: Intent){

    }

}

