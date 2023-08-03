package com.example.test

import android.app.Fragment
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout

import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.test.databinding.ActivityStartBinding
import com.example.test.ui.slideshow.SlideshowFragment
import com.google.android.material.navigation.NavigationView

class StartActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityStartBinding
    lateinit var navController:NavController
    var reportsFragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionBar: Toolbar?
        actionBar = binding.appBarStart.toolbar
        // with color hash code as its parameter
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))
        // Set BackgroundDrawable
        actionBar?.setElevation(0F);
        actionBar?.setBackgroundDrawable(colorDrawable)
        setSupportActionBar(actionBar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
         navController = findNavController(R.id.nav_host_fragment_content_start)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_trips, R.id.nav_revenue, R.id.nav_reports, R.id.nav_demand_prediction, R.id.nav_support,
            ), drawerLayout
        )
        if(intent.getBooleanExtra("fromPdf", false)){
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_content_start, reportsFragment)
            transaction.commit()
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        if(intent.getBooleanExtra("fromPdf", false)){
            navController.navigate(R.id.nav_reports)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.start, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_start)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    //Add a route// Preview the route
    //Add Vehicle including assigning the vehicle to the route
    //Add Driver including assigning Driver to the vehicle
    //Performing some actions on the driver
        //Call the driver
        //Message the driver
        //Reassign the driver to a vehicle
    //Add a tracker for  the particular vehicle you've added
    //Track the vehicle on the app
        //Condition => From the start of the route start point or end route end point (either direction)
                       //Recorded automatically and inserted in the database
    //View the recorded trip
        //Information => startTime
                      //=> endTime
                      //=> route info
                      //=> estimate revenue collected
                      //=> estimate fuel consumed

    //View analytics based on the trips, including performance of vehicles and routes
    //Generate reports on the same










}