package com.example.test

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore.Images.Media.getBitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.test.databinding.FragmentMapsBinding
import com.example.test.util.AnimationUtils
import com.example.test.util.MapUtils.getCarBitmap
import com.example.test.util.MapUtils.getListOfLocations
import com.example.test.util.MapUtils.getOriginDestinationMarkerBitmap


import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.util.MapUtils
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener




class MapsFragment : Fragment(),  OnMapReadyCallback  {
    private var _binding: FragmentMapsBinding? = null
    private lateinit var mMap: GoogleMap
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mapFragment:SupportMapFragment


    //location
    private lateinit var  locationRequest:LocationRequest
    private lateinit var  locationCallback:LocationCallback
    private lateinit var  fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var defaultLocation: LatLng

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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mapViewModel =
            ViewModelProvider(this).get(MapsViewModel::class.java)

        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        init()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }


    @SuppressLint("MissingPermission")
    private fun init() {
        onlineRef= FirebaseDatabase.getInstance().getReference().child("info/connected")
        userLocationRef= FirebaseDatabase.getInstance().getReference(Common.USER_LOCATION_REFERENCE)
        currentUser= FirebaseDatabase.getInstance().getReference(Common.USER_INFO_REFERENCE).child(
            FirebaseAuth.getInstance().currentUser!!.uid
        )
        geoFire= GeoFire(userLocationRef)

        registerOnLineSystem()

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
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(requireContext())
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
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

     /*   //Request access location permission
        Dexter
            .withContext(context)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object:PermissionListener{
                @SuppressLint("MissingPermission")
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    mMap.isMyLocationEnabled =true
                    mMap.uiSettings.isMyLocationButtonEnabled=true
                    mMap.setOnMyLocationClickListener {
                        fusedLocationProviderClient.lastLocation
                            .addOnFailureListener {
                                if (p0 != null) {
                                    Toast.makeText(context,"Permission"+p0.permissionName+ "was denied", Toast.LENGTH_SHORT).show()
                                }
                            }.addOnSuccessListener {
                                val userLatLang= LatLng(it.latitude, it.longitude)
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLang,18f))
                            }
                    }

                    val locationButton = (mapFragment.requireView()
                        .findViewById<View>("1".toInt())!!
                        .parent!! as View).findViewById<View>("2".toInt())
                    val params =locationButton.layoutParams as RelativeLayout.LayoutParams
                    params.addRule(RelativeLayout.ALIGN_TOP,0)
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                    params.bottomMargin =50
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    if (p0 != null) {
                        Toast.makeText(context,"Permission"+p0.permissionName+ "was denied", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    TODO("Not yet implemented")
                }

            }).check()

    */
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(),
                    R.raw.expresso_maps_style))
            if(!success)
                Log.e("EDMT_ERROR", "style parsing error")
        }catch (e: Resources.NotFoundException){
            Log.e("EDMT_ERROR", e.message!!)
        }

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
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(10f).build()
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
            BitmapDescriptorFactory.fromBitmap(getCarBitmap(activity?.applicationContext!!))
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
                    Toast.makeText(activity, "Trip Ends", Toast.LENGTH_LONG).show()
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
            BitmapDescriptorFactory.fromBitmap(getOriginDestinationMarkerBitmap())
        return mMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }






}