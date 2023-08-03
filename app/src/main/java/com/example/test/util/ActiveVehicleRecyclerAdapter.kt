package com.example.test.util

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.VehicleRecyclerAdapter
import com.example.test.VehicleTrackingActivity
import com.example.test.models.Vehicle
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class ActiveVehicleRecyclerAdapter (private var vehiclesList: ArrayList<Vehicle>, var context: Context) : RecyclerView.Adapter<ActiveVehicleRecyclerAdapter.VehicleViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ActiveVehicleRecyclerAdapter.VehicleViewHolder {
        // this method is use to inflate the layout file
        // which we have created for our recycler view.
        // on below line we are inflating our layout file.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.active_vehicle_recycler,
            parent, false
        )
        // at last we are returning our view holder
        return VehicleViewHolder(itemView)
    }

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<Vehicle>) {
        vehiclesList = filterlist
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ActiveVehicleRecyclerAdapter.VehicleViewHolder, position: Int) {
        // on below line we are setting data to our text view and our image view.
        if (vehiclesList.get(position).imageUrl != null) {
            Picasso.get()
                .load(vehiclesList.get(position).imageUrl)
                .into(holder.vehImage)
        } else {
            Picasso.get()
                .load(vehiclesList.get(position).imageUrl)
                .placeholder(context.resources.getDrawable(R.drawable.default_van))//it will show placeholder image when url is not valid.
                .networkPolicy(NetworkPolicy.OFFLINE) //for caching the image url in case phone is offline
                .into(holder.vehImage)
        }
        holder.vehicleTypeTV.text = vehiclesList.get(position).vehicleType
        holder.vehPlateNumberTv.text ="Number Plate : "+vehiclesList.get(position).plateNumber
        holder.vehMakeTv.text = "Make : "+vehiclesList.get(position).make
        holder.itemView.setOnClickListener(View.OnClickListener {
            var intent = Intent(context, VehicleTrackingActivity::class.java)
            intent.putExtra("vehicleType", vehiclesList.get(position).vehicleType)
            intent.putExtra("plateNumber", vehiclesList.get(position).plateNumber)
            intent.putExtra("make", vehiclesList.get(position).make)
            intent.putExtra("model", vehiclesList.get(position).model)
            intent.putExtra("year", vehiclesList.get(position).year)
            intent.putExtra("vin", vehiclesList.get(position).vin)
            intent.putExtra("fuelType", vehiclesList.get(position).fuelType)
            intent.putExtra("odometerReading", vehiclesList.get(position).odometerReading)
            intent.putExtra("currentLocation", vehiclesList.get(position).currentLocation)
            intent.putExtra("availability", vehiclesList.get(position).availability)
            context.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        // on below line we are returning
        // our size of our list
        return vehiclesList.size
    }

    class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val vehicleTypeTV: TextView = itemView.findViewById(R.id.vehicleTypeTv)
        val vehPlateNumberTv: TextView = itemView.findViewById(R.id.vehiclePlateTv)
        val vehMakeTv: TextView = itemView.findViewById(R.id.vehicleMakeTv)
        val vehImage: ImageView = itemView.findViewById(R.id.vehicleIV)
    }


}