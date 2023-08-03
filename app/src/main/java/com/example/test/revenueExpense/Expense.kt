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
import com.example.test.models.Maintenance
import com.example.test.models.OtherExpense
import com.example.test.models.OtherRevenue
import com.example.test.models.VehicleStat
import com.example.test.repository.Repository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


class Expense : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var maintenanceRev: RecyclerView
    private lateinit var fuelRev:RecyclerView
    private lateinit var otherRev: RecyclerView

    private var maintenanceTotal:Double = 0.0
    private var fuelTotal:Double = 0.0
    private var otherTotal:Double = 0.0
    private lateinit var otherTotalTv:TextView
    private lateinit var maintenanceTotalTv:TextView
    private lateinit var fuelTotalTv:TextView

    var expensesList: ArrayList<OtherExpense> = ArrayList<OtherExpense>()
    var maintenancesList: ArrayList<Maintenance> = ArrayList<Maintenance>()
    var vehicleStats: ArrayList<VehicleStat> = ArrayList<VehicleStat>()
    lateinit var maintenanceRecAdapter: MaintenanceRecyclerAdapter
    var vehiclesStats: ArrayList<VehicleStat> = ArrayList<VehicleStat>()
    lateinit var expenseRecAdapter: OtherExpenseRecAdapter
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
        // viewModel.getIncome()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_expense, container, false)

        otherTotalTv = view.findViewById(R.id.otherTotalTv)
        maintenanceTotalTv= view.findViewById(R.id.maintenanceTotalTv)
        fuelTotalTv = view.findViewById(R.id.fuelTotalTv)
        otherRev = view.findViewById(R.id.recOthers)
        maintenanceRev = view.findViewById(R.id.recMaintenances)
        fuelRev = view.findViewById(R.id.recFuel)

        getOtherExpense()
        getMaintenance()
        getFuelCost()
        return view
    }


    private fun getOtherExpense(){
        // on below line we are initializing our list
        expensesList = ArrayList()
        viewModel.getOtherExpenses()
        viewModel.expensesResponse.observe(viewLifecycleOwner, Observer {
            if (it.isSuccessful) {
                expensesList = it.body() as ArrayList<OtherExpense>
                for (expense in expensesList) {
                    otherTotal += expense.amount
                }
                otherTotalTv.text= ((otherTotal * 100).roundToInt()/100).toString() + " Ksh"
                expenseRecAdapter = OtherExpenseRecAdapter(expensesList,requireActivity())
                otherRev.layoutManager = LinearLayoutManager(requireActivity())
                otherRev.adapter = expenseRecAdapter
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
                maintenancesList = it.body() as ArrayList<Maintenance>
                maintenanceRecAdapter = MaintenanceRecyclerAdapter(maintenancesList, requireActivity(),1)
                maintenanceRev.layoutManager = LinearLayoutManager(requireActivity())
                maintenanceRev.adapter = maintenanceRecAdapter
                for (maintenance in maintenancesList) {
                    if(maintenance.maintained)
                        maintenanceTotal += maintenance.cost
                }
                maintenanceTotalTv.text= ((maintenanceTotal * 100).roundToInt()/100).toString() + " Ksh"
            } else {
                Toast.makeText(requireActivity(), it.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getFuelCost(){
        viewModel.getVehicleStats(startDate, endDate)
        viewModel.vehicleStatsResponse.observe(viewLifecycleOwner
        ) {
            if (it.isSuccessful) {
                vehiclesStats = it.body() as ArrayList<VehicleStat>
                for (stat in vehiclesStats) {
                    fuelTotal += stat.totalFuelCost
                }
                fuelTotalTv.text= ((fuelTotal * 100).roundToInt()/100).toString() + " Ksh"
                vehStatRecAdapter = VehStatRecAdapter(vehiclesStats,requireActivity(),1)
                fuelRev.layoutManager = LinearLayoutManager(requireActivity())
                fuelRev.adapter =  vehStatRecAdapter

            } else {
                Toast.makeText(requireActivity(), it.errorBody()?.string(), Toast.LENGTH_LONG)
                    .show()
                Log.d("Error", it.errorBody()?.string()!!)
            }
        }
    }
}