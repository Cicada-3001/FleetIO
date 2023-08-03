package com.example.test

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.Driver
import com.example.test.models.Trip
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class TripRecyclerAdapter (private var tripsList: ArrayList<Trip>, var context:Context) : RecyclerView.Adapter<TripRecyclerAdapter.TripViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  TripRecyclerAdapter.TripViewHolder {
        // this method is use to inflate the layout file
        // which we have created for our recycler view.
        // on below line we are inflating our layout file.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.trip_recycler_row,
            parent, false
        )
        // at last we are returning our view holder
        return TripViewHolder(itemView)
    }

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<Trip>) {
        tripsList = filterlist
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TripRecyclerAdapter.TripViewHolder, position: Int) {
        // on below line we are setting data to our text view and our image view.
        if (tripsList.get(position).vehicle.imageUrl != null) {
            Picasso.get()
                .load(tripsList.get(position).vehicle.imageUrl)
                .into(holder.vehicleImage)
        } else {
            Picasso.get()
                .load(tripsList.get(position).vehicle.imageUrl)
                .placeholder(context.resources.getDrawable(R.drawable.default_van))//it will show placeholder image when url is not valid.
                .networkPolicy(NetworkPolicy.OFFLINE) //for caching the image url in case phone is offline
                .into(holder.vehicleImage)
        }
        holder.startPointTv.text = tripsList.get(position).vehicle.route!!.startPoint
        holder.startTimeTv.text = tripsList.get(position).startTime.substring(0,21)
        holder.endPointTv.text = tripsList.get(position).vehicle.route!!.endPoint
        holder.endTimeTv.text = tripsList.get(position).endTime.substring(0,21)
        holder.driverFirstNameTv.text = tripsList.get(position).vehicle.driver!!.firstName
        holder.driverLastNameTv.text = tripsList.get(position).vehicle.driver!!.lastName
        holder.vehicleNameTv.text = tripsList.get(position).vehicle.vehicleType
        holder.vehicleNumberTv.text = tripsList.get(position).vehicle.plateNumber

        holder.itemView.setOnClickListener(View.OnClickListener{
          /*  var intent = Intent(context,VehicleTrackingActivity::class.java)
            intent.putExtra("vehicle",tripsList.get(position).vehicle)
            intent.putExtra("driver",tripsList.get(position).driver)
            intent.putExtra("startTime",tripsList.get(position).startTime)
            intent.putExtra("endTime",tripsList.get(position).endTime)
            intent.putExtra("startLocation",tripsList.get(position).route.startPoint)
            intent.putExtra("endLocation",tripsList.get(position).route.endPoint)
            intent.putExtra("distanceTravelled",tripsList.get(position).route.estimateDistance)
            intent.putExtra("fuelUsed",tripsList.get(position).vehicle.fuelConsumptionRate.toDouble() * tripsList.get(position).route.estimateDistance)
            context.startActivity(intent)*/
        })
    }

    override fun getItemCount(): Int {
        // on below line we are returning
        // our size of our list
        return tripsList.size
    }

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val vehicleNameTv: TextView = itemView.findViewById(R.id.vehicleNameTv)
        val driverLastNameTv: TextView = itemView.findViewById(R.id.driverLastNameTv)
        val driverFirstNameTv: TextView = itemView.findViewById(R.id.driverFirstNameTv)
        val vehicleNumberTv: TextView = itemView.findViewById(R.id.vehicleNumberTv)
        val startPointTv: TextView = itemView.findViewById(R.id.startPointTv)
        val endPointTv: TextView = itemView.findViewById(R.id.endPointTv)
        val startTimeTv: TextView = itemView.findViewById(R.id.startTimeTv)
        val endTimeTv: TextView = itemView.findViewById(R.id.endTimeTv)
        val vehicleImage: ImageView = itemView.findViewById(R.id.vehicleIv)

    }
}