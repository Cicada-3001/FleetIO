package com.example.test

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.OtherRevenue
import com.example.test.models.VehicleStat
import kotlin.math.roundToInt

class VehStatRecAdapter(private var vehicleStats: ArrayList<VehicleStat>, val context: Context, var viewType:Int )  : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var itemView: View? = null

        when(viewType){
            0 -> {
                val itemView = LayoutInflater.from(parent.context).inflate(
                    R.layout.layout_fare_card,
                    parent, false
                )
                // at last we are returning our view holder
                return VehStatViewHolder(itemView)
            }
            1->{
                // below line we are inflating user message layout.
                itemView = LayoutInflater.from(parent.context).inflate(
                    R.layout.layout_fuel_card,
                    parent, false
                )
                // at last we are returning our view holder
                return FuelCardHolder(itemView)

            }
        }
        return VehStatViewHolder(itemView!!)
    }

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<VehicleStat>) {
        vehicleStats= filterlist
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (viewType) {
            0 -> {
                holder as VehStatViewHolder
                holder.fareDesc.text = (vehicleStats.get(position).vehicle.route?.startPoint + " - " + vehicleStats.get(
                        position
                    ).vehicle.route?.endPoint).substring(0, 20)
                holder.fareDate.text =
                    vehicleStats.get(position).vehicle.make + " " + vehicleStats.get(position).vehicle.model
                holder.fareAmt.text =
                    (((vehicleStats.get(position).totalFareRevenue) * 100).roundToInt() / 100).toString()
            }
            1 -> {
                holder as FuelCardHolder
                holder.fuelDesc.text = vehicleStats.get(position).vehicle.make + " - " + vehicleStats.get(position).vehicle.model
                holder.fuelDate.text = vehicleStats.get(position).vehicle.make + " " + vehicleStats.get(position).vehicle.model
                holder.fuelAmt.text =
                    (((vehicleStats.get(position).totalFuelCost) * 100).roundToInt() / 100).toString()

            }


        }
    }

    override fun getItemViewType(position: Int): Int {
        // below line of code is to set position.
        return when (viewType) {
            0 -> 0
            1 -> 1
            else -> 0
        }
    }
    override fun getItemCount(): Int {
        // on below line we are returning
        // our size of our list
        if(vehicleStats.size > 6)
            return 6
        else
            return vehicleStats.size
    }

    class VehStatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val fareDesc: TextView = itemView.findViewById(R.id.fareDescTv)
        val fareDate: TextView = itemView.findViewById(R.id.fareDateTv)
        val fareAmt: TextView = itemView.findViewById(R.id.fareAmtTv)
    }

    class FuelCardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val fuelDesc: TextView = itemView.findViewById(R.id.fuelDescTv)
        val fuelDate: TextView = itemView.findViewById(R.id.fuelDateTv)
        val fuelAmt: TextView = itemView.findViewById(R.id.fuelAmtTv)
    }








}