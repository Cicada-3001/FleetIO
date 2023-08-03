package com.example.test

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
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
import com.example.retrofittutorial.api.RetrofitInstance
import com.example.test.models.Driver
import com.example.test.models.DriverAdd
import com.example.test.models.Vehicle
import com.example.test.repository.Repository
import com.example.test.util.Constants.Companion.userId
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*


class AddDriverActivity : AppCompatActivity(), View.OnClickListener, OnItemClickListener {
    private lateinit var driverProfileImg: ImageView
    private lateinit var addDriverCard: CardView
    private lateinit var backBtnImg: ImageView
    private lateinit var addDriverBtn: Button
    private var PICK_IMAGE_REQUEST = 1000

    private lateinit var viewModel: MainViewModel
    private lateinit var firstNameEdt: EditText;
    private lateinit var lastNameEdt: EditText;
    private lateinit var licenseNumberEdt: EditText;
    private lateinit var licenseExpiryEdt: EditText;

    private lateinit var dateOfBirthEdt: EditText;
    private lateinit var phoneNumberEdt: EditText;
    private lateinit var emailEdt: EditText;
    private lateinit var vehicleEdt: EditText;
    private var inserted: Boolean = false
    var vehiclesList = ArrayList<Vehicle>()
    var vehicleRecyclerAdapter: VehicleRecyclerAdapter =
        VehicleRecyclerAdapter(vehiclesList, this, 1, this)
    private lateinit var vehicleSelect: RecyclerView
    var vehicleId: String? = null
    var imageUrl: String? = null
    var vehicleName: String? = null
    lateinit var dialogBottom: BottomSheetDialog


