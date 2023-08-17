package com.example.test

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.*
import com.example.test.repository.Repository
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AboutRouteActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var viewModel:MainViewModel
    var distance: Double? = null
    var inserted = false

    private lateinit var estimateDistanceTv:TextView
    private lateinit var estimateTimeTv: TextView
    private lateinit var startPointTv:TextView
    private lateinit var endPointTv: TextView
    private lateinit var tripsCountTv:TextView
    private lateinit var vehicleCountTv: TextView
    private lateinit var estimateFareAmtTv:TextView
    private lateinit var vehicleRV: RecyclerView
    lateinit var vehicleRecyclerAdapter: VehicleRecyclerAdapter
    private lateinit var progressBar: ProgressBar



    lateinit var routeId: String
    lateinit var startPoint: String
    lateinit var endPoint: String
    lateinit var estimateDistance: String
    lateinit var estimateTime: String
    var estimateFareAmt: Double = 0.0
    private lateinit var backBtn: ImageView

    var routeStats: ArrayList<RouteStat> = ArrayList<RouteStat>()
    var filteredRoute: List<RouteStat> = ArrayList<RouteStat>()

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @RequiresApi(Build.VERSION_CODES.O)
    val current = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    val oneDayEarlier = current.minusDays(1)

    @RequiresApi(Build.VERSION_CODES.O)
    val formattedCurrent = current.format(formatter)

    @RequiresApi(Build.VERSION_CODES.O)
    val formattedOneDayEarlier = oneDayEarlier.format(formatter)

    @RequiresApi(Build.VERSION_CODES.O)
    var startDate:String= formattedOneDayEarlier
    @RequiresApi(Build.VERSION_CODES.O)
    var endDate:String= formattedCurrent




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide();
        setContentView(R.layout.activity_about_route)
        startPointTv = findViewById(R.id.startPointTv)
        endPointTv = findViewById(R.id.endPointTv)
        tripsCountTv = findViewById(R.id.tripsValueTv)
        vehicleCountTv= findViewById(R.id.vehicleValueTv)
        estimateTimeTv= findViewById(R.id.timeValueTv)
        estimateDistanceTv= findViewById(R.id.distanceValueTv)
        estimateFareAmtTv= findViewById(R.id.fareValueTv)
        vehicleRV= findViewById(R.id.vehiclesRV)

        val repository = Repository()
        progressBar = findViewById(R.id.progress_circular)
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)


        startPoint = intent.getStringExtra("startPoint").toString()
        endPoint = intent.getStringExtra("endPoint").toString()
        estimateDistance = intent.getStringExtra("estimateDistance").toString()
        estimateTime = intent.getStringExtra("estimateTime").toString()
        estimateFareAmt = intent.getDoubleExtra("estimateFare", 0.0)
        routeId = intent.getStringExtra("routeId").toString()

        startPointTv.text= startPoint
        endPointTv.text= endPoint
        estimateDistanceTv.text= estimateDistance
        estimateTimeTv.text= estimateTime
        estimateFareAmtTv.text= estimateFareAmt.toString()
        backBtn = findViewById(R.id.back_button_img)
        backBtn.setOnClickListener(this)
        progressBar.visibility = View.VISIBLE
        Handler().postDelayed({
            getVehicles()
            getRouteStats()
        },2000)
    }

    fun getVehicles() {
            // on below line we are initializing our list
            var vehicles: ArrayList<Vehicle>
            viewModel.getVehiclesByRoute(routeId)
            try {
                viewModel.vehiclesResponse.observe(this, Observer {
                    if (it.isSuccessful) {
                        vehicles = it.body() as ArrayList<Vehicle>
                        vehicleRecyclerAdapter = VehicleRecyclerAdapter(vehicles, this, 0, null)
                        vehicleRV.adapter = vehicleRecyclerAdapter
                        vehicleRV.layoutManager =  LinearLayoutManager(this,
                            LinearLayoutManager.VERTICAL,false)
                        Log.d("VEHICLES", vehicles.size.toString())
                        vehicleCountTv.text= vehicles.size.toString()
                        Toast.makeText(this, vehicles.size.toString(), Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(this, it.errorBody().toString(), Toast.LENGTH_SHORT).show()
                    }
                }
                )
            } catch (e: Exception) {
                Log.d("Exception", e.message.toString())
            }
            progressBar.visibility= View.GONE
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getRouteStats(){
        // on below line we are initializing our list
        viewModel.getRouteStats(startDate, endDate)
        try{
            viewModel.routeStatsResponse.observe(this
            ) {
                if (it.isSuccessful) {
                    routeStats = it.body() as ArrayList<RouteStat>
                    Log.d("RouteStat",routeStats.size.toString())
                    Toast.makeText(this, routeStats.size.toString(), Toast.LENGTH_SHORT).show()
                     filteredRoute = routeStats.filter{ it.route._id == routeId  }
                    Log.d("Filtered Stat",filteredRoute.size.toString())
                    Toast.makeText(this, filteredRoute.size.toString(), Toast.LENGTH_SHORT).show()
                    if(filteredRoute.size > 0)
                        tripsCountTv.text= filteredRoute[0].trips.toString()

                } else {
                    Toast.makeText(this, it.errorBody()?.string(), Toast.LENGTH_LONG)
                        .show()
                    Log.d("Error", it.errorBody()?.string()!!)
                }
            }
        }
        catch (e:Exception){
            Log.d("Exception", routeStats.size.toString())
        }
    }




    fun animate(){
        overridePendingTransition(R.anim.slide_in_right,
            R.anim.slide_out_left);
    }


    override fun onClick(v: View?) {
        val i: Intent
        if (v != null) {
            when (v.id) {
                R.id.back_button_img -> {
                    i = Intent(this, StartActivity::class.java)
                    startActivity(i)
                    animate()
                }
            }
        }
    }
}
