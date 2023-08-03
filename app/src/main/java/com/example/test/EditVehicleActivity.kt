package com.example.test

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.*
import com.example.test.repository.Repository
import com.example.test.util.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.time.LocalDateTime

class EditVehicleActivity : AppCompatActivity(), View.OnClickListener, OnItemClickListener {
    private lateinit var vehicleProfileImg: ImageView
    private lateinit var backButton:ImageView
    private lateinit var addVehicleCard: CardView
    private lateinit var vehicleTypeEdt: EditText;
    private lateinit var plateNumberEdt: EditText;
    private lateinit var makeEdt: EditText;
    private lateinit var modelEdt: EditText;
    private lateinit var yearEdt: EditText;
    private lateinit var vinEdt: EditText;
    private lateinit var fuelTypeEdt: AutoCompleteTextView;
    private lateinit var routeEdt: EditText;
    private lateinit var currentLocationEdt: EditText
    private lateinit var odometerEdt: EditText
    private lateinit var availabilityEdt: EditText
    private lateinit var viewModel: MainViewModel
    private lateinit var backBtnImg: ImageView
    private lateinit var editVehicleBtn: Button
    private lateinit var fuelConsumptionEdt: EditText
    private lateinit var seatCapacityEdt: EditText
    lateinit var vehicle_id:String
    private lateinit var vehicleType: String
    private lateinit var plateNumber: String
    private lateinit var make:String
    private lateinit var model:String
    private lateinit var year:String
    private lateinit var vin:String
    private lateinit var fuelType:String
    private var odometerReading:Double = 0.0
    private lateinit var currentLocation:String
    private lateinit var availability:String
    private lateinit var routeStartPoint:String
    private lateinit var routeEndPoint:String
    private var fuelConsumption:Double = 0.0
    private lateinit var seatCapacity:String



