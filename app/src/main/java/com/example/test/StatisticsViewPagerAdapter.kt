package com.example.test

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.test.statistics.daystatistics.DayStatistics
import com.example.test.statistics.MonthStatistics
import com.example.test.statistics.WeekStatistics

class StatisticsViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3;
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                return DayStatistics()
            }
            1 -> {
                return WeekStatistics()
            }
            2 -> {
                return MonthStatistics()
            }
            else -> {
                return DayStatistics()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> {
                return "Day"
            }
            1 -> {
                return "Week"
            }
            2 -> {
                return "Month"
            }
        }
        return super.getPageTitle(position)
    }

}



