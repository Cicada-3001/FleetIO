package com.example.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.Fuel


class FuelRecyclerAdapter(private var fuelsList: ArrayList<Fuel>) : RecyclerView.Adapter<FuelRecyclerAdapter.FuelViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  FuelRecyclerAdapter.FuelViewHolder {
        // this method is use to inflate the layout file
        // which we have created for our recycler view.
        // on below line we are inflating our layout file.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.fuel_recycler_row,
            parent, false
        )
        // at last we are returning our view holder
        return FuelViewHolder(itemView)
    }

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<Fuel>) {
        fuelsList = filterlist
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: FuelRecyclerAdapter.FuelViewHolder, position: Int) {
        // on below line we are setting data to our text view and our image view.
        holder.fuelTypeTv.text = fuelsList.get(position).fuelType

    }

    override fun getItemCount(): Int {
        // on below line we are returning
        // our size of our list
        return fuelsList.size
    }

    class FuelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val  fuelTypeTv: TextView = itemView.findViewById(R.id.vehTypeTv)
    //    val courseIV: ImageView = itemView.findViewById(R.id.idIVCourse)
    }









}