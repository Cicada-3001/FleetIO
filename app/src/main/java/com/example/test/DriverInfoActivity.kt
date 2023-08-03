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
import androidx.viewpager.widget.ViewPager
import com.example.test.models.Driver
import com.example.test.models.DriverAdd
import com.example.test.models.Vehicle
import com.example.test.util.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso


class DriverInfoActivity : AppCompatActivity() {
    private lateinit var backBtnImg:ImageView
    private lateinit var driverImg:ImageView
    private var imageUrl: String? = null

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
    }

    fun animate() {
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        );
    }




}






