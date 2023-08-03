package com.example.test

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.Driver
import com.example.test.models.Location
import com.example.test.models.Routing


class RoutesSelRecyclerAdapter (private var locationList: ArrayList<Routing>, val context:Context) : RecyclerView.Adapter<RoutesSelRecyclerAdapter.LocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RoutesSelRecyclerAdapter.LocationViewHolder {
        // this method is use to inflate the layout file
        // which we have created for our recycler view.
        // on below line we are inflating our layout file.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.selected_routes_recycler,
            parent, false
        )
        // at last we are returning our view holder
        return LocationViewHolder(itemView)
    }

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<Routing>) {
       locationList = filterlist
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RoutesSelRecyclerAdapter.LocationViewHolder, position: Int) {
        // on below line we are setting data to our text view and our image view.
        holder.startLocationNameTv.text = locationList.get(position).startLocation
        holder.endLocationNameTv.text = locationList.get(position).endLocation
    }

    override fun getItemCount(): Int {
        // on below line we are returning
        // our size of our list
        return locationList.size
    }

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val  startLocationNameTv: TextView = itemView.findViewById(R.id.startLocationNameTv)
        val  endLocationNameTv: TextView = itemView.findViewById(R.id.endLocationNameTv)
    }


}