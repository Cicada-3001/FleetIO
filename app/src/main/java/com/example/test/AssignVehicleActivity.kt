package com.example.test

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.ActionBar

class AssignVehicleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_vehicle)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        val actionBar: ActionBar?
        actionBar = supportActionBar
        // with color hash code as its parameter
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))
        // Set BackgroundDrawable
        actionBar?.setElevation(0F)
        actionBar?.setBackgroundDrawable(colorDrawable)

    }















}