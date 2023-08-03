package com.example.test

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.*
import com.example.test.repository.Repository
import com.example.test.util.Constants.Companion.userId
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso

class AddVehicleActivity : AppCompatActivity(), View.OnClickListener, OnItemClickListener {
    private lateinit var vehicleProfileImg: ImageView
    private lateinit var addVehicleCard: CardView
    private lateinit var vehicleTypeEdt: EditText;
    private lateinit var plateNumberEdt: EditText;
    private lateinit var makeEdt: EditText;
    private lateinit var modelEdt: EditText;
    private lateinit var yearEdt: EditText;
    private lateinit var vinEdt: EditText;
    private lateinit var fuelTypeEdt: AutoCompleteTextView;
    private lateinit var routeEdt: EditText;
    private lateinit var currentLocationEdt:EditText
    private lateinit var odometerEdt: EditText
    private lateinit var availabilityEdt:EditText
    private lateinit var viewModel: MainViewModel
    private lateinit var backBtnImg: ImageView
    private lateinit var addVehicleBtn: Button
    private lateinit var fuelConsumptionEdt:EditText
    private lateinit var seatCapacityEdt:EditText
    private var PICK_IMAGE_REQUEST = 1000
    private lateinit var routesAdapter:ArrayAdapter<Route>
    var routesList: ArrayList<Route> = ArrayList<Route>()
    var vehiclesList: ArrayList<Vehicle> = ArrayList<Vehicle>()
    var fuelTypesList: ArrayList<FuelType> = ArrayList<FuelType>()
    var inserted:Boolean = false
    val driver: String? = null
    val route: String? = null
    lateinit var dialog:BottomSheetDialog
    private lateinit var routesSelect: RecyclerView
    var routeId: String? = null
    var routesRecyclerAdapter: RoutesRecyclerAdapter = RoutesRecyclerAdapter(routesList,this,1,this)
    var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_add_vehicle)
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
        backBtnImg=findViewById(R.id.back_button_img)
        addVehicleBtn= findViewById(R.id.addVehicleBtn)
        addVehicleCard = findViewById(R.id.addVehicleCard)
        backBtnImg.setOnClickListener(this)
        addVehicleBtn.setOnClickListener(this)
        addVehicleCard.setOnClickListener(this)
        routeEdt.setOnClickListener(this)
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

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

    private fun getId(arg:String): String? {
        when(arg) {
            "Petroleum" -> return fuelTypesList.get(0)._id
            "Diesel" -> return fuelTypesList.get(0)._id
            "Electric" -> fuelTypesList.get(0)._id
            else -> print("I don't know anything about it")
        }
        return null
    }

    private fun addVehicle(){
        val vehicleType: String = vehicleTypeEdt.text.toString()
        val plateNumber: String = plateNumberEdt.text.toString()
        val make: String = makeEdt.text.toString()
        val model: String = modelEdt.text.toString()
        val year: String = yearEdt.text.toString()
        val vin: String = vinEdt.text.toString()
        val fuelType = getId(fuelTypeEdt.text.toString())
        Toast.makeText(this, fuelType, Toast.LENGTH_LONG).show()
        Log.d("fuelTypeId", fuelType!!)
        val odometerReading: Double = odometerEdt.text.toString().toDoubleOrNull()!!
        val currentLocation:String = currentLocationEdt.text.toString()
        val availability:String = availabilityEdt.text.toString()
        val fuelConsumptionRate= fuelConsumptionEdt.text.toString().toDouble()
        val seatCapacity = seatCapacityEdt.text.toString().toInt()
        val imageUrl: String? =  null


         val newVehicle= VehicleAdd(userId!!,vehicleType,plateNumber,make,model,year,vin,fuelType,odometerReading,currentLocation,availability, driver, routeId, fuelConsumptionRate, imageUrl, seatCapacity)
          Log.d("add state", newVehicle.toString())
          viewModel.addVehicle(newVehicle)
          viewModel.vehiclesResponse.observe(this, Observer{
               showProgressDialog()
              if(it.isSuccessful){
                  vehiclesList= it.body() as ArrayList<Vehicle>
                  inserted = true
              }else{
                  Toast.makeText(this,it.errorBody()?.string(),Toast.LENGTH_SHORT).show()
                  Log.d("vehicle ADD ERROR", it.errorBody()?.string()!!)
              }
          })
    }

    fun animate() {
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        );
    }


    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                if (result.resultCode == Activity.RESULT_OK) {

                    var selectedImageUri: Uri? = result?.data?.data
                    // Get the path from the Uri
                    /*val path: String = getPathFromURI(selectedImageUri)!!
                    if (path != null) {
                        val f = File(path)
                        selectedImageUri = Uri.fromFile(f)
                        Toast.makeText(this, "The uri " + selectedImageUri.toString(), Toast.LENGTH_SHORT)
                            .show()
                        Log.d("Image URI", "The image uri is"+selectedImageUri.toString())
                    }*/
                    // Set the image in ImageView
                    vehicleProfileImg.setImageURI(
                        selectedImageUri
                    )
                } else {
                    Toast.makeText(
                        this,
                        "An error occurred while selecting the image",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("ERROR", "An error occured")
                }
            } catch (e: java.lang.Exception) {
                Log.e("FileSelectorActivity", "File select error", e)
            }
        }


    // Select Image method
    private fun selectImage() {
        // Defining Implicit Intent to mobile gallery
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        /* startActivityForResult(
             Intent.createChooser(intent, "Select Image"),
             PICK_IMAGE_REQUEST
         )*/
        resultLauncher.launch(intent)
    }










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


    fun getPathFromURI(contentUri: Uri?): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(contentUri!!, proj, null, null, null)
        if (cursor?.moveToFirst() == true) {
            val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
        }
        cursor?.close()
        return res
    }

    private fun showOptionsDialog() {
        val uploadBtn: Button
        val closeBtn: Button
        val imageUrlEdt: EditText
        val urlBtn: Button

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_image_selector)
        // if button is clicked, close the custom dialog
        uploadBtn = dialog.findViewById(R.id.uploadBtn)
        closeBtn = dialog.findViewById(R.id.closeBtn)
        imageUrlEdt = dialog.findViewById(R.id.imageUrlEdt)
        urlBtn = dialog.findViewById(R.id.urlBtn)


        uploadBtn.setOnClickListener {
            selectImage()
            dialog.dismiss()
        }

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        urlBtn.setOnClickListener {
            imageUrl = imageUrlEdt.text.toString()
            dialog.findViewById<ProgressBar>(R.id.progress).visibility = View.VISIBLE
            Handler().postDelayed({
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(this.resources.getDrawable(R.drawable.default_user))
                    .into(vehicleProfileImg)
                dialog.dismiss()
            }, 3000)
        }


        imageUrlEdt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (imageUrlEdt.text.toString().trim() == null || imageUrlEdt.text.toString()
                        .trim() == ""
                ) {
                    closeBtn.visibility = View.VISIBLE
                    uploadBtn.visibility = View.VISIBLE
                    urlBtn.visibility = View.GONE
                } else {
                    closeBtn.visibility = View.GONE
                    uploadBtn.visibility = View.GONE
                    urlBtn.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {

            }
        })
        dialog.show()
    }




    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.back_button_img -> {
                    val i =Intent(this@AddVehicleActivity, VehiclesActivity::class.java)
                    startActivity(i)
                    animate()
                }
                R.id.addVehicleCard -> {
                    showOptionsDialog()
                }
                R.id.addVehicleBtn->{
                    addVehicle()
                }

                R.id.editTextRoute->{
                    showBottomSheet()
                }

                else -> {
                    print("Error")
                }
            }
        }
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
        val dialog=builder.create()
        dialog.show()

        Handler().postDelayed( {
            dialog.dismiss()
            if(inserted)
                Toast.makeText(applicationContext,"Vehicle added !", Toast.LENGTH_SHORT).show()
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

    override fun onItemClick(objectRep: HashMap<String, String>) {
        dialog?.dismiss()
        Toast.makeText(this, objectRep["name"],Toast.LENGTH_SHORT).show()
        routeEdt.setText(objectRep["name"])
        routeId = objectRep["id"]
    }

}