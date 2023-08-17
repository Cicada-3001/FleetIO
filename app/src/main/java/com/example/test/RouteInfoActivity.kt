package com.example.test

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.Route
import com.example.test.models.Routing
import com.example.test.repository.Repository
import com.example.test.util.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.maps.android.SphericalUtil
import java.io.IOException

class RouteInfoActivity : AppCompatActivity(),View.OnClickListener, OnMapReadyCallback {
    lateinit var mMap: GoogleMap
    lateinit var searchViewStart: SearchView
    lateinit var searchViewEnd: SearchView
    var startLatLng: LatLng? = null
    var endLatLng: LatLng? = null
    var locationsList: ArrayList<Routing> = ArrayList<Routing>()
    var routesSelRecyclerAdapter: RoutesSelRecyclerAdapter = RoutesSelRecyclerAdapter(locationsList,this)
    var location1: String? = null
    var location2: String? = null
    var markerStart: Marker? = null
    var markerEnd: Marker? = null
    private lateinit var viewModel: MainViewModel
    var polyline: Polyline? =null
    var distance: Double? = null
    var inserted= false
    private lateinit  var route: Route

    private lateinit var estimateFareAmtEdt: EditText
    private var updateRouteBtn: Button? = null
    private var clearBtn: Button? = null
    private lateinit var estimateDistanceTv:EditText
    private lateinit var estimateTimeTv:EditText
    private lateinit var routesSelRv:RecyclerView
    private lateinit var detailsTv:TextView

    lateinit var routeId: String
    lateinit var startPoint: String
    lateinit var endPoint: String
    lateinit var startingCoordinate: String
    lateinit var endingCoordinate: String
    lateinit var estimateDistance: String
    lateinit var estimateTime: String
    var estimateFareAmt:Double = 0.0

    private lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_info)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        getSupportActionBar()?.hide();


        startingCoordinate = ""
        endingCoordinate = ""

        // initializing our search view.
        searchViewStart = findViewById(R.id.searchViewStart)
        searchViewEnd = findViewById(R.id.searchViewEnd)
        updateRouteBtn = findViewById(R.id.addRouteBtn)
