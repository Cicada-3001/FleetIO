package com.example.test.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.test.*
import com.example.test.databinding.FragmentHomeBinding
import com.example.test.models.Counts
import com.example.test.models.VehicleStat
import com.example.test.repository.Repository
import com.google.api.Distribution
import kotlin.math.roundToInt

class HomeFragment : Fragment(), View.OnClickListener {
    private lateinit var vehicleCard: CardView
    private lateinit var driverCard: CardView
    private lateinit var routeCard: CardView
    private lateinit var maintenanceCard: CardView
    private lateinit var statisticsCard: CardView
    private lateinit var testsCard: CardView
    private lateinit var statsCardUpper: LinearLayout
    private lateinit var tripsCardUpper:LinearLayout
    private lateinit var viewModel: MainViewModel
    private lateinit var  counts:Counts

    private lateinit var  needMaintenance:TextView
    private lateinit var  onRoute:TextView
    private lateinit var  expiredLicenses:TextView


    private var _binding: FragmentHomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel= ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        vehicleCard = root.findViewById(R.id.vehicleCard)
        driverCard = root.findViewById(R.id.driverCard)
        routeCard = root.findViewById(R.id.routeCard)
        maintenanceCard = root.findViewById(R.id.maintenanceCard)
        statisticsCard = root.findViewById(R.id.statsCard)
        statsCardUpper = root.findViewById(R.id.statsCardUpper)
        tripsCardUpper = root.findViewById(R.id.tripsCardUpper)
        needMaintenance= root.findViewById(R.id.maintenanceTv)
        onRoute = root.findViewById(R.id.onRouteTv)
        expiredLicenses = root.findViewById(R.id.expiredTv)
        testsCard = root.findViewById(R.id.testsCard)
        testsCard.setOnClickListener(this)
        vehicleCard.setOnClickListener(this)
        driverCard.setOnClickListener(this)
        routeCard.setOnClickListener(this)
        testsCard.setOnClickListener(this)
        maintenanceCard.setOnClickListener(this)
        statisticsCard.setOnClickListener(this)
        statsCardUpper.setOnClickListener(this)
        tripsCardUpper.setOnClickListener(this)

        getCounts()
        return root
    }

    fun animate(){
        activity?.overridePendingTransition(R.anim.slide_in_right,
            R.anim.slide_out_left);
    }

    override fun onClick(v: View?) {
        var i: Intent

        if (v != null) {
            when (v.id) {
                R.id.vehicleCard -> {
                    i = Intent(activity, VehiclesActivity::class.java)
                    startActivity(i)
                    animate()
                }
                R.id.driverCard -> {
                    i = Intent(activity, DriversActivity::class.java)
                    startActivity(i)
                    animate()
                }
                R.id.routeCard -> {
                    i = Intent(activity, RoutesActivity::class.java)
                    startActivity(i)
                    animate()
                }
                R.id.maintenanceCard -> {
                    i = Intent(activity, MaintenancesActivity::class.java)
                    startActivity(i)
                    animate()
                }
                R.id.statsCardUpper->{
                    i = Intent(activity, StatisticsActivity::class.java)
                    startActivity(i)
                    animate()
                }

                R.id.testsCard->{
                    i = Intent(activity, TestActivity::class.java)
                    startActivity(i)
                    animate()
                }

                R.id.tripsCardUpper->{
                    i = Intent(activity, TripActivity::class.java)
                    startActivity(i)
                    animate()
                }
                else -> {
                    print("Error")
                }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun getCounts(){
        // on below line we are initializing our list
        viewModel.getCounts()
        try{
            viewModel.countsResponse.observe(viewLifecycleOwner
            ) {
                if (it.isSuccessful) {
                     counts = it.body() as Counts
                    Log.d("Need Maintenance", counts.needMaintenance.toString())
                    needMaintenance.text= "NOT MAINTAINED   "+ counts.needMaintenance.toString()
                    onRoute.text = "ON ROUTE   "+ counts.onRoute.toString()
                    expiredLicenses.text = "EXPIRED LICENSE   "+ counts.expiredLicenses.toString()
                } else {
                    Log.d("Error", it.errorBody()?.string()!!)
                }
            }
        }
        catch (e:Exception){
            Log.d("Exception",e.toString())
        }
    }






}