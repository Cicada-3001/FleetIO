package com.example.test.driverInfo

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.test.*
import com.example.test.models.Driver
import com.example.test.models.DriverAdd
import com.example.test.models.Vehicle
import com.example.test.repository.Repository
import com.example.test.util.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.ContextUtils
import java.util.*


class DriverInfoFragment : Fragment(), View.OnClickListener, OnItemClickListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var driverFirstname: EditText
    private lateinit var driverLastname: EditText
    private lateinit var driverPhone: EditText
    private lateinit var driverEmail: EditText
    private lateinit var driverLicense: EditText
    private lateinit var driverDOB: EditText
    private lateinit var driverVehicle: EditText
    private lateinit var licenseExpiryEdt: EditText
    private lateinit var driverId: String
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var vehId: String
    private lateinit var licenseNumber: String
    private lateinit var dateOfBirth: String
    private lateinit var phoneNumber: String
    private lateinit var email: String
    private lateinit var vehicle: String
    private lateinit var licenseExpiry: String
    private lateinit var imageUrl: String
    private lateinit var updateBtn:Button
    private var buttonClickListener: OnButtonClickListener? = null
    private lateinit var menuFb: FloatingActionButton
    private lateinit var vehFb: FloatingActionButton
    private lateinit var msgFb: FloatingActionButton
    private lateinit var callFb: FloatingActionButton
    var vehiclesList = ArrayList<Vehicle>()
    lateinit var vehicleRecyclerAdapter: VehicleRecyclerAdapter
    private lateinit var vehicleSelect: RecyclerView
    var vehicleId: String? = null
    var vehicleName: String? = null
    var driversList: ArrayList<Driver> = ArrayList<Driver>()
    lateinit var dialog: BottomSheetDialog
    lateinit var driver: DriverAdd
    var inserted = false


    val phone_intent = Intent(Intent.ACTION_CALL)
    // Set data of Intent through Uri by parsing phone number

    val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = activity?.intent
        driverId = intent?.getStringExtra("driverId").toString()
        firstName = intent?.getStringExtra("firstName").toString()
        lastName = intent?.getStringExtra("lastName").toString()
        licenseNumber = intent?.getStringExtra("licenseNumber").toString()
        dateOfBirth = intent?.getStringExtra("dateOfBirth").toString()
        phoneNumber = intent?.getStringExtra("phoneNumber").toString()
        email = intent?.getStringExtra("email").toString()
        vehicle = intent?.getStringExtra("vehicle").toString()
        licenseExpiry = intent?.getStringExtra("licenseExpiry").toString()
        licenseExpiry = intent?.getStringExtra("vehId").toString()


        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        // on below line we are initializing our list
         viewModel.getVehicles()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_driver_info, container, false)

        driverFirstname = view.findViewById(R.id.editFirstName)
        driverLastname = view.findViewById(R.id.editLastname)
        driverPhone = view.findViewById(R.id.editPhone)
        driverEmail = view.findViewById(R.id.editEmailAddress)
        driverLicense = view.findViewById(R.id.editLicense)
        driverDOB = view.findViewById(R.id.editDOB)
        driverVehicle = view.findViewById(R.id.editVehicle)
        licenseExpiryEdt = view.findViewById(R.id.editLicenseExpiry)
        updateBtn = view.findViewById(R.id.updateBtn)
        
        updateBtn.setOnClickListener(this)

        driverFirstname.setText(firstName)
        driverLastname.setText(lastName)
        driverPhone.setText(phoneNumber)
        driverEmail.setText(email)
        driverLicense.setText(licenseNumber)
        driverDOB.setText(dateOfBirth)
        driverVehicle.setText(vehicle)
        licenseExpiryEdt.setText(licenseExpiry)


        licenseExpiryEdt.setOnClickListener(this)
        driverDOB.setOnClickListener(this)

        phone_intent.data = Uri.parse("tel:+254"+phoneNumber.drop(1))
        menuFb = view.findViewById(R.id.driver_options_fab)
        vehFb = view.findViewById(R.id.assign_veh_fab)
        msgFb = view.findViewById(R.id.message_driver_fab)
        callFb = view.findViewById(R.id.call_driver_fab)
        menuFb.setOnClickListener(this)
        vehFb.setOnClickListener(this)
        callFb.setOnClickListener(this)


        return view
    }

    fun animate() {
        activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    interface OnButtonClickListener {
        fun onButtonClicked()
    }

    // Call this method when the button is clicked
    private fun buttonClickHandler() {
        if (buttonClickListener != null) {
            buttonClickListener!!.onButtonClicked()
        }
    }


    override fun onAttach(@NonNull context: Context) {
        super.onAttach(context)
        buttonClickListener = if (context is OnButtonClickListener) {
            context
        } else {
            throw ClassCastException(
                context.toString().toString() + " must implement OnButtonClickListener"
            )
        }
    }



    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.driver_options_fab -> {
                    if (vehFb.visibility == View.GONE) {
                        vehFb.visibility = View.VISIBLE
                        msgFb.visibility = View.VISIBLE
                        callFb.visibility = View.VISIBLE
                    } else {
                        vehFb.visibility = View.GONE
                        msgFb.visibility = View.GONE
                        callFb.visibility = View.GONE
                    }
                }
                R.id.back_button_img -> {
                    val i = Intent(activity, DriversActivity::class.java)
                    startActivity(i)
                    animate()
                }

                R.id.call_driver_fab -> {
                    if (ContextUtils.getActivity(activity)?.let {
                            ContextCompat.checkSelfPermission(
                                it,
                                Manifest.permission.CALL_PHONE
                            )
                        } == PackageManager.PERMISSION_GRANTED) {
                        startActivity(phone_intent);
                    } else {
                        requestPermissions(
                            arrayOf(Manifest.permission.CALL_PHONE),
                            PERMISSION_REQUEST_CODE
                        );
                    }
                }

                R.id.driver_options_fab -> {
                    if (vehFb.visibility == View.GONE) {
                        vehFb.visibility = View.VISIBLE
                        msgFb.visibility = View.VISIBLE
                        callFb.visibility = View.VISIBLE
                    } else {
                        vehFb.visibility = View.GONE
                        msgFb.visibility = View.GONE
                        callFb.visibility = View.GONE
                    }
                }
                R.id.assign_veh_fab -> {
                    showBottomSheet()
                  //  buttonClickHandler()
                }

                R.id.editDOB -> {
                    showDatePicker(driverDOB)
                }
                R.id.editLicenseExpiry -> {
                    showDatePicker(licenseExpiryEdt)
                }

                R.id.assign_veh_fab -> {
                    showBottomSheet()
                }


                R.id.updateBtn -> {
                    updateDriver()
                }

                else -> {
                    print("Error")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // Get the result from intent
                val result = activity?.intent?.getStringExtra("result")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startActivity(phone_intent)
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


    fun showBottomSheet() {
        // on below line we are creating a new bottom sheet dialog.
        dialog = BottomSheetDialog(requireActivity())
        val view = layoutInflater.inflate(R.layout.select_vehicle_bottom_sheet, null)
        vehicleSelect = view.findViewById(R.id.vehiclesRV)
        // on below line we are initializing our list
        vehiclesList = ArrayList()
        viewModel.getVehicles()
        try {
            viewModel.vehiclesResponse.observe(this, Observer {
                if (it.isSuccessful) {
                    vehiclesList = it.body() as ArrayList<Vehicle>
                    vehicleRecyclerAdapter =
                        VehicleRecyclerAdapter(vehiclesList, requireActivity(), 1, this)
                    vehicleSelect.adapter = vehicleRecyclerAdapter
                    Log.d("LIST", vehiclesList.size.toString())
                } else {
                    Toast.makeText(requireActivity(), it.errorBody().toString(), Toast.LENGTH_SHORT)
                        .show()
                }

            })
        } catch (e: Exception) {
            Log.d("Exception", vehiclesList.size.toString())
        }
        Log.d("LIST", vehiclesList.size.toString())
        // on below line we are setting adapter to our recycler view.
        vehicleRecyclerAdapter.notifyDataSetChanged()
        // closing of dialog box when clicking on the screen.
        dialog.setCancelable(true)
        // content view to our view.
        dialog.setContentView(view)
        dialog.show()
    }


    fun showAlertDialog() {
        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Background
        )
            .setTitle("Confirm Vehicle Assignment")
            .setMessage("Assign " + firstName + " " + lastName+ " to " + vehicleName)
            .setPositiveButton("Assign") { _, _ ->
                viewModel.assignVehicle(driverId, vehicleId!!)
                try {
                    viewModel.driverResponse.observe(this, Observer {
                        if (it.isSuccessful) {
                            driver = it.body() as DriverAdd
                            Toast.makeText(
                                requireActivity(),
                                "Assignment Successful",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireActivity(),
                                it.errorBody()?.string(),
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("Error", it.errorBody()?.string()!!)
                        }
                    })
                } catch (e: Exception) {
                    print(e.toString())
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    override fun onItemClick(objectRep: HashMap<String, String>) {
        dialog?.dismiss()
        vehicleName = (objectRep["name"])
        vehicleId = objectRep["id"]
        showAlertDialog()
    }

    fun  updateDriver(){
        val firstName: String = driverFirstname.text.toString()
        val lastName: String =  driverLastname.text.toString()
        val licenseNumber: String = driverLicense.text.toString()
        val dateOfBirth: String = driverDOB.text.toString()
        val phoneNumber: String = driverPhone.text.toString()
        val email: String = driverEmail.text.toString()
        val imageUrl: String? = null
        val licenseExpiry: String? = licenseExpiryEdt.text.toString()

        val newDriver =  DriverAdd(
            Constants.userId!!, firstName, lastName, licenseNumber, licenseExpiry, dateOfBirth,phoneNumber,email,
            imageUrl,vehicleId
        )
        viewModel.updateDriver(driverId, newDriver)
        viewModel.driverResponse.observe(this, Observer{
            showProgressDialog()
            if(it.isSuccessful){
                inserted= true
                driver =  it.body() as DriverAdd
                Log.d("LIST",driversList.size.toString())
            }else{
                Toast.makeText(requireActivity(),it.errorBody()?.string(),Toast.LENGTH_LONG).show()
                Log.d("Error",it.errorBody()?.string()!!)
            }
        })
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
            requireContext(),
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


















    @SuppressLint("SetTextI18n")
    fun showProgressDialog() {
        // Creating a Linear Layout
        val linearPadding = 30
        val linear = LinearLayout(requireActivity())
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
        val progressBar = ProgressBar(requireActivity())
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, linearPadding, 0)
        progressBar.layoutParams = linearParam
        linearParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        linearParam.gravity = Gravity.CENTER
        // Creating a TextView inside the layout
        val tvText = TextView(requireActivity())
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
        val dialog=builder.create()
        dialog.show()

        Handler().postDelayed( {
            dialog.dismiss()
            if(inserted)
                Toast.makeText(requireContext(),"Vehicle added !", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(requireContext(),"Something went wrong !", Toast.LENGTH_SHORT).show()

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












}


















