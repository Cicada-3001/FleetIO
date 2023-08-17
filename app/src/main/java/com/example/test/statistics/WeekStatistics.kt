package com.example.test.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.chart.common.listener.Event
import com.anychart.chart.common.listener.ListenersInterface
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Bar
import com.anychart.core.cartesian.series.JumpLine
import com.anychart.data.Mapping
import com.anychart.enums.*
import com.example.test.R


class WeekStatistics : Fragment() {
    private lateinit var verticalCharView: AnyChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_week_statistics, container, false)
        verticalCharView= view.findViewById(R.id.verticalBarChart);
        setBarData()
        return view;
    }


    private fun setBarData(){
        val vertical: Cartesian = AnyChart.vertical()
        vertical.animation(true)
            .title("Vertical Combination of Bar and Jump Line Chart")
        val data: MutableList<DataEntry> = ArrayList()
        data.add(CustomDataEntry("Jan", 11.5, 9.3))
        data.add(CustomDataEntry("Feb", 12, 10.5))
        data.add(CustomDataEntry("Mar", 11.7, 11.2))
        data.add(CustomDataEntry("Apr", 12.4, 11.2))
        data.add(CustomDataEntry("May", 13.5, 12.7))
        data.add(CustomDataEntry("Jun", 11.9, 13.1))
        data.add(CustomDataEntry("Jul", 14.6, 12.2))
        data.add(CustomDataEntry("Aug", 17.2, 12.2))
        data.add(CustomDataEntry("Sep", 16.9, 10.1))
        data.add(CustomDataEntry("Oct", 15.4, 14.5))
        data.add(CustomDataEntry("Nov", 16.9, 14.5))
        data.add(CustomDataEntry("Dec", 17.2, 15.5))
        val set = com.anychart.data.Set.instantiate()
        set.data(data)
        val barData: Mapping = set.mapAs("{ x: 'x', value: 'value' }")
        val jumpLineData: Mapping = set.mapAs("{ x: 'x', value: 'jumpLine' }")
        val bar: Bar = vertical.bar(barData)
        bar.labels().format("\${%Value} mln")




        val jumpLine: JumpLine = vertical.jumpLine(jumpLineData)
        jumpLine.stroke("2 #60727B")
        jumpLine.labels().enabled(false)
        vertical.yScale().minimum(0.0)
        vertical.labels(true)
        vertical.tooltip()
            .displayMode(TooltipDisplayMode.UNION)
            .positionMode(TooltipPositionMode.POINT)
            .unionFormat(
                """function() {
      return 'Plain: $' + this.points[1].value + ' mln' +
        '\n' + 'Fact: $' + this.points[0].value + ' mln';
    }"""
            )
        vertical.interactivity().hoverMode(HoverMode.BY_X)
        vertical.xAxis(true)
        vertical.yAxis(true)
        vertical.yAxis(0).labels().format("\${%Value} mln")
        verticalCharView.setChart(vertical)
    }



    private class CustomDataEntry(x: String?, value: Number?, jumpLine: Number?) :
        ValueDataEntry(x, value) {
        init {
            setValue("jumpLine", jumpLine)
        }
    }















}
