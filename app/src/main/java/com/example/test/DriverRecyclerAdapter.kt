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
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class DriverRecyclerAdapter (private var driversList: ArrayList<Driver>,val context:Context) : RecyclerView.Adapter<DriverRecyclerAdapter.DriverViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  DriverRecyclerAdapter.DriverViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.driver_recycler_row,
            parent, false
        )
        // at last we are returning our view holder
        return DriverViewHolder(itemView)
    }

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<Driver>) {
        driversList = filterlist
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: DriverRecyclerAdapter.DriverViewHolder, position: Int) {
        if(driversList.get(position).imageUrl != null){
            Picasso.get()
                .load(driversList.get(position).imageUrl)
                .into(holder.driverImg)
        }else{
            Picasso.get()
                .load(driversList.get(position).imageUrl)
                .placeholder(context.resources.getDrawable(R.drawable.default_user))//it will show placeholder image when url is not valid.
                .networkPolicy(NetworkPolicy.OFFLINE) //for caching the image url in case phone is offline
                .into(holder.driverImg)
        }
        // on below line we are setting data to our text view and our image view.
        holder.driverNameTv.text = driversList.get(position).firstName+" "+driversList.get(position).lastName
        holder.driverVehTv.text = driversList.get(position).vehicle?.vehicleType

        holder.itemView.setOnClickListener(View.OnClickListener{
            var intent = Intent(context,DriverInfoActivity::class.java)
            intent.putExtra("driverId",driversList.get(position)._id)
            intent.putExtra("firstName",driversList.get(position).firstName)
            intent.putExtra("lastName",driversList.get(position).lastName)
            intent.putExtra("licenseNumber",driversList.get(position).licenseNumber)
            intent.putExtra("dateOfBirth",driversList.get(position).dateOfBirth)
            intent.putExtra("phoneNumber",driversList.get(position).phoneNumber)
            intent.putExtra("email",driversList.get(position).email)
            intent.putExtra("imageUrl", driversList.get(position).imageUrl)
            intent.putExtra("licenseExpiry", driversList.get(position).licenseExpiry)
            intent.putExtra("vehicle", driversList.get(position).vehicle?.vehicleType)
            intent.putExtra("vehicleId", driversList.get(position).vehicle?._id)
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