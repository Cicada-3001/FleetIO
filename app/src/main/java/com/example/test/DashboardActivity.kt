package com.example.test

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView

class DashboardActivity : AppCompatActivity(), View.OnClickListener  {
    private lateinit var vehicleCard: CardView
    private lateinit var driverCard: CardView
    private lateinit var routeCard: CardView
    private lateinit var maintenanceCard: CardView
    private lateinit var statisticsCard: CardView
    private lateinit var statsCardUpper: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_dashboard)

        vehicleCard = findViewById(R.id.vehicleCard)
        driverCard = findViewById(R.id.driverCard)
        routeCard = findViewById(R.id.routeCard)
        maintenanceCard = findViewById(R.id.maintenanceCard)
        statisticsCard = findViewById(R.id.statsCard)
        statsCardUpper = findViewById(R.id.statsCardUpper)
        vehicleCard.setOnClickListener(this)
        driverCard.setOnClickListener(this)
        routeCard.setOnClickListener(this)
        maintenanceCard.setOnClickListener(this)
        statisticsCard.setOnClickListener(this)
        statsCardUpper.setOnClickListener(this)
    }

    fun animate(){
        overridePendingTransition(R.anim.slide_in_right,
            R.anim.slide_out_left);
    }

    override fun onClick(v: View?) {
        var i: Intent

        if (v != null) {
            when (v.id) {
                R.id.vehicleCard -> {
                    i = Intent(this@DashboardActivity, VehiclesActivity::class.java)
                    startActivity(i)
                    animate()
                }
                R.id.driverCard -> {
                    i = Intent(this@DashboardActivity, DriverInfoActivity::class.java)
                    startActivity(i)
                    animate()
                }
                R.id.routeCard -> {
                    i = Intent(this@DashboardActivity, RoutesActivity::class.java)
                    startActivity(i)
                    animate()
                }
                R.id.maintenanceCard -> {
                    i = Intent(this@DashboardActivity, MaintenancesActivity::class.java)
                    startActivity(i)
                    animate()
                }
                R.id.statsCardUpper->{
                    i = Intent(this@DashboardActivity, StatisticsActivity::class.java)
                    startActivity(i)
                    animate()

                }
                else -> {
                    print("Error")
                }
            }
        }

    }

}






