package com.example.test

import android.Manifest.permission.CALL_PHONE
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.service.controls.actions.FloatAction
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.VectorEnabledTintResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.test.driverInfo.DriverInfoFragment
import com.example.test.models.Driver
import com.example.test.models.DriverAdd
import com.example.test.models.Vehicle
import com.example.test.repository.Repository
import com.example.test.util.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import java.util.ArrayList
import java.util.HashMap


class DriverInfoActivity : AppCompatActivity(), DriverInfoFragment.OnButtonClickListener, OnItemClickListener {
    private lateinit var backBtnImg:ImageView
    private lateinit var driverImg:ImageView
    private var imageUrl: String? = null
    var vehiclesList = ArrayList<Vehicle>()
    lateinit var vehicleRecyclerAdapter: VehicleRecyclerAdapter
    private lateinit var vehicleSelect: RecyclerView
    private lateinit var viewModel: MainViewModel
    lateinit var dialog: BottomSheetDialog
    var vehicleId: String? = null
    var vehicleName: String? = null
    private lateinit var driverId: String
    private lateinit var firstName: String
    private lateinit var lastName: String
    lateinit var driver: DriverAdd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_info)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = DriverInfoViewPagerAdapter(supportFragmentManager)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.setupWithViewPager(viewPager)
        imageUrl = intent.getStringExtra("imageUrl").toString()
        driverImg =findViewById(R.id.profile_image)

        if(imageUrl != null){
            Picasso.get()
                .load(imageUrl)
                .into(driverImg)
        }else{
            Picasso.get()
                .load(imageUrl)
                .placeholder(this.resources.getDrawable(R.drawable.default_user))//it will show placeholder image when url is not valid.
                .networkPolicy(NetworkPolicy.OFFLINE) //for caching the image url in case phone is offline
                .into(driverImg)
        }

        backBtnImg=  findViewById(R.id.back_button_img)
        backBtnImg.setOnClickListener{
             startActivity(Intent(this@DriverInfoActivity,DriversActivity::class.java))
            animate()
        }

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        // on below line we are initializing our list
        viewModel.getVehicles()
    }

    fun animate() {
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        );
    }

    override fun onButtonClicked() {
        showBottomSheet()
    }


    fun showBottomSheet() {
        // on below line we are creating a new bottom sheet dialog
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
                        VehicleRecyclerAdapter(vehiclesList, this, 1, this)
                    vehicleSelect.adapter = vehicleRecyclerAdapter
                    Log.d("LIST", vehiclesList.size.toString())
                } else {
                    Toast.makeText(this, it.errorBody().toString(), Toast.LENGTH_SHORT)
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

    override fun onItemClick(objectRep: HashMap<String, String>) {
        dialog?.dismiss()
        vehicleName = (objectRep["name"])
        vehicleId = objectRep["id"]
        showAlertDialog()
    }


    fun showAlertDialog() {
        MaterialAlertDialogBuilder(
            this,
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
                                this,
                                "Assignment Successful",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
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


















}






