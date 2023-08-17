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
import com.example.test.models.DriverStat
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class DriverRankRecyclerAdapter (private var driversList: ArrayList<DriverStat>, val context:Context) : RecyclerView.Adapter<DriverRankRecyclerAdapter.DriverViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  DriverRankRecyclerAdapter.DriverViewHolder {
        // this method is use to inflate the layout file
        // which we have created for our recycler view.
        // on below line we are inflating our layout file.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.driver_recycler_row,
            parent, false
        )
        // at last we are returning our view holder
        return DriverViewHolder(itemView)
    }

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<DriverStat>) {
        driversList = filterlist
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: DriverRankRecyclerAdapter.DriverViewHolder, position: Int) {




        if(driversList.get(position).driver.imageUrl != null){
            Picasso.get()
                .load(driversList.get(position).driver.imageUrl)
                .into(holder.driverImg)
        }else{
            Picasso.get()
                .load(driversList.get(position).driver.imageUrl)
                .placeholder(context.resources.getDrawable(R.drawable.default_user))//it will show placeholder image when url is not valid.
                .networkPolicy(NetworkPolicy.OFFLINE) //for caching the image url in case phone is offline
                .into(holder.driverImg)
        }
        // on below line we are setting data to our text view and our image view.
        holder.driverNameTv.text = driversList.get(position).driver.firstName+" "+driversList.get(position).driver.lastName
        holder.driverVehTv.text = driversList.get(position).driver.vehicle

        holder.itemView.setOnClickListener(View.OnClickListener{
            var intent = Intent(context,DriverInfoActivity::class.java)
            intent.putExtra("firstName",driversList.get(position).driver.firstName)
            intent.putExtra("lastName",driversList.get(position).driver.lastName)
            intent.putExtra("licenseNumber",driversList.get(position).driver.licenseNumber)
            intent.putExtra("dateOfBirth",driversList.get(position).driver.dateOfBirth)
            intent.putExtra("phoneNumber",driversList.get(position).driver.phoneNumber)
            intent.putExtra("email",driversList.get(position).driver.email)
            intent.putExtra("imageUrl",driversList.get(position).driver.imageUrl)
            context.startActivity(intent)
    })
    }

    override fun getItemCount(): Int {
        // on below line we are returning
        // our size of our list

        return driversList.size
    }

    class DriverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val  driverNameTv: TextView = itemView.findViewById(R.id.driverNameTv)
        val  driverVehTv: TextView = itemView.findViewById(R.id.driverVehTv)
        val  driverImg: ImageView = itemView.findViewById(R.id.driverImg)

    }



}