package com.example.test.statistics.daystatistics

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.anychart.enums.*
import com.example.test.*
import com.example.test.databinding.FragmentDayStatisticsBinding
import com.example.test.models.*
import com.example.test.repository.Repository
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class DayStatistics : Fragment() {
    lateinit var barDataSet: BarDataSet
    var barEntriesList: ArrayList<BarEntry> = ArrayList<BarEntry>()
    lateinit var barData: BarData
    private var _binding: FragmentDayStatisticsBinding? = null

    var driversList: ArrayList<DriverStat> = ArrayList<DriverStat>()
    lateinit var incomeList: Income
    private lateinit var driverRV: RecyclerView
    private lateinit var viewModel: MainViewModel
    lateinit var pieChart: PieChart
    lateinit var pieChartView:AnyChartView
    lateinit var barChartView: BarChart
    lateinit var pie: Pie
    private val binding get() = _binding!!
    var vehiclesStats: ArrayList<VehicleStat> = ArrayList<VehicleStat>()


    @RequiresApi(Build.VERSION_CODES.O)
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @RequiresApi(Build.VERSION_CODES.O)
    val current = LocalDateTime.now().format(formatter)
    @RequiresApi(Build.VERSION_CODES.O)
    var startDate:String= current
    @RequiresApi(Build.VERSION_CODES.O)
    var endDate:String= current


    var revenueList: ArrayList<OtherRevenue> = ArrayList<OtherRevenue>()
    var maintenancesList: ArrayList<Maintenance> = ArrayList<Maintenance>()
    var expensesList = java.util.ArrayList<OtherExpense>()
    var expenseTotal:Double = 0.0
    var grossRevenue:Double = 0.0
    var fuelTotal:Double = 0.0
    var revenueTotal:Double = 0.0







    @RequiresApi(Build.VERSION_CODES.O)
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
        val slideshowViewModel =
            ViewModelProvider(this).get(DayStatisticsViewModel::class.java)
        _binding = FragmentDayStatisticsBinding.inflate(inflater, container, false)
        val view: View = binding.root
        // Inflate the layout for this fragment
       // val view = inflater.inflate(R.layout.fragment_day_statistics, container, false)
        // on below line we are initializing
        // our variable with their ids.

        // on below line we are initializing our views with their ids.
        driverRV= view.findViewById(R.id.driversRv)
        barChartView = view.findViewById(R.id.idBarChart)
        pieChart = view.findViewById(R.id.pieChart)
        setPieChart()

        var driverRankRecyclerAdapter = DriverRankRecyclerAdapter(driversList,requireActivity())
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

        driverRankRecyclerAdapter.notifyDataSetChanged()
            // the current activity will get finished.
      return view
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
                        barEntriesList.add(BarEntry(i.toFloat(), stat.totalFareRevenue.toFloat()))
                        setBarData()
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



    private fun setBarData(){
        // on below line we are adding data
        // to our bar entries list
        // on below line we are initializing our bar data set
        barDataSet = BarDataSet(barEntriesList, "Bar Chart Data")

        // on below line we are initializing our bar data
        barData = BarData(barDataSet)

        // on below line we are setting data to our bar chart
        barChartView.data = barData
        barChartView.xAxis.setDrawGridLines(false);
        barChartView.axisLeft.setDrawGridLines(false);
        barChartView.axisRight.setDrawGridLines(false);

        barChartView.axisRight.setDrawAxisLine(false);
        barChartView.axisLeft.setDrawAxisLine(false);
        barChartView.xAxis.setDrawAxisLine(false);

        barChartView.description.isEnabled = false;
        barChartView.legend.isEnabled = false;

        // on below line we are setting colors for our bar chart text
        barDataSet.valueTextColor = Color.LTGRAY

        // on below line we are setting color for our bar data set
        barDataSet.setColor(resources.getColor(R.color.quill_blue))

        // on below line we are setting text size
        barDataSet.valueTextSize = 12f

        // on below line we are enabling description as false
        barChartView.description.isEnabled = true
    }


    fun setPieChart(){
        // on below line we are setting user percent value,
        // setting description as enabled and offset for pie chart
        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        // on below line we are setting drag for our pie chart
        pieChart.setDragDecelerationFrictionCoef(0.95f)

        // on below line we are setting hole
        // and hole color for pie chart
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.WHITE)

        // on below line we are setting circle color and alpha
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        // on  below line we are setting hole radius
        pieChart.setHoleRadius(58f)
        pieChart.setTransparentCircleRadius(61f)

        // on below line we are setting center text
        pieChart.setDrawCenterText(true)

        // on below line we are setting
        // rotation for our pie chart
        pieChart.setRotationAngle(0f)

        // enable rotation of the pieChart by touch
        pieChart.setRotationEnabled(true)
        pieChart.setHighlightPerTapEnabled(true)

        // on below line we are setting animation for our pie chart
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        // on below line we are disabling our legend for pie chart
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        // on below line we are creating array list and
        // adding data to it to display in pie chart
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(70f))
        entries.add(PieEntry(20f))
        entries.add(PieEntry(10f))

        // on below line we are setting pie data set
        val dataSet = PieDataSet(entries, "Mobile OS")

        // on below line we are setting icons.
        dataSet.setDrawIcons(false)

        // on below line we are setting slice for pie
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors to list
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.quill_blue))
        colors.add(resources.getColor(R.color.yellow))
        colors.add(resources.getColor(R.color.quill_red))

        // on below line we are setting colors.
        dataSet.colors = colors

        // on below line we are setting pie data set
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.setData(data)

        // undo all highlights
        pieChart.highlightValues(null)

        // loading chart
        pieChart.invalidate()

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
                }
            } else {
                Log.d("Error",it.message().toString())
                Toast.makeText(requireActivity(), it.errorBody().toString(), Toast.LENGTH_SHORT).show()
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
                    //  if(maintenance.maintained)
                    expenseTotal += maintenance.cost
                }
            } else {
                Toast.makeText(requireActivity(), it.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }






}





