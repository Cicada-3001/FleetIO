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
import com.example.test.models.Routing


class ActiveRoutesRecyclerAdapter (private var routesList: ArrayList<Routing>, val context:Context) : RecyclerView.Adapter<ActiveRoutesRecyclerAdapter.RouteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  ActiveRoutesRecyclerAdapter.RouteViewHolder {
        // this method is use to inflate the layout file
        // which we have created for our recycler view.
        // on below line we are inflating our layout file.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.active_routes_recycler,
            parent, false
        )
        // at last we are returning our view holder
        return RouteViewHolder(itemView)
    }

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<Routing>) {
        routesList = filterlist
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ActiveRoutesRecyclerAdapter.RouteViewHolder, position: Int) {
        // on below line we are setting data to our text view and our image view.
        holder.routeNameTv.text = routesList.get(position).startLocation+" - "+routesList.get(position).endLocation

    }

    override fun getItemCount(): Int {
        // on below line we are returning
        // our size of our list
        return routesList.size
    }

    class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val  routeNameTv: TextView = itemView.findViewById(R.id.routeNameTv)
    }


}