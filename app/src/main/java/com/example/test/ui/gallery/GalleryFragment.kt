package com.example.test.ui.gallery

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.*
import com.example.test.databinding.FragmentGalleryBinding
import com.example.test.models.*
import com.example.test.repository.Repository
import com.example.test.util.Constants
import com.github.mikephil.charting.data.BarEntry
import com.squareup.picasso.Picasso
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class GalleryFragment : Fragment(),View.OnClickListener {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    lateinit var income: Income
    private lateinit var viewModel: MainViewModel
    private lateinit var totalIncome:TextView
    private lateinit var totalExpense:TextView
    private lateinit var netRevenue:TextView
    private lateinit var cashInBtn:TextView
    private lateinit var cashOutBtn:TextView
    private lateinit var viewDetailsTv:TextView
    var revenueList: ArrayList<OtherRevenue> = ArrayList<OtherRevenue>()
    var maintenancesList: ArrayList<Maintenance> = ArrayList<Maintenance>()



    private var inserted: Boolean = false
    var expensesList = java.util.ArrayList<OtherExpense>()
    var revenuesList = java.util.ArrayList<OtherRevenue>()
    var expenseTotal:Double = 0.0
    var revenueTotal:Double = 0.0
    var grossRevenue:Double = 0.0
    var vehiclesStats: ArrayList<VehicleStat> = ArrayList<VehicleStat>()
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val view: View = binding.root
        totalIncome  =view.findViewById(R.id.totalInNumberTv)
        totalExpense =view.findViewById(R.id.totalOutNumberTv)
        netRevenue = view.findViewById(R.id.balNumberTv)
        cashInBtn =view.findViewById(R.id.cashInBtn)
        cashOutBtn = view.findViewById(R.id.cashOutBtn)
        viewDetailsTv = view.findViewById(R.id.detailsTv)
        viewDetailsTv.setOnClickListener(this)
        cashOutBtn.setOnClickListener(this)
        cashInBtn.setOnClickListener(this)
        getOtherExpense()
        getMaintenance()
        getOtherRevenues()
        getVehicleStats()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getVehicleStats(){
        // on below line we are initializing our list
        viewModel.getVehicleStats(startDate, endDate )
        try{
            viewModel.vehicleStatsResponse.observe(viewLifecycleOwner
            ) {
                if (it.isSuccessful) {
                    vehiclesStats = it.body() as ArrayList<VehicleStat>
                    Log.d("LIST", vehiclesStats.size.toString())
                    Log.d("Stat response", "The response was successful")
                    for (stat in vehiclesStats) {
                        revenueTotal += stat.totalFareRevenue
                        expenseTotal += stat.totalFuelCost
                        grossRevenue += stat.grossRevenue
                    }
                    totalIncome.text= ((revenueTotal * 100).roundToInt()/100).toString() + " Ksh"
                    totalExpense.text = ((expenseTotal * 100).roundToInt()/100).toString()+ " Ksh"
                    netRevenue.text = ((grossRevenue * 100).roundToInt()/100).toString()+ " Ksh"
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



    @SuppressLint("SetTextI18n")
    fun showProgressDialog() {
        // Creating a Linear Layout
        val linearPadding = 30
        val linear = LinearLayout(activity)
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
        val progressBar = ProgressBar(activity)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, linearPadding, 0)
        progressBar.layoutParams = linearParam
        linearParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        linearParam.gravity = Gravity.CENTER

        // Creating a TextView inside the layout
        val tvText = TextView(activity)
        tvText.text = "Saving ..."
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20f
        tvText.layoutParams = linearParam
        linear.addView(progressBar)
        linear.addView(tvText)

        // Setting the AlertDialog Builder view
        // as the Linear layout created above
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setCancelable(true)
        builder.setView(linear)

        // Displaying the dialog
        val dialog = builder.create()
        dialog.show()

        Handler().postDelayed({
            dialog.dismiss()
            if (inserted)
                Toast.makeText(activity, "Entity added !", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(activity, "Something went wrong !", Toast.LENGTH_SHORT)
                    .show()

        }, 4000.toLong())

        val window: Window? = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
            // Disabling screen touch to avoid exiting the Dialog
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }

    fun animate(){
        activity?.overridePendingTransition(R.anim.slide_in_right,
            R.anim.slide_out_left);
    }

    private fun addOtherRevOrExp(description: String, amount:Double, created:String, type:Int){
        if(type== 0){
            val newExpense  =  OtherExpense(
                Constants.userId!!,
                description,
                amount,
                created,
            )
            viewModel.addOtherExpense(newExpense)
            viewModel.expensesResponse.observe(this, Observer {
                showProgressDialog()
                if (it.isSuccessful) {
                    expensesList = it.body() as ArrayList<OtherExpense>
                    inserted = true
                } else {
                    Toast.makeText(activity, it.errorBody()?.string(), Toast.LENGTH_LONG).show()
                    Log.d("Error", it.errorBody()?.string()!!)
                }
            })
        }else{
            val newRevenue  =  OtherRevenue(
                Constants.userId!!,
                description,
                amount,
                created,
            )
            viewModel.addOtherRevenue(newRevenue)
            viewModel. revenueResponse.observe(this, Observer {
                showProgressDialog()
                if (it.isSuccessful) {
                    revenuesList = it.body() as ArrayList<OtherRevenue>
                    inserted = true
                } else {
                    Toast.makeText(activity, it.errorBody()?.string(), Toast.LENGTH_LONG).show()
                    Log.d("Error", it.errorBody()?.string()!!)
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showOptionsDialog(view: View) {
        val uploadBtn: Button
        val closeBtn: ImageView
        val descriptionEdt: EditText
        val amountEdt: EditText
        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.layout_cashinout)
        // if button is clicked, close the custom dialog
        uploadBtn = dialog.findViewById(R.id.uploadBtn)
        closeBtn = dialog.findViewById(R.id.closeBtn)
        descriptionEdt = dialog.findViewById(R.id.descEdt)
        amountEdt = dialog.findViewById(R.id.amtEdt)
        uploadBtn.setOnClickListener {
            val description = descriptionEdt?.text.toString()
            val amount = amountEdt?.text.toString().toDouble()
            var formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy")
            var date = LocalDate.now()
            var formattedDate = date.format(formatter)
            val created = formattedDate


            if (view.id == R.id.cashInBtn) {


                addOtherRevOrExp(description, amount, created, 1)
            } else {
                addOtherRevOrExp(description, amount, created, 0)
            }
        }

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.cashOutBtn -> {
                    showOptionsDialog(v)
                }
                R.id.cashInBtn -> {
                    showOptionsDialog(v)
                }
                R.id.detailsTv->{
                    startActivity(Intent(requireActivity(), RevenueExpenseDetails::class.java))
                    animate()
                }
            }
        }
    }



}