package com.example.test.ui.home

import android.content.Intent
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.*
import com.example.test.databinding.FragmentHomeBinding
import com.example.test.repository.Repository
import com.example.test.util.ActiveVehicleRecyclerAdapter
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.test.models.*
import com.github.mikephil.charting.data.BarEntry
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


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
    private lateinit var cancelImg:ImageView
    private lateinit var licenseRv:RecyclerView
    private lateinit var maintenanceRv:RecyclerView
    private lateinit var alertCard:CardView


    private lateinit var maintenancesTv: TextView
    private lateinit var vehiclesTv: TextView
    private lateinit var driversTv:TextView
    private lateinit var routesTv:TextView
    private lateinit var tripsTv:TextView



    private lateinit var  needMaintenance:TextView
    private lateinit var  onRoute:TextView
    private lateinit var  expiredLicenses:TextView
    // on below line we are creating variables for
    private lateinit var vehicleRV: RecyclerView
    private lateinit var routeRV: RecyclerView
    private lateinit var backButtonImg: ImageView
    var vehiclesList: ArrayList<Vehicle> = ArrayList<Vehicle>()
    lateinit var  vehicleRecyclerAdapter: ActiveVehicleRecyclerAdapter
    private lateinit var progressBar: ProgressBar
    lateinit var anyChartView: AnyChartView
    var maintenancesList: ArrayList<Maintenance> = ArrayList<Maintenance>()
    var filteredMaintenances: List<Maintenance> = ArrayList<Maintenance>()

    var  routesList: ArrayList<Route> = ArrayList<Route>()
    lateinit var maintenanceRecyclerAdapter: MaintenanceRecyclerAdapter
    var driversList: ArrayList<Driver> = ArrayList<Driver>()
    var filteredDrivers: List<Driver> = ArrayList<Driver>()

    lateinit var  driverRecyclerAdapter: DriverRecyclerAdapter



    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    @RequiresApi(Build.VERSION_CODES.O)
    val current = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    val sixMonthsEarlier = current.minusMonths(6)

    @RequiresApi(Build.VERSION_CODES.O)
    val formattedCurrent = current.format(formatter)


    @RequiresApi(Build.VERSION_CODES.O)
    val formattedCurrent2 = current.format(formatter2)

    @RequiresApi(Build.VERSION_CODES.O)
    val formattedSixMonthsEarlier = sixMonthsEarlier.format(formatter)


    @RequiresApi(Build.VERSION_CODES.O)
    var startDate:String= formattedSixMonthsEarlier
    @RequiresApi(Build.VERSION_CODES.O)
    var endDate:String= formattedCurrent

    val data: MutableList<DataEntry> = ArrayList()
    var vehiclesStats: ArrayList<VehicleStat> = ArrayList<VehicleStat>()

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

    @RequiresApi(Build.VERSION_CODES.O)
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
        progressBar = root.findViewById(R.id.progress_circular)
        expiredLicenses = root.findViewById(R.id.expiredTv)
        testsCard = root.findViewById(R.id.testsCard)
        cancelImg= root.findViewById(R.id.cancelImg)
        licenseRv = root.findViewById(R.id.licenseRv)
        maintenanceRv = root.findViewById(R.id.maintenanceRv)
        alertCard = root.findViewById(R.id.alertCard)
        maintenancesTv = root.findViewById(R.id.maintenancesTv)
        vehiclesTv = root.findViewById(R.id.vehiclesTv)
        driversTv = root.findViewById(R.id.driversTv)
        routesTv= root.findViewById(R.id.routesTv)
        tripsTv=  root.findViewById(R.id.tripsTv)

        testsCard.setOnClickListener(this)
        vehicleCard.setOnClickListener(this)
        driverCard.setOnClickListener(this)
        routeCard.setOnClickListener(this)
        testsCard.setOnClickListener(this)
        maintenanceCard.setOnClickListener(this)
        statisticsCard.setOnClickListener(this)
        statsCardUpper.setOnClickListener(this)
        tripsCardUpper.setOnClickListener(this)
        cancelImg.setOnClickListener(this)

        vehicleRV= root.findViewById(R.id.activeVehiclesRv)
        vehicleRV.layoutManager =  LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
        anyChartView = root.findViewById<AnyChartView>(R.id.any_chart_view)
        driverRecyclerAdapter = DriverRecyclerAdapter(driversList, requireActivity(),1)
        maintenanceRecyclerAdapter = MaintenanceRecyclerAdapter(maintenancesList,requireActivity(),2)
        getVehicleStats()
        getVehicles()
        getDrivers()
        getMaintenances()
        getCounts()
        getRoutes()

        Handler().postDelayed({
            showStatsPreview()
        },3000)

        Handler().postDelayed({
            needMaintenance.text= "NOT MAINTAINED   "+filteredMaintenances.size.toString()
            expiredLicenses.text = "EXPIRED LICENSE   "+filteredDrivers.size.toString()
            vehiclesTv.text = vehiclesList.size.toString()+" Vehicles"
            driversTv.text = driversList.size.toString()+" Drivers"
            routesTv.text = routesList.size.toString()+" Routes"
            if(filteredMaintenances.size > 0  && filteredDrivers.size > 0)
                alertCard.visibility= View.VISIBLE
        },5000)


        return root
    }

    fun showStatsPreview() {
        val cartesian: Cartesian = AnyChart.column()
        val column: Column = cartesian.column(data)
        column.tooltip()
            .titleFormat("{%X}")
            .position(Position.CENTER_BOTTOM)
            .anchor(Anchor.CENTER_BOTTOM)
            .offsetX(0.0)
            .offsetY(5.0)
            .format("${'%'}Value{groupsSeparator: }")

        cartesian.animation(true)
        cartesian.title("Trips Summary In the Past Week")

        cartesian.yScale().minimum(0.0)

        cartesian.yAxis(0).labels().format("${'%'}Value{groupsSeparator: }")

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
        cartesian.interactivity().hoverMode(HoverMode.BY_X)
        cartesian.xAxis(0).title("Product")
        cartesian.yAxis(0).title("Revenue")
        anyChartView.setChart(cartesian)
    }




    var i:Int = 0
    @RequiresApi(Build.VERSION_CODES.O)
    fun getVehicleStats(){
        // on below line we are initializing our list
        viewModel.getVehicleStats(startDate, endDate)
        try{
            viewModel.vehicleStatsResponse.observe(viewLifecycleOwner
            ) {
                if (it.isSuccessful) {
                    vehiclesStats = it.body() as ArrayList<VehicleStat>
                    Log.d("LIST", vehiclesStats.size.toString())
                    Log.d("Stat response", "The response was successful")
                    for (stat in vehiclesStats) {
                         data.add(ValueDataEntry(stat.vehicle.vehicleType,stat.trips))
                        i++
                    }
                } else {
                    Toast.makeText(requireActivity(), it.errorBody()?.string(), Toast.LENGTH_LONG)
                        .show()
                    Log.d("Error", it.errorBody()?.string()!!)
                }
            }
        }
        catch (e:Exception){
            Log.d("Exception",e.toString())
        }
    }



    fun getRoutes() {
        // on below line we are initializing our list
        routesList = ArrayList()
        viewModel.getRoutes()

        viewModel.routesResponse.observe(this, Observer {
            if (it.isSuccessful) {
                routesList = it.body() as ArrayList<Route>
                Log.d("LIST", routesList.size.toString())
            } else {
                Toast.makeText(activity, it.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }

        })

    }



    fun getVehicles(){
        progressBar.visibility = View.VISIBLE
        Handler().postDelayed({
        // on below line we are initializing our list
        vehiclesList = ArrayList()
        viewModel.getVehicles()

        viewModel.vehiclesResponse.observe(this, Observer{
            if(it.isSuccessful){
                vehiclesList= it.body() as ArrayList<Vehicle>
                vehicleRecyclerAdapter = ActiveVehicleRecyclerAdapter(vehiclesList,1,requireActivity())
                vehicleRV.adapter = vehicleRecyclerAdapter
                vehicleRV.layoutManager =  LinearLayoutManager(activity,
                    LinearLayoutManager.HORIZONTAL,false)

            }else{
                Toast.makeText(activity,it.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
            progressBar.visibility = View.GONE
        })}, 2000)
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

                R.id.cancelImg->{
                    alertCard.visibility= View.GONE
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
                    onRoute.text = "ON ROUTE   "+ counts.onRoute.toString()
                } else {
                    Log.d("Error", it.errorBody()?.string()!!)
                }
            }
        }
        catch (e:Exception){
            Log.d("Exception",e.toString())
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getMaintenances(){
            viewModel.getMaintenances()
            viewModel.maintenancesResponse.observe(this, Observer {
                if (it.isSuccessful) {
                    maintenancesList = it.body() as ArrayList<Maintenance>
                    filteredMaintenances = maintenancesList.filter{ it.date  <= formattedCurrent2   }
                    Log.d("Filtered Maintenance",filteredMaintenances.size.toString())
                    Toast.makeText(activity, filteredMaintenances.size.toString(), Toast.LENGTH_SHORT).show()
                    maintenanceRecyclerAdapter = MaintenanceRecyclerAdapter(filteredMaintenances as ArrayList<Maintenance>,requireActivity(),2)
                    maintenanceRv.adapter = maintenanceRecyclerAdapter
                    maintenanceRv.layoutManager =  LinearLayoutManager(activity,
                        LinearLayoutManager.HORIZONTAL,false)


                } else {
                    Toast.makeText(activity, it.errorBody().toString(), Toast.LENGTH_SHORT).show()
                }
            })

        maintenanceRecyclerAdapter.notifyDataSetChanged()


    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getDrivers(){
        viewModel.getDrivers()
        viewModel.driversResponse.observe(this, Observer {
            if (it.isSuccessful) {
                driversList = it.body() as ArrayList<Driver>
                filteredDrivers = driversList.filter{ it.licenseExpiry!! <= formattedCurrent2   }
                Log.d("Filtered Drivers",filteredDrivers.size.toString())
                Toast.makeText(activity, filteredDrivers.size.toString(), Toast.LENGTH_SHORT).show()
                driverRecyclerAdapter = DriverRecyclerAdapter(filteredDrivers as ArrayList<Driver>, requireActivity(),1)
                licenseRv.adapter = driverRecyclerAdapter
                licenseRv.layoutManager =  LinearLayoutManager(activity,
                    LinearLayoutManager.HORIZONTAL,false)
            } else {
                Toast.makeText(activity, it.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
        })

        driverRecyclerAdapter.notifyDataSetChanged()

    }









}