    private var PICK_IMAGE_REQUEST = 1000
    private lateinit var routesAdapter: ArrayAdapter<Route>
    var routesList: ArrayList<Route> = ArrayList<Route>()
    var vehiclesList: ArrayList<Vehicle> = ArrayList<Vehicle>()
    var fuelTypesList: ArrayList<FuelType> = ArrayList<FuelType>()
    var inserted:Boolean = false
    val driver: String? = null
    val route: String? = null
    lateinit var dialog: BottomSheetDialog
    private lateinit var routesSelect: RecyclerView
    var routeId: String? = null
    var routesRecyclerAdapter: RoutesRecyclerAdapter = RoutesRecyclerAdapter(routesList,this,1,this)
    var imageUrl: String? = null
    lateinit var i:Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_edit_vehicle)
        getSupportActionBar()?.hide();
        vehicleTypeEdt  =findViewById(R.id.editTextVehType)
        plateNumberEdt=findViewById(R.id.editTextPlateNo)
        availabilityEdt= findViewById(R.id.editTextAvailability)
        makeEdt=findViewById(R.id.editTextMake)
        modelEdt=findViewById(R.id.editTextModel)
        yearEdt=findViewById(R.id.editTextYear)
        routeEdt= findViewById(R.id.editTextRoute)
        vinEdt=findViewById(R.id.editTextVin)
        fuelTypeEdt=findViewById(R.id.editTextFuelType)
        odometerEdt=findViewById(R.id.editTextOdometer)
        vehicleProfileImg = findViewById(R.id.addVehicleProfile)
        currentLocationEdt= findViewById(R.id.editTextCurrentLocation)
        fuelConsumptionEdt= findViewById(R.id.editTextFuelConsumption)
        seatCapacityEdt = findViewById(R.id.editTextSeatCapacity)
        backBtnImg = findViewById(R.id.back_button_img)
        backBtnImg.setOnClickListener(this)

        vehicleType= intent.getStringExtra("vehicleType").toString()
        plateNumber= intent.getStringExtra("plateNumber").toString()
        make= intent.getStringExtra("make").toString()
        model = intent.getStringExtra("model").toString()
        year = intent.getStringExtra("year").toString()
        vin = intent.getStringExtra("vin").toString()
        fuelType = intent.getStringExtra("fuelType").toString()
        odometerReading = intent.getDoubleExtra("odometerReading",0.0)
        currentLocation= intent.getStringExtra("currentLocation").toString()
        availability= intent.getStringExtra("availability").toString()
        imageUrl = intent.getStringExtra("imageUrl").toString()
        routeStartPoint = intent.getStringExtra("routeStartPoint").toString()
        routeEndPoint = intent.getStringExtra("routeEndPoint").toString()
        fuelConsumption = intent.getDoubleExtra("fuelConsumption",0.0)
        seatCapacity = intent.getStringExtra("seatCapacity").toString()
        fuelType = intent.getStringExtra("fuelType").toString()
        vehicle_id = intent.getStringExtra("vehicle_id").toString()





        i= Intent(this@EditVehicleActivity, VehicleInfoActivity::class.java)

        vehicleTypeEdt.setText(vehicleType)
        plateNumberEdt.setText(plateNumber)
        availabilityEdt.setText(availability)
        makeEdt.setText(make)
        modelEdt.setText(model)
        yearEdt.setText(year)
        routeEdt.setText(routeStartPoint+ " - "+routeEndPoint)
        vinEdt.setText(vin)
        fuelTypeEdt.setText(fuelType)
        odometerEdt.setText(odometerReading.toString())
        currentLocationEdt.setText(currentLocation)
        fuelConsumptionEdt.setText(fuelConsumption.toString())
        seatCapacityEdt.setText(seatCapacity)

        Log.d("Current Location", intent.getStringExtra("currentLocation")!!)

        //availability and current location
        //vehicleRecyclerAdapter
        //VehicleTracking2
        //VehicleInfo
        //EditVehicle

        //fuelType
        //availability= string
        //currentLocation  = string
        //seatCapacity
        //fuelConsumption

        i.putExtra("driverImageUrl", intent.getStringExtra("driverImageUrl"))
        i.putExtra("driverFirstname", intent.getStringExtra("driverFirstname"))
        i.putExtra("driverLastname", intent.getStringExtra("driverLastname"))
        i.putExtra("routeStartPoint",  intent.getStringExtra("routeStartPoint"))
        i.putExtra("routeEndPoint", intent.getStringExtra("routeEndPoint"))
        i.putExtra("operationArea", intent.getStringExtra("operationArea"))
       // i.putExtra("geofenceRadius", intent.getStringExtra("geofenceRadius"))
        /* Project report and uploading of the documentation by Monday */
        backBtnImg=findViewById(R.id.back_button_img)
        editVehicleBtn= findViewById(R.id.editVehicleBtn)
        addVehicleCard = findViewById(R.id.addVehicleCard)
        backBtnImg.setOnClickListener(this)
        editVehicleBtn.setOnClickListener(this)
        addVehicleCard.setOnClickListener(this)
        routeEdt.setOnClickListener(this)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        getFuelTypes()


    }


    fun getFuelTypes(){
        viewModel.getFuelTypes()
        viewModel.fuelTypesResponse.observe(this, Observer{
            if(it.isSuccessful){
                fuelTypesList= it.body() as ArrayList<FuelType>
                // in our case pass the context, drop down layout , and array.
                val fuelsAdapter = ArrayAdapter(this, R.layout.drop_down_item, fuelTypesList)
                // set adapter to the autocomplete tv to the arrayAdapter
                fuelTypeEdt.setAdapter(fuelsAdapter)
            }else{
                Toast.makeText(this,it.errorBody().toString(),Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun updateVehicle(){
        vehicleType = vehicleTypeEdt.text.toString()
        plateNumber = plateNumberEdt.text.toString()
        make = makeEdt.text.toString()
        model = modelEdt.text.toString()
        year = yearEdt.text.toString()
        vin = vinEdt.text.toString()
        fuelType = getId(fuelTypeEdt.text.toString())!!
        odometerReading = odometerEdt.text.toString().toDoubleOrNull()!!
        currentLocation = currentLocationEdt.text.toString()
        availability = availabilityEdt.text.toString()
        fuelConsumption= fuelConsumptionEdt.text.toString().toDouble()
        seatCapacity = seatCapacityEdt.text.toString()
        imageUrl =  null


        val newVehicle= VehicleAdd(Constants.userId!!,vehicleType,plateNumber,make,model,year,vin,fuelType,odometerReading,currentLocation,availability, driver, routeId, fuelConsumption, imageUrl, seatCapacity.toInt())
        Log.d("update state", newVehicle.toString())
        viewModel.updateVehicle(vehicle_id,newVehicle)
        viewModel.vehicleResponse.observe(this, Observer{
            showProgressDialog()
            if(it.isSuccessful){
                val vehicle =  it.body() as VehicleAdd
                inserted = true
            }else{
                Toast.makeText(this,it.errorBody()?.string(),Toast.LENGTH_SHORT).show()
                Log.d("vehicle ADD ERROR", it.errorBody()?.string()!!)
                }
        })
    }

    private fun getId(arg:String): String? {
        when(arg) {
            "Petroleum" -> return fuelTypesList.get(0)._id
            "Diesel" -> return fuelTypesList.get(0)._id
            "Electric" -> fuelTypesList.get(0)._id
            else -> print("I don't know anything about it")
        }
        return null
    }





    //get routes
    //show the routes
    //show the progress button
    //show the success message
    //confirm update


    fun showBottomSheet(){
        // on below line we are creating a new bottom sheet dialog.
        dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.select_route_bottom_sheet, null)
        routesSelect= view.findViewById(R.id.routesRV)
        // on below line we are initializing our list
        routesList = ArrayList()

        try{
            // on below line we are initializing our list
            viewModel.getRoutes()
            viewModel.routesResponse.observe(this, Observer{
                if(it.isSuccessful){
                    routesList= it.body() as ArrayList<Route>
                    routesRecyclerAdapter = RoutesRecyclerAdapter(routesList,this,1,this)
                    routesSelect.setAdapter(routesRecyclerAdapter)

                }else{
                    Toast.makeText(this,it.errorBody().toString(), Toast.LENGTH_SHORT).show()
                }

            })
        }catch (e:Exception){
            Log.d("Exception",routesList.size.toString())
        }
        Log.d("LIST", routesList.size.toString())
        // on below line we are setting adapter to our recycler view.
        routesRecyclerAdapter.notifyDataSetChanged()
        // closing of dialog box when clicking on the screen.
        dialog.setCancelable(true)
        // content view to our view.
        dialog.setContentView(view)
        dialog.show()
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
        tvText.text = "Updating ..."
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
                Toast.makeText(applicationContext,"Vehicle Updated !", Toast.LENGTH_SHORT).show()
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


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.back_button_img ->{
                i.putExtra("vehicle_id", intent.getStringExtra("vehicle_id"))
                i.putExtra("vehicleType",vehicleType)
                i.putExtra("plateNumber",plateNumber)
                i.putExtra("make", make)
                i.putExtra("model",model)
                i.putExtra("year", year)
                i.putExtra("vin", vin)
                i.putExtra("fuelType", fuelType)
                i.putExtra("odometerReading",odometerReading)
                i.putExtra("currentLocation",currentLocation)
                i.putExtra("availability",availability)
                i.putExtra("imageUrl",imageUrl)
                i.putExtra("fuelType", fuelType)
                i.putExtra("seatCapacity", seatCapacity)
                i.putExtra("fuelConsumption", fuelConsumption)
                startActivity(i)
            }

            R.id.editVehicleBtn ->{
                updateVehicle()
                //updateVehicle()
            }
            R.id.editTextRoute ->{
                showBottomSheet()

            }
            R.id.back_button_img ->{

            }
        }
    }

    override fun onItemClick(objectRep: HashMap<String, String>) {
        dialog?.dismiss()
        Toast.makeText(this, objectRep["name"],Toast.LENGTH_SHORT).show()
        routeEdt.setText(objectRep["name"])
        routeId = objectRep["id"]
    }



}