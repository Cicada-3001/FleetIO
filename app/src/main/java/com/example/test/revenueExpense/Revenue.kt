package com.example.test.revenueExpense

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.*
import com.example.test.models.OtherExpense
import com.example.test.models.OtherRevenue
import com.example.test.models.VehicleStat
import com.example.test.repository.Repository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


class Revenue : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var fareRev:RecyclerView
    private lateinit var otherRev:RecyclerView

    private lateinit var fareTotalTv:TextView
    private lateinit var otherTotalTv:TextView

    private var fareTotal:Double =  0.0
    private var otherTotal:Double = 0.0
    var vehiclesStats: ArrayList<VehicleStat> = ArrayList<VehicleStat>()
    var revenueList: ArrayList<OtherRevenue> = ArrayList<OtherRevenue>()

    lateinit var revenueRecAdapter: OtherRevenueRecAdapter
    lateinit var vehStatRecAdapter: VehStatRecAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    @RequiresApi(Build.VERSION_CODES.O)
    val current = LocalDateTime.now().format(formatter)

    @RequiresApi(Build.VERSION_CODES.O)
    var startDate:String= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.now().minusYears(1).format(formatter)
    } else {
        LocalDateTime.now().minusYears(1).format(formatter)
    };
    @RequiresApi(Build.VERSION_CODES.O)
    var endDate:String= current



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel= ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)
        // on below line we are initializing our list
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_revenue, container, false)
            fareRev = view.findViewById(R.id.recFare)
            otherRev = view.findViewById(R.id.recOthers)
            fareTotalTv = view.findViewById(R.id.fareTotalTv)
            otherTotalTv = view.findViewById(R.id.otherTotalTv)

        getOtherRevenues()
        getFareRevenue()
        return view
    }

    fun getOtherRevenues(){
        // on below line we are initializing our list
        revenueList = ArrayList()
        viewModel.getOtherRevenue()
        viewModel.revenueResponse.observe(viewLifecycleOwner, Observer {
            if (it.isSuccessful) {
                revenueList = it.body() as ArrayList<OtherRevenue>
                for (revenue in revenueList) {
                    otherTotal += revenue.amount
                }
                otherTotalTv.text= ((otherTotal * 100).roundToInt()/100).toString() + " Ksh"
                revenueRecAdapter = OtherRevenueRecAdapter(revenueList,requireActivity())
                otherRev.layoutManager = LinearLayoutManager(requireActivity())
                otherRev.adapter =  revenueRecAdapter
            } else {
                Log.d("Error",it.message().toString())
                Toast.makeText(requireActivity(), it.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getFareRevenue(){
        viewModel.getVehicleStats(startDate, endDate)
        viewModel.vehicleStatsResponse.observe(viewLifecycleOwner
        ) {
            if (it.isSuccessful) {
                vehiclesStats = it.body() as ArrayList<VehicleStat>
                for (stat in vehiclesStats) {
                    fareTotal += stat.totalFareRevenue
                }
                fareTotalTv.text= ((fareTotal * 100).roundToInt()/100).toString() + " Ksh"
                vehStatRecAdapter = VehStatRecAdapter(vehiclesStats,requireActivity(),0)
                fareRev.layoutManager = LinearLayoutManager(requireActivity())
                fareRev.adapter =  vehStatRecAdapter

            } else {
                Toast.makeText(requireActivity(), it.errorBody()?.string(), Toast.LENGTH_LONG)
                    .show()
                Log.d("Error", it.errorBody()?.string()!!)
            }
        }
    }
}











