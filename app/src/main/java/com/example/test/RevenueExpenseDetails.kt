package com.example.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class RevenueExpenseDetails : AppCompatActivity() {
    private lateinit var backBtnImg: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revenue_expense_details)

        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = IncomeDetailsPagerAdapter(supportFragmentManager)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.setupWithViewPager(viewPager)

        backBtnImg = findViewById(R.id.back_button_img)
        backBtnImg.setOnClickListener{
            startActivity(Intent(this@RevenueExpenseDetails, StartActivity::class.java))
        }




    }
}