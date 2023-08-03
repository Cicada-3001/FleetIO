package com.example.test

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.Driver
import com.example.test.models.Maintenance

class MaintenanceRecyclerAdapter (private var maintenancesList: ArrayList<Maintenance>, var context:Context, var viewType:Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var itemView: View? = null

        when(viewType){
            0 -> {
                val itemView = LayoutInflater.from(parent.context).inflate(
                    R.layout.maintenance_recycler_row,
                    parent, false
                )
                // at last we are returning our view holder
                return MaintenanceViewHolder(itemView)
            }
            1->{
                // below line we are inflating user message layout.
                itemView = LayoutInflater.from(parent.context).inflate(
                    R.layout.layout_maintenance_card,
                    parent, false
                )
                // at last we are returning our view holder
                return MaintenanceCardHolder(itemView)
            }
        }
        return MaintenanceViewHolder(itemView!!)
    }

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<Maintenance>) {
        maintenancesList = filterlist
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        when (viewType) {
            0 -> {
                holder as MaintenanceViewHolder

                // on below line we are setting data to our text view and our image view.
                holder.maintenanceTypeTv.text = maintenancesList.get(position).maintenanceType
                holder.maintenanceVehTypeTv.text =
                    maintenancesList.get(position).vehicle.vehicleType
                holder.maintenanceVehModelTv.text = maintenancesList.get(position).vehicle.model
                holder.maintenanceVehNumberTv.text =
                    maintenancesList.get(position).vehicle.plateNumber
                holder.mainCostTv.text = maintenancesList.get(position).cost.toString()
                holder.mainDateTv.text = maintenancesList.get(position).date.toString()

                holder.itemView.setOnClickListener(View.OnClickListener {
                    var intent = Intent(context, MaintenanceInfoActivity::class.java)
                    intent.putExtra("maintenanceId", maintenancesList.get(position)._id)
                    intent.putExtra(
                        "vehicleType",
                        maintenancesList.get(position).vehicle.vehicleType
                    )
                    intent.putExtra("vehicleMake", maintenancesList.get(position).vehicle.make)
                    intent.putExtra("vehicleModel", maintenancesList.get(position).vehicle.model)
                    intent.putExtra(
                        "maintenanceType",
                        maintenancesList.get(position).maintenanceType
                    )
                    intent.putExtra("date", maintenancesList.get(position).date)
                    intent.putExtra("cost", maintenancesList.get(position).cost)
                    intent.putExtra("description", maintenancesList.get(position).description)
                    context.startActivity(intent)

                })
            }
            1 -> {
                holder as MaintenanceCardHolder

                if(maintenancesList.get(position).maintained){
                    // on below line we are setting data to our text view and our image view.
                    holder.maintenanceDesc.text = maintenancesList.get(position).maintenanceType
                    holder.maintenanceDate.text = maintenancesList.get(position).date
                    holder.maintenanceAmt.text = maintenancesList.get(position).cost.toString()
                }
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
            return maintenancesList.size
    }

    class MaintenanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // on below line we are initializing our course name text view and our image view.
            val maintenanceTypeTv: TextView = itemView.findViewById(R.id.maintenanceTypeTv)
            val maintenanceVehTypeTv: TextView = itemView.findViewById(R.id.mainVehType)
            val maintenanceVehModelTv: TextView = itemView.findViewById(R.id.mainVehModelTv)
            val maintenanceVehNumberTv: TextView = itemView.findViewById(R.id.mainVehNumberTv)
            val mainDealerTv: TextView = itemView.findViewById(R.id.mainDealerTv)
            val mainCostTv: TextView = itemView.findViewById(R.id.mainCostTv)
            val mainDateTv: TextView = itemView.findViewById(R.id.mainDateTv)
            val maintenanceIv: ImageView = itemView.findViewById(R.id.maintenanceIv)
    }

    class MaintenanceCardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // on below line we are initializing our course name text view and our image view.
            val maintenanceDesc: TextView = itemView.findViewById(R.id.maintenanceDescTv)
            val maintenanceDate: TextView = itemView.findViewById(R.id.maintenanceDateTv)
            val maintenanceAmt: TextView = itemView.findViewById(R.id.maintenanceAmtTv)
    }
}