//        clearBtn= findViewById(R.id.clearBtn)
        estimateDistanceTv= findViewById(R.id.distanceValueTv)
        estimateTimeTv= findViewById(R.id.timeValueTv)
        routesSelRv = findViewById(R.id.routesSelectRv)
        estimateFareAmtEdt = findViewById(R.id.fareAmtEdit)
        detailsTv = findViewById(R.id.detailsTv)
        detailsTv.setOnClickListener(this)


        startPoint = intent.getStringExtra("startPoint").toString()
        endPoint = intent.getStringExtra("endPoint").toString()
        estimateDistance = intent.getStringExtra("estimateDistance").toString()
        estimateFareAmt = intent.getDoubleExtra("estimateFare", 0.0)
        routeId = intent.getStringExtra("routeId").toString()


        Log.d("RouteId", routeId)
        estimateTime =  intent.getStringExtra("estimateTime").toString()

        estimateDistanceTv.setText(estimateDistance)
        estimateTimeTv.setText(estimateTime)
        estimateFareAmtEdt.setText(estimateFareAmt.toString())


        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        // Obtain the SupportMapFragment and get notified
        // when the map is ready to be used.
        mapFragment = (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!

        // adding on query listener for our search view.

        searchViewStart.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // on below line we are getting the
                // location name from search view.
                locate(searchViewStart)
                if(location2  != null || location2 != "")
                    calculateValues()
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        searchViewEnd.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // on below line we are getting the
                // location name from search view.
                locate(searchViewEnd)
                if(location1 != null || location1 != "")
                    calculateValues()
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        // at last we calling our map fragment to update.
        mapFragment!!.getMapAsync(this)
        updateRouteBtn?.setOnClickListener{
            updateRoute()
        }
        clearBtn?.setOnClickListener{
            mMap.clear()
            locationsList.clear()
            searchViewStart.setQuery("", false);
            searchViewStart.clearFocus();
            searchViewEnd.setQuery("", false);
            searchViewEnd.clearFocus();
        }

        locationsList.add(Routing(startPoint,endPoint))
        routesSelRecyclerAdapter = RoutesSelRecyclerAdapter(locationsList,this)
        routesSelRv?.adapter =routesSelRecyclerAdapter
        routesSelRv?.layoutManager= LinearLayoutManager(this)


    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap;
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this,
                    R.raw.expresso_maps_style))
            if(!success)
                Log.e("EDMT_ERROR", "style parsing error")
        }catch (e: Resources.NotFoundException){
            Log.e("EDMT_ERROR", e.message!!)
        }
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
        tvText.text = "Saving ..."
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
                Toast.makeText(applicationContext,"Route added !", Toast.LENGTH_SHORT).show()
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

    fun updateRoute() {
        if(estimateFareAmt < 1)
            Toast.makeText(this,"Fare amount is required", Toast.LENGTH_SHORT).show()
        else {
            estimateFareAmt = estimateFareAmtEdt.text.toString().toDouble()
            estimateDistance = estimateDistanceTv.text.toString()
            estimateTime = estimateTimeTv.text.toString()

            val newRoute = Route(
                Constants.userId!!,
                startPoint,
                endPoint,
                startingCoordinate,
                endingCoordinate,
                estimateDistance.toDouble(),
                estimateTime,
                estimateFareAmt
            )
            Toast.makeText(this, estimateTime.toString(), Toast.LENGTH_SHORT).show()
            Toast.makeText(this, estimateDistance.toString(), Toast.LENGTH_SHORT).show()
            viewModel.updateRoute(routeId,newRoute)
            viewModel.routeResponse.observe(this, Observer {
                showProgressDialog()
                if (it.isSuccessful) {
                    route = it.body() as Route
                    inserted = true
                } else {
                    Toast.makeText(this, it.errorBody()!!.string(), Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun geoCode(area: String):LatLng{
        // where we will store the list of all address.
        var addressList: List<Address>? = null

        // on below line we are creating and initializing a geo coder.
        val geocoder = Geocoder(this@RouteInfoActivity)
        try {
            // on below line we are getting location from the
            // location name and adding that location to address list.
            addressList = geocoder.getFromLocationName(area, 1)
        } catch (e: IOException) {
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


    fun locate(view: View){
        val location: String?
        if(view.id == R.id.searchViewStart) {
            location1 = searchViewStart.getQuery().toString()
            location = location1
            startPoint = location1 as String
            startingCoordinate = geoCode(startPoint).toString()

        }
        else {
            location2 = searchViewEnd.getQuery().toString()
            location = location2
            endPoint = location2 as String
            endingCoordinate = geoCode(endPoint).toString()


        }

        // below line is to create a list of address


        // checking if the entered location is null or not.
        if (location != null || location == "") {
            val latLng= geoCode(location)

            if(view.id == R.id.searchViewStart){
                startLatLng = latLng

                if((location1 != null || location1 == "") && (location2 != null || location2 == "")){
                    locationsList.clear()
                    locationsList.add(Routing(location1,location2))
                    if(markerStart != null)
                        markerStart?.remove()
                    // on below line we are adding marker to that position.
                    markerStart = mMap.addMarker(MarkerOptions().position(latLng).title(location1))!!
                }
            }

            else{
                endLatLng = latLng
                if((location1 != null || location1 == "") && (location2 != null || location2 == "")){
                    locationsList.clear();
                    locationsList.add(Routing(location1,location2))
                    if(markerEnd != null)
                        markerEnd?.remove()

                    // on below line we are adding marker to that position.
                    markerEnd = mMap.addMarker(MarkerOptions().position(latLng).title(location2))!!
                }
            }

            // below line is to animate camera to that position.
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            if(startLatLng != null && endLatLng != null){
                polyline?.remove()
                polyline= mMap.addPolyline(
                    PolylineOptions().add(startLatLng,endLatLng)
                        .width // below line is use to specify the width of poly line.
                            (5f) // below line is use to add color to our poly line.
                        .color(Color.BLACK) // below line is to make our poly line geodesic.
                        .geodesic(true)
                )
            }

        }
    }




    fun calculateValues(){
        estimateFareAmt= estimateFareAmtEdt?.text.toString().toDouble()
        val distance:Double?  = SphericalUtil.computeDistanceBetween(startLatLng, endLatLng)
        val distanceValue = String.format("%.2f", distance!! / 1000)
        var time= "%.1f".format(distance/(40 *1000))
        if(time.toDouble() < 1.0)
            time = "%.0f".format(time.toDouble() * 60)+ " min"
        else
            time = time + " hours"

        estimateTime =time
        estimateDistance = distanceValue
        estimateDistanceTv.setText(distanceValue + " km")
        estimateTimeTv.setText(time.toString()+ " ")

        routesSelRecyclerAdapter.notifyDataSetChanged()
        // on below line we are initializing our list
      /*  routesSelRecyclerAdapter = RoutesSelRecyclerAdapter(locationsList,this)
        routesSelRv?.adapter =routesSelRecyclerAdapter
        routesSelRv?.layoutManager= LinearLayoutManager(this)*/
    }


    fun animate(){
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.clearSearchIv -> {
                    searchViewStart.clearFocus()
                    searchViewEnd.clearFocus()
                }
                R.id.detailsTv -> {
                    var intent = Intent(this@RouteInfoActivity, AboutRouteActivity::class.java)
                    intent.putExtra("routeId", routeId)
                    intent.putExtra("startPoint", startPoint)
                    intent.putExtra("endPoint", endPoint)
                    intent.putExtra("estimateFare", estimateFareAmt)
                    intent.putExtra("estimateDistance", estimateDistance)
                    intent.putExtra("estimateTime", estimateTime)
                    startActivity(intent)
                    animate()
                }
                else -> {
                    print("Error")
                }

            }
        }
    }
}





