package com.example.test

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.test.driverInfo.DriverInfoFragment
import com.example.test.driverInfo.DriverStatsFragment
import com.example.test.driverInfo.DriverTodayFragment

class DriverInfoViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3;
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                return DriverInfoFragment()
            }
            1 -> {
                return DriverTodayFragment()
            }
            2 -> {
                return DriverStatsFragment()
            }
            else -> {
                return DriverInfoFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> {
                return "Info"
            }
            1 -> {
                return "Today"
            }
            2 -> {
                return "Stats"
            }
        }
        return super.getPageTitle(position)
    }

}



