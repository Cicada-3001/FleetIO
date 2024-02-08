package com.example.test.util

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test.*
import com.example.test.models.Vehicle
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class ActiveVehicleRecyclerAdapter (private var vehiclesList: ArrayList<Vehicle>, var viewType: Int, var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        var itemView: View? = null
        when(viewType){
            0 -> {
                // below line we are inflating user message layout.

                val itemView = LayoutInflater.from(parent.context).inflate(
                    R.layout.active_vehicle_recycler,
                    parent, false
                )
                return  VehicleViewHolder(itemView)

            }
            1->{
                // below line we are inflating user message layout.
                itemView = LayoutInflater.from(parent.context).inflate(
                    R.layout.preview_vehicle_recycler,
                    parent, false
                )
                // at last we are returning our view holder
                return VehiclePreviewHolder(itemView)

            }
        }
        return VehicleViewHolder(itemView!!)



    }

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<Vehicle>) {
        vehiclesList = filterlist
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // on below line we are setting data to our text view and our image view.
        when (viewType) {
            0 -> {
                if (vehiclesList.get(position).imageUrl != null) {
                    Picasso.get()
                        .load(vehiclesList.get(position).imageUrl)
                        .into((holder as VehicleViewHolder).vehImage)
                } else {
                    Picasso.get()
                        .load(vehiclesList.get(position).imageUrl)
                        .placeholder(context.resources.getDrawable(R.drawable.default_van))//it will show placeholder image when url is not valid.
                        .networkPolicy(NetworkPolicy.OFFLINE) //for caching the image url in case phone is offline
                        .into((holder as VehicleViewHolder).vehImage)
                }
                holder.vehicleTypeTV.text = vehiclesList.get(position).vehicleType
                holder.vehPlateNumberTv.text ="Number Plate : "+vehiclesList.get(position).plateNumber
                holder.vehMakeTv.text = "Make : "+vehiclesList.get(position).make
                holder.itemView.setOnClickListener(View.OnClickListener {
                    var intent = Intent(context, VehicleTracking2::class.java)
                    intent.putExtra("vehicle_id", vehiclesList.get(position)._id)
                    intent.putExtra("vehicleType", vehiclesList.get(position).vehicleType)
                    intent.putExtra("plateNumber", vehiclesList.get(position).plateNumber)
                    intent.putExtra("make", vehiclesList.get(position).make)
                    intent.putExtra("model", vehiclesList.get(position).model)
                    intent.putExtra("year", vehiclesList.get(position).year)
                    intent.putExtra("vin", vehiclesList.get(position).vin)
                    intent.putExtra("odometerReading", vehiclesList.get(position).odometerReading)
                    intent.putExtra("currentLocation", vehiclesList.get(position).currentLocation)
                    intent.putExtra("availability", vehiclesList.get(position).availability)
                    intent.putExtra("fuelType",vehiclesList.get(position).fuelType?.name)
                    intent.putExtra("imageUrl", vehiclesList.get(position).imageUrl)
                    intent.putExtra("driverImageUrl", vehiclesList.get(position).driver?.imageUrl)
                    intent.putExtra("driverFirstname", vehiclesList.get(position).driver?.firstName)
                    intent.putExtra("driverLastname", vehiclesList.get(position).driver?.lastName)
                    intent.putExtra("driverPhone", vehiclesList.get(position).driver?.phoneNumber)
                    intent.putExtra("routeStartPoint", vehiclesList.get(position).route?.startPoint)
                    intent.putExtra("routeEndPoint", vehiclesList.get(position).route?.endPoint)
                    intent.putExtra("operationArea", vehiclesList.get(position).operationArea)
                    intent.putExtra("geofenceRadius", vehiclesList.get(position).geofenceRadius)
                    intent.putExtra("fuelConsumption", vehiclesList.get(position).fuelConsumptionRate)
                    intent.putExtra("seatCapacity", vehiclesList.get(position).seatCapacity)
                    intent
                    context.startActivity(intent)
                })
            }
            1-> {
                if (vehiclesList.get(position).imageUrl != null) {
                    Picasso.get()
                        .load(vehiclesList.get(position).imageUrl)
                        .into((holder as VehiclePreviewHolder).vehImage)
                } else {
                    Picasso.get()
                        .load(vehiclesList.get(position).imageUrl)
                        .placeholder(context.resources.getDrawable(R.drawable.default_van))//it will show placeholder image when url is not valid.
                        .networkPolicy(NetworkPolicy.OFFLINE) //for caching the image url in case phone is offline
                        .into((holder as VehiclePreviewHolder).vehImage)
                }
                holder.vehicleTypeTV.text = vehiclesList.get(position).vehicleType
                holder.vehPlateNumberTv.text ="Number Plate : "+vehiclesList.get(position).plateNumber
                holder.vehMakeTv.text = "Make : "+vehiclesList.get(position).make
                holder.itemView.setOnClickListener(View.OnClickListener {
                    var intent= Intent(context, VehicleInfoActivity::class.java)
                    intent.putExtra("vehicle_id", vehiclesList.get(position)._id)
                    intent.putExtra("vehicleType", vehiclesList.get(position).vehicleType)
                    intent.putExtra("plateNumber", vehiclesList.get(position).plateNumber)
                    intent.putExtra("make", vehiclesList.get(position).make)
                    intent.putExtra("model", vehiclesList.get(position).model)
                    intent.putExtra("year", vehiclesList.get(position).year)
                    intent.putExtra("vin", vehiclesList.get(position).vin)
                    intent.putExtra("odometerReading", vehiclesList.get(position).odometerReading)
                    intent.putExtra("currentLocation", vehiclesList.get(position).currentLocation)
                    intent.putExtra("availability", vehiclesList.get(position).availability)
                    intent.putExtra("fuelType",vehiclesList.get(position).fuelType?.name)
                    intent.putExtra("imageUrl", vehiclesList.get(position).imageUrl)
                    intent.putExtra("driverImageUrl", vehiclesList.get(position).driver?.imageUrl)
                    intent.putExtra("driverFirstname", vehiclesList.get(position).driver?.firstName)
                    intent.putExtra("driverLastname", vehiclesList.get(position).driver?.lastName)
                    intent.putExtra("driverPhone", vehiclesList.get(position).driver?.phoneNumber)
                    intent.putExtra("routeStartPoint", vehiclesList.get(position).route?.startPoint)
                    intent.putExtra("routeEndPoint", vehiclesList.get(position).route?.endPoint)
                    intent.putExtra("operationArea", vehiclesList.get(position).operationArea)
                    intent.putExtra("geofenceRadius", vehiclesList.get(position).geofenceRadius)
                    intent.putExtra("fuelConsumption", vehiclesList.get(position).fuelConsumptionRate)
                    intent.putExtra("seatCapacity", vehiclesList.get(position).seatCapacity)
                    context.startActivity(intent)
                })

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
        return vehiclesList.size
    }

    class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val vehicleTypeTV: TextView = itemView.findViewById(R.id.vehicleTypeTv)
        val vehPlateNumberTv: TextView = itemView.findViewById(R.id.vehiclePlateTv)
        val vehMakeTv: TextView = itemView.findViewById(R.id.vehicleMakeTv)
        val vehImage: ImageView = itemView.findViewById(R.id.vehicleIV)
    }


    class VehiclePreviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val vehicleTypeTV: TextView = itemView.findViewById(R.id.vehicleTypeTv)
        val vehPlateNumberTv: TextView = itemView.findViewById(R.id.vehiclePlateTv)
        val vehMakeTv: TextView = itemView.findViewById(R.id.vehicleMakeTv)
        val vehImage: ImageView = itemView.findViewById(R.id.vehicleIV)
    }


}