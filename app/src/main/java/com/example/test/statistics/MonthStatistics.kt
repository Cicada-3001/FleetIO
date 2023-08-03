package com.example.test.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Waterfall
import com.example.test.R


class MonthStatistics : Fragment() {
    private lateinit var waterfallChartView: AnyChartView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_month_statistics, container, false)
        waterfallChartView= view.findViewById(R.id.waterfallChart)
        setWaterfallData()
        return view;
    }


     private fun setWaterfallData(){
         val waterfall: Waterfall = AnyChart.waterfall()

         waterfall.title("ACME corp. Revenue Flow 2017")

         waterfall.yScale().minimum(0.0)

         waterfall.yAxis(0).labels().format("\${%Value}{scale:(1000000)(1)|(mln)}")
         waterfall.labels().enabled(true)
         waterfall.labels().format(
             """function() {
              if (this['isTotal']) {
                return anychart.format.number(this.absolute, {
                  scale: true
                })
              }
        
              return anychart.format.number(this.value, {
                scale: true
              })
            }"""
         )

         val data: MutableList<DataEntry> = ArrayList()
         data.add(ValueDataEntry("Start", 23000000))
         data.add(ValueDataEntry("Jan", 2200000))
         data.add(ValueDataEntry("Feb", -4600000))
         data.add(ValueDataEntry("Mar", -9100000))
         data.add(ValueDataEntry("Apr", 3700000))
         data.add(ValueDataEntry("May", -2100000))
         data.add(ValueDataEntry("Jun", 5300000))
         data.add(ValueDataEntry("Jul", 3100000))
         data.add(ValueDataEntry("Aug", -1500000))
         data.add(ValueDataEntry("Sep", 4200000))
         data.add(ValueDataEntry("Oct", 5300000))
         data.add(ValueDataEntry("Nov", -1500000))
         data.add(ValueDataEntry("Dec", 5100000))
         val end = DataEntry()
         end.setValue("x", "End")
         end.setValue("isTotal", true)
         data.add(end)
         waterfall.data(data)
         waterfallChartView.setChart(waterfall)
     }


}