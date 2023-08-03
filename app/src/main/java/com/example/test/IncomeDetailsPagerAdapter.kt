package com.example.test

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.test.driverInfo.DriverInfoFragment
import com.example.test.driverInfo.DriverStatsFragment
import com.example.test.driverInfo.DriverTodayFragment
import com.example.test.revenueExpense.Expense
import com.example.test.revenueExpense.Revenue

class IncomeDetailsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                return Expense()
            }
            1 -> {
                return Revenue()
            }
            else -> {
                return Expense()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> {
                return "Expenses"
            }
            1 -> {
                return "Revenue"
            }
        }
        return super.getPageTitle(position)
    }







}