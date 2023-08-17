package com.example.test.statistics

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.chart.common.listener.Event
import com.anychart.chart.common.listener.ListenersInterface
import com.anychart.charts.Pie
import com.anychart.charts.Waterfall
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import com.example.test.DriverRankRecyclerAdapter
import com.example.test.MainViewModel
import com.example.test.MainViewModelFactory
import com.example.test.R
import com.example.test.models.*
import com.example.test.repository.Repository
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarEntry
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MonthStatistics : Fragment() {
    private lateinit var waterfallChartView: AnyChartView
    private lateinit var anyChartView: AnyChartView
    var driversList: ArrayList<DriverStat> = ArrayList<DriverStat>()
    lateinit var incomeList: Income
    private lateinit var driverRV: RecyclerView
    private lateinit var viewModel: MainViewModel
    lateinit var pieChart: PieChart
    lateinit var pieChartView:AnyChartView
    lateinit var barChartView: BarChart
    lateinit var pie: Pie
    var vehiclesStats: ArrayList<VehicleStat> = ArrayList<VehicleStat>()
    private lateinit var progressBar: ProgressBar

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @RequiresApi(Build.VERSION_CODES.O)
    val current = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    val sixMonthsEarlier = current.minusMonths(1)

    @RequiresApi(Build.VERSION_CODES.O)
    val formattedCurrent = current.format(formatter)

    @RequiresApi(Build.VERSION_CODES.O)
    val formattedOneMonthEarlier = sixMonthsEarlier.format(formatter)


    @RequiresApi(Build.VERSION_CODES.O)
    var startDate:String= formattedOneMonthEarlier
    @RequiresApi(Build.VERSION_CODES.O)
    var endDate:String= formattedCurrent


    var revenueList: ArrayList<OtherRevenue> = ArrayList<OtherRevenue>()
    var maintenancesList: ArrayList<Maintenance> = ArrayList<Maintenance>()
    var expensesList = java.util.ArrayList<OtherExpense>()
    var expenseTotal:Double = 0.0
    var grossRevenue:Double = 0.0
    var fuelTotal:Double = 0.0
    var fareTotal:Double = 0.0
    var commissionTotal = 0.0
    var revenueTotal:Double = 0.0
    var maintenanceTotal:Double = 0.0
    var otherExpense:Double = 0.0
    var otherRevenue:Double = 0.0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel= ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)
        // on below line we are initializing our list
        driversList = ArrayList()
        viewModel.getDriverRanks()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_month_statistics, container, false)
        waterfallChartView= view.findViewById(R.id.waterfallChart)
        setWaterfallData()
        anyChartView = view.findViewById(R.id.any_chart_view)
        // on below line we are initializing our views with their ids.
        driverRV= view.findViewById(R.id.driversRv)
       // pieChart = view.findViewById(R.id.pieChart)


        var driverRankRecyclerAdapter: DriverRankRecyclerAdapter
        viewModel.driverRankResponse.observe(viewLifecycleOwner, Observer{
            if(it.isSuccessful){
                driversList= it.body() as ArrayList<DriverStat>
                driverRankRecyclerAdapter = DriverRankRecyclerAdapter(driversList,requireActivity())
                driverRV.adapter = driverRankRecyclerAdapter
                driverRV.setLayoutManager(
                    LinearLayoutManager(activity)
                );
            }else{
                Log.d("Result","The response was unsuccessful error"+it.errorBody()?.string())
                Toast.makeText(activity,it.errorBody()?.string(), Toast.LENGTH_SHORT).show()
            }
        })


        getOtherExpense()
        getMaintenance()
        getOtherRevenues()
        getVehicleStats()


        Handler().postDelayed({
            setPieChart()
        },4000)
        return view;

    }




    fun getOtherRevenues(){
        // on below line we are initializing our list
        revenueList = ArrayList()
        viewModel.getOtherRevenue()
        viewModel.revenueResponse.observe(viewLifecycleOwner, Observer {
            if (it.isSuccessful) {
                revenueList = it.body() as ArrayList<OtherRevenue>
                for (revenue in revenueList) {
                    revenueTotal += revenue.amount
                    otherRevenue += revenue.amount
                }
            } else {
                Log.d("Error",it.message().toString())
            }
        })
    }


    private fun getOtherExpense(){
        // on below line we are initializing our list
        expensesList = ArrayList()
        viewModel.getOtherExpenses()
        viewModel.expensesResponse.observe(viewLifecycleOwner, Observer {
            if (it.isSuccessful) {
                expensesList = it.body() as ArrayList<OtherExpense>
                for (expense in expensesList) {
                    expenseTotal += expense.amount
                    otherExpense +=  expense.amount
                }
            } else {
                Toast.makeText(requireActivity(), it.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun getMaintenance(){
        maintenancesList = ArrayList()
        viewModel.getMaintenances()
        viewModel.maintenancesResponse.observe(viewLifecycleOwner, Observer {
            if (it.isSuccessful) {
                for (maintenance in maintenancesList) {
                     if(maintenance.maintained){
                         expenseTotal += maintenance.cost
                         maintenanceTotal += maintenance.cost
                     }

                }
            } else {
                Toast.makeText(requireActivity(), it.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
        })
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
                        Toast.makeText(activity, "stat "+stat.trips, Toast.LENGTH_SHORT).show()
                        //barEntriesList.add(BarEntry(i.toFloat(), stat.totalFareRevenue.toFloat()))
                        fareTotal += stat.totalFareRevenue
                        revenueTotal += stat.totalFareRevenue
                        fuelTotal += stat.totalFuelCost
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




    fun setPieChart(){
        val pie = AnyChart.pie()

        pie.setOnClickListener(object : ListenersInterface.OnClickListener(arrayOf("x", "value")) {
            override fun onClick(event: Event) {
                Toast.makeText(activity,
                    event.getData().get("x").toString() + ":" + event.getData().get("value"),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        val data: MutableList<DataEntry> = ArrayList()
        data.add(ValueDataEntry("Other Revenue", otherRevenue)) //blue
        data.add(ValueDataEntry("Fare",fareTotal)) //blue dark
        data.add(ValueDataEntry("Fuel",  fuelTotal)) //red
        data.add(ValueDataEntry("Maintenance",maintenanceTotal)) //yellow
        data.add(ValueDataEntry("Other Expense",otherExpense)) //black

        pie.data(data)

        pie.title("Income")

        pie.labels().position("outside")


        pie.legend().title().enabled(true)
        pie.legend().title()
            .text("Retail channels")
            .padding(0.0, 0.0, 10.0, 0.0)

        pie.legend()
            .position("center-bottom")
            .itemsLayout(LegendLayout.HORIZONTAL)
            .align(Align.CENTER)

        anyChartView.setChart(pie)

    }



     private fun setWaterfallData(){
         val waterfall: Waterfall = AnyChart.waterfall()

         waterfall.title("ACME corp. Revenue Flow 2017")

         waterfall.yScale().minimum(0.0)

         waterfall.yAxis(0).labels().format("\${%Value}{scale:(1000000)(1)|(mln)}")
         waterfall.labels().enabled(true)
         waterfall.labels().format(
             """function() {
              if (this['isTotal']) {
                return anychart.format.number(this.absolute, {
                  scale: true
                })
              }
        
              return anychart.format.number(this.value, {
                scale: true
              })
            }"""
         )

         val data: MutableList<DataEntry> = ArrayList()
         data.add(ValueDataEntry("Start", 23000000))
         data.add(ValueDataEntry("Jan", 2200000))
         data.add(ValueDataEntry("Feb", -4600000))
         data.add(ValueDataEntry("Mar", -9100000))
         data.add(ValueDataEntry("Apr", 3700000))
         data.add(ValueDataEntry("May", -2100000))
         data.add(ValueDataEntry("Jun", 5300000))
         data.add(ValueDataEntry("Jul", 3100000))
         data.add(ValueDataEntry("Aug", -1500000))
         data.add(ValueDataEntry("Sep", 4200000))
         data.add(ValueDataEntry("Oct", 5300000))
         data.add(ValueDataEntry("Nov", -1500000))
         data.add(ValueDataEntry("Dec", 5100000))
         val end = DataEntry()
         end.setValue("x", "End")
         end.setValue("isTotal", true)
         data.add(end)
         waterfall.data(data)
         waterfallChartView.setChart(waterfall)
     }


}