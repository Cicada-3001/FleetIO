package com.example.test

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.Remote.GoogleAPI
import com.example.test.Remote.RetrofitClient
import com.example.test.callback.FirebaseDriverInfoListener
import com.example.test.models.*
import com.example.test.repository.Repository
import com.example.test.util.ActiveVehicleRecyclerAdapter
import com.example.test.util.AnimationUtils
import com.example.test.util.Constants
import com.example.test.util.MapUtils
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.time.LocalDateTime
import kotlin.random.Random


class VehicleTracking2 : AppCompatActivity(), View.OnClickListener, OnMapReadyCallback, FirebaseDriverInfoListener,
    GeoQueryEventListener {
    private lateinit var moreTv: TextView
    private lateinit var vehicleName: TextView
    private lateinit var vehicleNumber: TextView
    private lateinit var vehicleImg: ImageView
    private lateinit var driverImg: ImageView
    private lateinit var viewModel: MainViewModel
    // on below line we are creating variables for
    private lateinit var vehicleRV: RecyclerView
    private lateinit var routeRV: RecyclerView
    private lateinit var backButtonImg: ImageView
    var vehiclesList: ArrayList<Vehicle> = ArrayList<Vehicle>()
    var  vehicleRecyclerAdapter: ActiveVehicleRecyclerAdapter = ActiveVehicleRecyclerAdapter(vehiclesList,this)

    private lateinit var id: String
    private lateinit var vehicleType: String
    private lateinit var plateNumber: String
    private lateinit var make:String
    private lateinit var model:String
    private lateinit var year:String
    private lateinit var vin:String
    private lateinit var fuelType:String
    private lateinit var imageUrl:String
    private lateinit var driverPhone:String
    private var odometerReading:Double = 0.0
    private  var driver: Driver? = null
    private  var route: Route? = null
    private lateinit var availability:String
    var tripsList: ArrayList<Trip> = ArrayList<Trip>()
    private var startTime:String? = null
    private  var endTime:String? = null
    private lateinit var driverImageUrl:String
    private lateinit var driverFirstName:String
    private lateinit var driverLastName:String
    private lateinit var routeStartPoint:String
    private lateinit var routeEndPoint:String
    private lateinit var operationArea:String
    private lateinit var seatCapacity:String
    private lateinit var vehCurrentLocation:String
    private  var fuelConsumption:Double = 0.0
    private  var  geofenceRadius:Double = 0.0
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    private var originMarker: Marker? = null


    //location
    private lateinit var  locationRequest: LocationRequest
    private lateinit var  locationCallback: LocationCallback
    private lateinit var  fusedLocationProviderClient: FusedLocationProviderClient

    //Online system
    private lateinit var currentUser: DatabaseReference
    private lateinit var copLocationRef: DatabaseReference
    private lateinit var geoFire: GeoFire

    var addressList: List<Address>? = null
    var tripStarted: Boolean =  false

    //Load Driver
    var distance = 1.0
    var LIMIT_RANGE = 10.0
    var previousLocation:Location? = null
    lateinit var currentLocation: Location
    var endPoints:ArrayList<LatLng> = ArrayList()

    //listener
    var firstTime  = true
    lateinit var firebaseDriverInfoListener: FirebaseDriverInfoListener
//    lateinit var firebaseFailedListener: FirebaseFailedListener

    var cityName = ""
    val compositeDisposable = CompositeDisposable()
    lateinit var googleAPI: GoogleAPI
    //Moving Marker
    var polylineList:List<LatLng>? = null
    var handler: Handler? = null
    var index: Int = 0
    var next: Int = 0
    var y:Float = 0.0f
    var lat: Double = 0.0
    var lng:Double = 0.0
    lateinit var start:LatLng
    lateinit var end:LatLng
    private var movingCabMarker: Marker? = null
    private var previousLatLng: LatLng? = null
    private var currentLatLng: LatLng? = null

    //Firebase Database Query
    var geoQueryModel: GeoQueryModel? = null
    var geoLocation: GeoLocation? = null
    var vehicleGeoModel:VehicleGeoModel? = null
    var newDriverLocation = Location("")
    private lateinit var driverLocationReference: DatabaseReference
    private val onlineValueEventListener= object: ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            Snackbar.make(mapFragment.requireView(), error.message, Snackbar.LENGTH_SHORT).show()
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            if(snapshot.exists())
                currentUser.onDisconnect().removeValue()

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_tracking)
        supportActionBar?.hide()
        moreTv = findViewById(R.id.moreTv)
        backButtonImg = findViewById(R.id.back_button_img)
        id = intent.getStringExtra("vehicle_id").toString()
        vehicleType = intent.getStringExtra("vehicleType").toString()
        plateNumber = intent.getStringExtra("plateNumber").toString()
        make = intent.getStringExtra("make").toString()
        model = intent.getStringExtra("model").toString()
        year = intent.getStringExtra("year").toString()
        vin = intent.getStringExtra("vin").toString()
        fuelType = intent.getStringExtra("fuelType").toString()
        odometerReading = intent.getDoubleExtra("odometerReading", 0.0)
        availability = intent.getStringExtra("availability").toString()
        imageUrl = intent.getStringExtra("imageUrl").toString()
        driverImageUrl = intent.getStringExtra("driverImageUrl").toString()
        driverFirstName = intent.getStringExtra("driverFirstname").toString()
        driverLastName = intent.getStringExtra("driverLastname").toString()
        driverPhone = intent.getStringExtra("driverPhone").toString()
        routeStartPoint = intent.getStringExtra("routeStartPoint").toString()
        routeEndPoint = intent.getStringExtra("routeEndPoint").toString()
        operationArea= intent.getStringExtra("operationArea").toString()
        geofenceRadius  = intent.getDoubleExtra("geofenceRadius", 0.0)
        fuelConsumption = intent.getDoubleExtra("fuelConsumption", 0.0)
        seatCapacity = intent.getIntExtra("seatCapacity",0).toString()
        vehCurrentLocation = intent.getStringExtra("currentLocation").toString()
      //  seatCapacity = intent.getDoubleExtra("seatCapacity").toDouble()
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel= ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)
    /*
        Log.d("fuel Consumption", fuelConsumption.toString())
        Log.d("availability", availability)
        Log.d("current Location", vehCurrentLocation)
        Log.d("seatCapacity", seatCapacity) */



        //  Common.geofenceVehicle= Vehicle(id,Constants.userId, vehicleType, plateNumber, make, model, year, vin, fuelType, odometerReading, currentLocation, availability, driver, route, fuelConsumption, imageUrl, seatCapacity =)
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



        Log.d("StartPoint", routeStartPoint)
        Log.d("Endpoint", routeEndPoint)
        Log.d("Driverlast name", driverLastName)
        val routePoints = ArrayList<String>()
        routePoints.add(routeStartPoint)
        routePoints.add(routeEndPoint)

        // where we will store the list of all address.
        var addressList: List<Address>? = null
        Log.d("routePoints", routeEndPoint)
        try{
            for(point:String in  routePoints) {
                // checking if the entered location is null or not.
                if (point != null || point != "") {
                    // on below line we are creating and initializing a geo coder.
                    endPoints.add(geoCodeArea(point))
                }else {
                    Toast.makeText(this@VehicleTracking2, "No route has been assigned to the vehicle please assign the route", Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e: Exception){
            Toast.makeText(this@VehicleTracking2, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        init()

    }

    fun  geoCodeArea(point:String):LatLng {
        val geocoder = Geocoder(this@VehicleTracking2)
        try {
            // location name and adding that location to address list.
            addressList = geocoder.getFromLocationName(point, 1)
            while (addressList?.size==0) {
                addressList = geocoder.getFromLocationName(point, 1);
            }
        } catch (e: IOException){
            e.printStackTrace()
        }
        // on below line we are getting the location
        // from our list a first position.
        val address: Address = addressList!![0]
        // on below line we are creating a variable for our location
        // where we will add our locations latitude and longitude.
        val latLng = LatLng(address.getLatitude(), address.getLongitude())
        return latLng
    }


    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }


    @SuppressLint("MissingPermission")
    private fun init() {
        googleAPI = RetrofitClient.instance!!.create(GoogleAPI::class.java)
        firebaseDriverInfoListener = this
        locationRequest= LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setFastestInterval(3000)
        locationRequest.interval=5000
        locationRequest.setSmallestDisplacement(10f)
        geoFire= GeoFire(FirebaseDatabase.getInstance().getReference(Common.VEHICLE_LOCATION_REFERENCE))

        val geofenceListener = GeofenceListener(this,intent)
        val geoquery = geoFire.queryAtLocation (GeoLocation(geoCodeArea(operationArea).latitude,geoCodeArea(operationArea).longitude),geofenceRadius)
        geoquery.addGeoQueryEventListener(geofenceListener);


        locationCallback = object: LocationCallback(){
            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                super.onLocationAvailability(locationAvailability)
            }

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val newPos = LatLng(
                    locationResult.lastLocation!!.latitude,
                    locationResult.lastLocation!!.longitude
                )

                val mDatabase= FirebaseDatabase.getInstance().getReference(Common.VEHICLE_LOCATION_REFERENCE)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPos, 15.5f))

                if(firstTime){
                    previousLocation = locationResult.lastLocation
                    currentLocation = locationResult.lastLocation!!
                    firstTime = false
                }else {
                    previousLocation = currentLocation
                    currentLocation = locationResult.lastLocation!!
                }

                loadAvailableDrivers()
            }
        }
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
        loadAvailableDrivers()
    }


    private fun loadAvailableDrivers(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, getString(R.string.permission_require), Toast.LENGTH_SHORT).show()
            return
        }

        //Getting the  device last location
        fusedLocationProviderClient.lastLocation.addOnFailureListener {
            Toast.makeText(this,it.message!!, Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            //load all drivers in city
           //   val geocoder = Geocoder(this, Locale.getDefault())
          //  var addressList: List<Address> = ArrayList()
            try{
                getDriver()
            }catch (e:IOException){
                Toast.makeText(this,getString(R.string.permission_require), Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun checkFenceOrTrip(latLng: Location){
        //Add Circle for dangerous area for (LatLng lating: dangerousArea)
        val geoquery = geoFire.queryAtLocation (GeoLocation (latLng.latitude, latLng.longitude),0.6 )
        geoquery.addGeoQueryEventListener(this);
    }

    private fun getDriver(){
        //findDriverByKey()
        driverLocationReference.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //have new driver
                if(!snapshot.exists())
                    Toast.makeText(this@VehicleTracking2, "Snapshot does not exist", Toast.LENGTH_SHORT).show()
                updateLocation(snapshot)
                /*
                val newDistance = it.distanceTo(newDriverLocation)/1000
              //  if(newDistance  <= LIMIT_RANGE)zz
                    findDriverByKey(driverGeoModel) */
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //have new driver
                updateLocation(snapshot)

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                updateLocation(snapshot)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VehicleTracking2, error.toString(),Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun updateLocation(snapshot:DataSnapshot){
        if(!snapshot.exists())
            Toast.makeText(this@VehicleTracking2, "The data snapshot does not exist", Toast.LENGTH_LONG).show()
        Log.d("Snapshot", snapshot.value.toString())

        geoFire.getLocation(id, object : com.firebase.geofire.LocationCallback {
            override fun onLocationResult(key: String?, location: GeoLocation?) {
                if (location != null) {
                    vehicleGeoModel = VehicleGeoModel(key!!, location)
                    Common.driversFound.add(vehicleGeoModel!!)
                    newDriverLocation = Location("")
                    newDriverLocation.latitude = location!!.latitude
                    newDriverLocation.longitude = location!!.longitude

                    updateCarLocation(LatLng(newDriverLocation.latitude, newDriverLocation.longitude))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                System.err.println("There was an error getting the GeoFire location: $databaseError")
            }
        })

    }


    private fun animateCamera(latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(15.5f).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
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

    private fun addCarMarkerAndGet(latLng: LatLng): Marker? {
        val bitmapDescriptor =
            BitmapDescriptorFactory.fromBitmap(MapUtils.getCarBitmap(this))
        return mMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }



    private fun findDriverByKey(){
        FirebaseDatabase.getInstance()
            .getReference(Common.VEHICLE_INFO_REFERENCE)
            .child(id)
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                //    firebaseFailedListener.onFirebaseFailed(error.message)
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        vehicleGeoModel?.vehicleInfoModel=(snapshot.getValue(VehicleInfoModel::class.java))
                        Log.d("Model", vehicleGeoModel?.vehicleInfoModel?.vehicleName!!)
                        firebaseDriverInfoListener.onDriverInfoLoadSuccess(vehicleGeoModel!!)
                    }else{
                        Toast.makeText(this@VehicleTracking2, "Snapshot does not exist", Toast.LENGTH_SHORT).show()
                    }
                //firebaseFailedListener.onFirebaseFailed(getString(R.string.key_not_found)+ driverGeoModel!!.key)
                }
            }
            )
    }


    override fun onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    override fun onResume(){
        super.onResume()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //Request access location permission
        Dexter
            .withContext(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object: PermissionListener {
                @SuppressLint("MissingPermission")
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    driverLocationReference = FirebaseDatabase.getInstance()
                        .getReference(Common.VEHICLE_LOCATION_REFERENCE)
                        .child(id)
                    mMap.isMyLocationEnabled =true
                    mMap.uiSettings.isMyLocationButtonEnabled=true
                    mMap.setOnMyLocationClickListener {
                    fusedLocationProviderClient.lastLocation
                            .addOnFailureListener {
                                if (p0 != null) {
                                    Toast.makeText(this@VehicleTracking2,"Permission"+p0.permissionName+ "was denied", Toast.LENGTH_SHORT).show()
                                }
                            }.addOnSuccessListener {
                                val userLatLang= LatLng(it.latitude, it.longitude)
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLang,15.5f))
                            }
                    }
                    val locationButton = (mapFragment.requireView()
                        .findViewById<View>("1".toInt())!!
                        .parent!! as View).findViewById<View>("2".toInt())
                    val params =locationButton.layoutParams as RelativeLayout.LayoutParams
                    params.addRule(RelativeLayout.ALIGN_TOP,0)
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                    params.bottomMargin = 250 // move to see zoom control

                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    if (p0 != null) {
                        Toast.makeText(this@VehicleTracking2,"Permission"+p0.permissionName+ "was denied", Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {

                }
            }).check()


        for(latLng:LatLng in  endPoints){
            Log.d("EndPoints", endPoints.toString())
            mMap.addCircle (
                CircleOptions()
                    .center(latLng)
                    .radius(80.0)
                    .fillColor(0x30ff0000)
                    .strokeColor(Color.TRANSPARENT)
                    .strokeWidth(4f)
            )
            originMarker = addOriginDestinationMarkerAndGet(latLng)
            originMarker?.setAnchor(0.5f, 0.5f)
            val geoquery = geoFire.queryAtLocation (GeoLocation (latLng.latitude, latLng.longitude),0.08 )
            geoquery.addGeoQueryEventListener(this);
        }



        mMap.uiSettings.isZoomControlsEnabled = true

        try {
            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.expresso_maps_style))
            if(!success)
                Log.e("EDMT_ERROR", "style parsing error")
        }catch (e: Resources.NotFoundException){
            Log.e("EDMT_ERROR", e.message!!)
        }

    }



    private fun addOriginDestinationMarkerAndGet(latLng: LatLng): Marker? {
        val bitmapDescriptor =
            BitmapDescriptorFactory.fromBitmap(MapUtils.getOriginDestinationMarkerBitmap())
        return mMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    override fun onDriverInfoLoadSuccess(driverGeoModel: VehicleGeoModel) {
        //if already have marker with this key doesn't set it again
        if(!Common.markerList.containsKey(driverGeoModel.key)){
            var driverLatLang= LatLng(driverGeoModel.geoLocation!!.latitude,
                driverGeoModel.geoLocation!!.longitude)
            Common.markerList.put(driverGeoModel!!.key!!,
                mMap.addMarker(MarkerOptions()
                    .position(driverLatLang)
                    .flat(true)
                    .title(Common.buildName(driverGeoModel.vehicleInfoModel!!.vehicleName))
                    .snippet(driverGeoModel.vehicleInfoModel?.plateNumber)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                )!!
            )
         //   mMap.clear()
            mMap.addMarker(MarkerOptions()
                .position(driverLatLang)
                .flat(true)
                .title(Common.buildName(driverGeoModel.vehicleInfoModel!!.vehicleName))
                .snippet(driverGeoModel.vehicleInfoModel?.plateNumber)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
            )!!

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverLatLang,15.5F))
        }

        if(true) {
            val driverLocation = FirebaseDatabase.getInstance()
                .getReference(Common.VEHICLE_LOCATION_REFERENCES)
                .child(id)
            driverLocation.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.hasChildren()) {
                            if (Common.markerList.get(driverGeoModel.key) != null) {
                                val marker = Common.markerList.get(driverGeoModel.key)
                                marker!!.remove()
                                Common.markerList.remove(driverGeoModel.key)
                                Common.driversSubscribe.remove(driverGeoModel.key)
                                driverLocation.removeEventListener(this)

                            }
                        }else{
                            if(Common.markerList.get(driverGeoModel!!.key!!) != null)
                            {
                                val geoQueryModel = snapshot.getValue (GeoQueryModel::class.java)
                                val animationModel = AnimationModel(false, geoQueryModel!!)
                                if(Common.driversSubscribe.get(driverGeoModel.key!!) != null )
                                {
                                    val marker =  Common.markerList.get(driverGeoModel!!.key!!)
                                    val oldPosition = Common.driversSubscribe.get(driverGeoModel.key!!)
                                    val from = StringBuilder()
                                    .append(oldPosition?.geoQueryModel!!.l?.get(0))
                                    .append(",")
                                    .append(oldPosition.geoQueryModel!!.l?.get(1)).toString()

                                    val to = StringBuilder()
                                    .append(animationModel.geoQueryModel!!.l?.get(0))
                                    .append(",")
                                    .append(oldPosition.geoQueryModel!!.l?.get(1)).toString()

                                    moveMarkerAnimation(driverGeoModel.key!!, animationModel, marker, from, to)
                                }else
                                    Common.driversSubscribe.put(driverGeoModel.key!!, animationModel) //First location init
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@VehicleTracking2, error.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            )
        }
    }

    private fun moveMarkerAnimation(key: String, animationModel: AnimationModel, marker: Marker?, from: String?, to: String?) {
        if(animationModel.isRun) {
            //Request API
            compositeDisposable.add(
                googleAPI.getDirections( "driving",
                    "less driving",
                    from, to,
                    getString(R.string.google_api_key))
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn (AndroidSchedulers.mainThread())
                !!.subscribe{
                Log.d( "API_RETURN", it)
                try{
                    val jsonObject = JSONObject(it)
                    val jsonArray = jsonObject.getJSONArray("routes")

                    for(i in 0 until jsonArray.length()) {
                        val route = jsonArray.getJSONObject(i)
                        val poly = route.getJSONObject("overview_polyline")
                        val polyline = poly.getString("points")
                        polylineList = Common.decodePoly(polyline)
                    }

                    //Moving
                    handler = Handler()
                    index= -1
                    next = 1
                    val runnable = object: Runnable{
                        override fun run() {
                            if(polylineList!!.size > 1){
                                if(index < polylineList!!.size - 2){
                                    index++
                                    next
                                    index+1
                                    start= polylineList!![index]
                                    end =  polylineList!![next]
                                }
                                val valueAnimator  =  ValueAnimator.ofInt( 0,1)
                                valueAnimator.duration = 3000
                                valueAnimator.interpolator = LinearInterpolator()
                                valueAnimator.addUpdateListener {
                                    y = it.animatedFraction
                                    lat = y*end.latitude  +(1 - y) * start.latitude
                                    lng = y*end.longitude +(1 - y) * start.longitude
                                    val newPos = LatLng (lat, lng)
                                    marker!!.position = newPos
                                    marker!!.setAnchor(0.5f, 0.5f)
                                    marker.rotation = Common.getBearing (start, newPos)
                                }
                                valueAnimator.start()
                                if(index < polylineList!!.size - 2)
                                    handler!!.postDelayed(this,  1500)
                                else if(index < polylineList!!.size - 1) {
                                    animationModel.isRun = false
                                    Common.driversSubscribe.put(key, animationModel) // Update
                                }
                            }
                        }
                    }
                    handler!!.postDelayed(runnable, 1500)

                }catch (e: java.lang.Exception){
                    Toast.makeText(this@VehicleTracking2,e.message, Toast.LENGTH_LONG).show()
                }

            })
        }
    }

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
        vehicleRV.layoutManager =  LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)


        // on below line we are initializing our list
        vehiclesList = ArrayList()
        viewModel.getVehicles()

        viewModel.vehiclesResponse.observe(this, Observer{
            if(it.isSuccessful){
                vehiclesList= it.body() as ArrayList<Vehicle>
                vehicleRecyclerAdapter = ActiveVehicleRecyclerAdapter(vehiclesList,this)
                vehicleRV.adapter = vehicleRecyclerAdapter
                vehicleRV.layoutManager =  LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL,false)

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


    override fun onClick(v: View?) {
        var i: Intent
        if (v != null) {
            when (v.id) {
                R.id.back_button_img -> {
                    i= Intent(this@VehicleTracking2, VehiclesActivity::class.java)
                    startActivity(i)
                    animate()
                }
                R.id.moreTv-> {
                    showBottomSheet()
                }
                R.id.vehImg-> {
                    i= Intent(this@VehicleTracking2, VehicleInfoActivity::class.java)
                    i.putExtra("vehicle_id", id)
                    i.putExtra("vehicleType",vehicleType)
                    i.putExtra("plateNumber", plateNumber)
                    i.putExtra("make", make)
                    i.putExtra("model", model)
                    i.putExtra("year", year)
                    i.putExtra("vin", vin)
                    i.putExtra("odometerReading",odometerReading)
                    i.putExtra("currentLocation", vehCurrentLocation)
                    i.putExtra("availability", availability)
                    i.putExtra("imageUrl", imageUrl)
                    i.putExtra("driverImageUrl", driverImageUrl)
                    i.putExtra("driverFirstname", driverFirstName)
                    i.putExtra("driverLastname",  driverLastName)
                    i.putExtra("routeStartPoint", routeStartPoint)
                    i.putExtra("routeEndPoint", routeEndPoint)
                    i.putExtra("operationArea", operationArea)
                    i.putExtra("geofenceRadius", geofenceRadius)
                    i.putExtra("seatCapacity", seatCapacity)
                    i.putExtra("fuelConsumption", fuelConsumption)
                    i.putExtra("fuelType",fuelType)
                    startActivity(i)
                    animate2()
                }
                else -> {
                    print("Error")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onKeyEntered(key: String?, location: GeoLocation?) {
        if(tripStarted){
            endTime = LocalDateTime.now().toString()
            addTrip()
            sendNotification("Trip Complete", "Trip Completed");
        }
        Log.d("Key Entered", "Entered Key")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onKeyExited(key: String?) {
       startTime = LocalDateTime.now().toString()
        sendNotification("Trip Started", "Trip Started");
        tripStarted= true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onKeyMoved(key: String?, location: GeoLocation?) {
        Log.d("Within", "Moved within the geofence area")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onGeoQueryReady() {
       Log.d("GeoQueryReady", "GeoQuery is ready")
    }

    override fun onGeoQueryError(error: DatabaseError?) {
        Toast.makeText(this,error?.message, Toast.LENGTH_SHORT).show()
    }



    fun addTrip(){
        Toast.makeText(this, "Adding trip information", Toast.LENGTH_SHORT).show()
            try{
                val newTrip= TripAdd(Constants.userId!!,id!!,startTime!!, endTime!!)
                Log.d("add state", "Added")
                viewModel.addTrip(newTrip)
                viewModel.tripsResponse.observe(this, Observer{
                    if(it.isSuccessful){
                        tripsList= it.body() as ArrayList<Trip>
                        if(tripsList.size != null)
                            Toast.makeText(this, "Trip Complete", Toast.LENGTH_SHORT).show()

                        Log.d("TRIP",tripsList.size.toString())
                    }else{
                        Toast.makeText(this,it.errorBody()!!.string(),Toast.LENGTH_LONG).show()
                        Log.d("ERROR", it.errorBody()!!.string())
                    }
                    })
                }catch(e:Exception) {
                    Log.d("Add Trip", e.message.toString())
            }//}
            //,1000)
    }





    @SuppressLint("RemoteViewLayout")
    @RequiresApi(Build.VERSION_CODES.O)
    fun sendNotification(title:String , content:String) {
        val NOTIFICATION_CHANNEL_ID = "Trip Complete";
        val notificationManager:NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,"Trip Complete Notification",
            NotificationManager.IMPORTANCE_DEFAULT)

        //Config
        notificationChannel.setDescription ("Channel description");
        notificationChannel.enableLights (true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setVibrationPattern (longArrayOf(0, 1000, 500, 1000));
        notificationChannel.enableVibration (true);
        notificationManager.createNotificationChannel(notificationChannel)
        }

        // intent passed here is to our afterNotification class
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val  builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        builder.setContentTitle (title)
            .setContentText(content)
            .setAutoCancel (false)
            .setSmallIcon (R.mipmap.ic_launcher)
            .setLargeIcon (BitmapFactory.decodeResource(getResources (), R.mipmap.ic_launcher))
            .setContentIntent(pendingIntent)
        val  notification =  builder.build()
        notificationManager.notify(Random.nextInt(), notification)
    }

}