    var driversList: ArrayList<Driver> = ArrayList<Driver>()
    lateinit var dialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_add_driver)
        getSupportActionBar()?.hide();
        backBtnImg = findViewById(R.id.back_button_img)
        addDriverCard = findViewById(R.id.addDriverCard)
        backBtnImg.setOnClickListener(this)
        addDriverCard.setOnClickListener(this)
        firstNameEdt = findViewById(R.id.editTextFirstname)
        lastNameEdt = findViewById(R.id.editTextLastname)
        licenseNumberEdt = findViewById(R.id.editTextLicense)
        dateOfBirthEdt = findViewById(R.id.editTextDob)
        phoneNumberEdt = findViewById(R.id.editTextPhone)
        licenseExpiryEdt = findViewById(R.id.editTextLinExpiry)
        emailEdt = findViewById(R.id.editTextEmail)
        vehicleEdt = findViewById(R.id.editTextVehicle)
        addDriverBtn = findViewById(R.id.addDriverBtn)
        driverProfileImg = findViewById(R.id.addDriverProfile)
        addDriverBtn.setOnClickListener(this)
        dateOfBirthEdt.setOnClickListener(this)
        licenseExpiryEdt.setOnClickListener(this)
        vehicleEdt.setOnClickListener(this)
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

    }

    fun addDriver() {
        val firstName: String = firstNameEdt.text.toString()
        val lastName: String = lastNameEdt.text.toString()
        val licenseNumber: String = licenseNumberEdt.text.toString()
        val dateOfBirth: String = dateOfBirthEdt.text.toString()
        val phoneNumber: String = phoneNumberEdt.text.toString()
        val licenseExpiry: String = licenseExpiryEdt.text.toString()
        val email: String = emailEdt.text.toString()
        val vehicle: Vehicle? = null
        Toast.makeText(this, userId.toString(), Toast.LENGTH_SHORT).show()

        val newDriver = DriverAdd(
            userId!!,
            firstName,
            lastName,
            licenseNumber,
            licenseExpiry,
            dateOfBirth,
            phoneNumber,
            email,
            imageUrl,
            vehicleId
        )
        viewModel.addDriver(newDriver)
        viewModel.driversResponse.observe(this, Observer {
            showProgressDialog()
            if (it.isSuccessful) {
                driversList = it.body() as ArrayList<Driver>
                inserted = true
            } else {
                Toast.makeText(this, it.errorBody()?.string(), Toast.LENGTH_LONG).show()
                Log.d("Error", it.errorBody()?.string()!!)
            }
        })

    }

    fun assignVehicle(){
        if(vehicleId !=null && driversList[0]._id != null)
            viewModel.assignVehicle(vehicleId!!, driversList[0]._id!!)
            viewModel.driversResponse.observe(this, Observer {
                showProgressDialog()
                if (it.isSuccessful) {
                    driversList = it.body() as ArrayList<Driver>
                    inserted = true
                } else {
                    Toast.makeText(this, it.errorBody()?.string(), Toast.LENGTH_LONG).show()
                    Log.d("Error", it.errorBody()?.string()!!)
                }
            })
    }

    fun animate() {
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        );
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
                    driverProfileImg.setImageURI(
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


    //an activity for result that comes after the image has been selected
    /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == PICK_IMAGE_REQUEST) {
                    var selectedImageUri: Uri? = data?.data
                    // Get the path from the Uri
                    val path: String = getPathFromURI(selectedImageUri)!!
                    if (path != null) {
                        val f = File(path)
                        selectedImageUri = Uri.fromFile(f)
                        Toast.makeText(this, "The uri "+selectedImageUri, Toast.LENGTH_SHORT).show()
                    }
                    // Set the image in ImageView
                     driverProfileImg.setImageURI(
                        selectedImageUri
                    )
                }
            }else {
                Toast.makeText(this, "An error occurred while selecting the image", Toast.LENGTH_SHORT).show()
            }
        } catch (e: java.lang.Exception) {
            Log.e("FileSelectorActivity", "File select error", e)
        }
    }*/

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
                    .into(driverProfileImg)
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
                    val i = Intent(this@AddDriverActivity, DriversActivity::class.java)
                    startActivity(i)
                    animate()
                }

                R.id.addDriverCard -> {
                    showOptionsDialog()
                }
                R.id.addDriverBtn -> {
                    addDriver()
                }
                R.id.editTextVehicle -> {
                    showBottomSheet()
                }
                R.id.editTextDob -> {
                    showDatePicker(dateOfBirthEdt)
                }
                R.id.editTextLinExpiry -> {
                    showDatePicker(licenseExpiryEdt)
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
        val dialog = builder.create()
        dialog.show()

        Handler().postDelayed({
            dialog.dismiss()
            if (inserted)
                Toast.makeText(applicationContext, "Driver added !", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(applicationContext, "Something went wrong !", Toast.LENGTH_SHORT)
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


    fun showDatePicker(editText: EditText) {
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

                editText.setText(date)
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


    fun showBottomSheet() {
        // on below line we are creating a new bottom sheet dialog.
        dialogBottom = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.select_vehicle_bottom_sheet, null)
        vehicleSelect = view.findViewById(R.id.vehiclesRV)

        // on below line we are initializing our list
        vehiclesList = ArrayList()
        viewModel.getVehicles()

        try {
            viewModel.vehiclesResponse.observe(this, Observer {
                if (it.isSuccessful) {
                    vehiclesList = it.body() as ArrayList<Vehicle>
                    vehicleRecyclerAdapter = VehicleRecyclerAdapter(vehiclesList, this, 1, this)
                    vehicleSelect.adapter = vehicleRecyclerAdapter
                    Log.d("LIST", vehiclesList.size.toString())
                } else {
                    Toast.makeText(this, it.errorBody().toString(), Toast.LENGTH_SHORT).show()
                }

            })
        } catch (e: Exception) {
            Log.d("Exception", vehiclesList.size.toString())
        }

        Log.d("LIST", vehiclesList.size.toString())
        // on below line we are setting adapter to our recycler view.
        vehicleRecyclerAdapter.notifyDataSetChanged()

        // closing of dialog box when clicking on the screen.
        dialogBottom.setCancelable(true)
        // content view to our view.
        dialogBottom.setContentView(view)
        dialogBottom.show()
    }


    override fun onItemClick(objectRep: HashMap<String, String>) {
        dialogBottom?.dismiss()
        Toast.makeText(this, objectRep["name"], Toast.LENGTH_SHORT).show()
        vehicleEdt.setText(objectRep["name"])
        vehicleId = objectRep["id"]
        vehicleName = objectRep["name"]
    }

}










