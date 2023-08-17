package com.example.test

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.Maintenance
import com.example.test.models.MaintenanceAdd
import com.example.test.models.Vehicle
import com.example.test.repository.Repository
import com.example.test.util.Constants
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MaintenanceInfoActivity : AppCompatActivity(), View.OnClickListener, OnItemClickListener{
    private lateinit var viewModel: MainViewModel
    private lateinit var vehicleEdt: EditText;
    private lateinit var maintenanceTypeEdt: EditText;
    private lateinit var dateEdt: EditText;
    private lateinit var costEdt: EditText;
    private lateinit var descriptionEdt: EditText;

    lateinit var maintenanceId:String
    lateinit var vehicle:String
    lateinit var maintenanceType:String
    lateinit var date:String
    lateinit var cost:String
    lateinit var description:String

    private lateinit var backBtnImg: ImageView
    private lateinit var addMaintenanceBtn: Button
    var maintenancesList: ArrayList<Maintenance> = ArrayList<Maintenance>()
    var inserted:Boolean = false
    var maintained:Boolean =  true
    var vehiclesList = ArrayList<Vehicle>()
    var  vehicleRecyclerAdapter: VehicleRecyclerAdapter = VehicleRecyclerAdapter(vehiclesList,this,1,this)
    private lateinit var vehicleSelect: RecyclerView
    var vehicleId: String? = null
    lateinit var dialog:BottomSheetDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide();
        setContentView(R.layout.activity_maintenance_info)
        vehicleEdt  =findViewById(R.id.editTextVehicle)
        maintenanceTypeEdt=findViewById(R.id.editTextMaintenanceType)
        dateEdt=findViewById(R.id.editTextDate)
        costEdt=findViewById(R.id.editTextCost)
        descriptionEdt=findViewById(R.id.editTextDescription)
        backBtnImg=findViewById(R.id.back_button_img)
        addMaintenanceBtn=findViewById(R.id.addMaintenanceBtn)
        backBtnImg.setOnClickListener(this)
        addMaintenanceBtn.setOnClickListener(this)
        val repository = Repository()

        maintenanceId = intent?.getStringExtra("maintenanceId").toString()
        vehicle= intent?.getStringExtra("vehicleType").toString()
        maintenanceType = intent?.getStringExtra("maintenanceType").toString()
        date= intent?.getStringExtra("date").toString()
        cost= intent?.getDoubleExtra("cost",0.00).toString()
        description= intent?.getStringExtra("description").toString()


        vehicleEdt.setText(vehicle)
        maintenanceTypeEdt.setText(maintenanceType)
        dateEdt.setText(date)
        costEdt.setText(cost)
        descriptionEdt.setText(description)

        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        // on below line we are initializing our list
        viewModel.getVehicles()
        dateEdt.setOnClickListener(this)
        vehicleEdt.setOnClickListener(this)


    }


    fun  showDatePicker() {
        // the instance of our calendar.
        val c = Calendar.getInstance()
        // on below line we are getting
        // our day, month and year.
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        // on below line we are creating a
        // variable for date picker dialog.
        val datePickerDialog = DatePickerDialog(
            // on below line we are passing context.
            this,
            { view, year, monthOfYear, dayOfMonth ->
                // on below line we are setting
                // date to our edit text.
                val date = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                dateEdt.setText(date)
            },
            // on below line we are passing year, month
            // and day for the selected date in our date picker.
            year,
            month,
            day
        )
        // to display our date picker dialog.
        datePickerDialog.show()
    }


    fun showBottomSheet(){
        // on below line we are creating a new bottom sheet dialog.
        dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.select_vehicle_bottom_sheet, null)
        vehicleSelect= view.findViewById(R.id.vehiclesRV)

        // on below line we are initializing our list
        vehiclesList = ArrayList()
        viewModel.getVehicles()

        try{
            viewModel.vehiclesResponse.observe(this, Observer{
                if(it.isSuccessful){
                    vehiclesList= it.body() as ArrayList<Vehicle>
                    vehicleRecyclerAdapter = VehicleRecyclerAdapter(vehiclesList,this,1,this)
                    vehicleSelect.adapter = vehicleRecyclerAdapter
                    Log.d("LIST",vehiclesList.size.toString())
                }else{
                    Toast.makeText(this,it.errorBody().toString(),Toast.LENGTH_SHORT).show()
                }

            })
        }catch (e:Exception){
            Log.d("Exception",vehiclesList.size.toString())
        }
        Log.d("LIST",vehiclesList.size.toString())
        // on below line we are setting adapter to our recycler view.
        vehicleRecyclerAdapter.notifyDataSetChanged()
        // closing of dialog box when clicking on the screen.
        dialog.setCancelable(true)
        // content view to our view.
        dialog.setContentView(view)
        dialog.show()
    }


    fun updateMaintenance(){
        var maintenance:Maintenance
        val maintenanceType: String = maintenanceTypeEdt.text.toString()
        val date: String = dateEdt.text.toString()
        val cost: Double = costEdt.text.toString().toDoubleOrNull()!!
        val description: String = descriptionEdt.text.toString()
        val newMaintenance =  MaintenanceAdd(
            Constants.userId!!,
            vehicleId!!, maintenanceType, date, cost, description, maintained)
        Log.d("TAG", "I have been clicked")
        viewModel.updateMaintenance(maintenanceId,newMaintenance)
        viewModel.maintenanceResponse.observe(this, Observer{
            if(it.isSuccessful){
                maintenance= it.body() as Maintenance
            }else {
                Log.d("ERROR", it.errorBody()!!.string())
                Toast.makeText(this, it.errorBody()!!.string(), Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun animate() {
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        );
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.back_button_img -> {
                    val i = Intent(this@MaintenanceInfoActivity, MaintenancesActivity::class.java)
                    startActivity(i)
                    animate()
                }
                R.id.editTextVehicle -> {
                    showBottomSheet()
                }
                R.id.editTextDate -> {
                    showDatePicker()
                }

                R.id.addMaintenanceBtn->{
                    updateMaintenance()
                }
                else -> {
                    print("Error")
                }
            }
        }
    }


    // Function to display ProgressBar
    // inside AlertDialog
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
        val dialog: AlertDialog = builder.create()
        dialog.show()

        Handler().postDelayed( {
            dialog.dismiss()
            if(inserted)
                Toast.makeText(applicationContext,"Vehicle added !", Toast.LENGTH_LONG).show()
            else
                Toast.makeText(applicationContext,"Something went wrong !", Toast.LENGTH_LONG).show()

        }, 3000.toLong() )

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

    override fun onItemClick(objectRep: HashMap<String, String>) {
        dialog?.dismiss()
        Toast.makeText(this, objectRep["name"], Toast.LENGTH_SHORT).show()
        vehicleEdt.setText(objectRep["name"])
        vehicleId = objectRep["id"]
    }






}



