package com.example.test

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.test.models.Vehicle
import com.example.test.models.VehicleStat
import com.example.test.repository.Repository
import com.google.android.material.tabs.TabLayout
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StatisticsActivity : AppCompatActivity() {
    private lateinit var backBtnImg:ImageView
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
        getSupportActionBar()?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = StatisticsViewPagerAdapter(supportFragmentManager)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)
        backBtnImg=  findViewById(R.id.back_button_img)
        backBtnImg.setOnClickListener{
            startActivity(Intent(this@StatisticsActivity,DriversActivity::class.java))
            animate()
        }
    }

    fun animate() {
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        );
    }


    fun getVehicleStats(startDate:String, endDate:String){
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        val viewModel= ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)
        // on below line we are initializing our list
        viewModel.getVehicleStats(startDate, endDate)
        try{
            viewModel.vehicleStatsResponse.observe(this
            ) {
                if (it.isSuccessful) {
                    vehiclesStats = it.body() as java.util.ArrayList<VehicleStat>
                    Log.d("LIST", vehiclesStats.size.toString())
                } else {
                    Toast.makeText(this, it.errorBody()?.string(), Toast.LENGTH_LONG)
                        .show()
                    Log.d("Error", it.errorBody()?.string()!!)
                }
            }


        }
        catch (e:Exception){
            Log.d("Exception",vehiclesStats.size.toString())
        }
    }
}