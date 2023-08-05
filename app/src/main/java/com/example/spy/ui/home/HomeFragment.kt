package com.example.spy.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.spy.Common
import com.example.spy.R
import com.example.spy.databinding.FragmentHomeBinding
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.common.primitives.UnsignedBytes.toInt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var mMap: GoogleMap
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mapFragment:SupportMapFragment

    //location
    private lateinit var  locationRequest:LocationRequest
    private lateinit var  locationCallback:LocationCallback
    private lateinit var  fusedLocationProviderClient: FusedLocationProviderClient


    //Online system
    private lateinit var onlineRef: DatabaseReference
    private lateinit var copLocationRef:DatabaseReference
    private lateinit var geoFire: GeoFire

    private val onlineValueEventListener= object:ValueEventListener{
        override fun onCancelled(error: DatabaseError) {
            Snackbar.make(mapFragment.requireView(), error.message, Snackbar.LENGTH_SHORT).show()
        }

        override fun onDataChange(snapshot: DataSnapshot) {
           if(snapshot.exists()){
               Log.d("NOT", "NO")
           }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        init()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return root
    }

    @SuppressLint("MissingPermission")
    private fun init() {

        onlineRef= FirebaseDatabase.getInstance().getReference().child("info/connected")
        copLocationRef=FirebaseDatabase.getInstance().getReference(Common.VEHICLE_LOCATION_REFERENCE)
        geoFire= GeoFire(copLocationRef)

        registerOnLineSystem()

        locationRequest= LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setFastestInterval(15000)  //15 seconds
        locationRequest.interval= 10000  //10 seconds
        locationRequest.setSmallestDisplacement(50f) //50seconds

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
                //update location
                geoFire.setLocation(
                    Common.vehicle_id,
                    GeoLocation(locationResult.lastLocation!!.latitude, locationResult.lastLocation!!.longitude)
                ){ key:String?, error:DatabaseError? ->
                    if(error != null) {
                        Snackbar.make(mapFragment.requireView(), error.message, Snackbar.LENGTH_SHORT).show()
                    } else
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

        //Request access location permission
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
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE)
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
        try {
            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),R.raw.spy_maps_style))
            if(!success)
                Log.e("EDMT_ERROR", "style parsing error")
          }catch (e: Resources.NotFoundException){
            Log.e("EDMT_ERROR", e.message!!)
        }

    }
}
