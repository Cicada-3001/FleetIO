package com.example.test

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.OtherExpense
import com.example.test.models.OtherRevenue
import kotlin.math.roundToInt

class OtherRevenueRecAdapter(private var revenueList: ArrayList<OtherRevenue>, val context: Context)  : RecyclerView.Adapter<OtherRevenueRecAdapter.OtherRevenueViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  OtherRevenueRecAdapter.OtherRevenueViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_other_revenue_card,
            parent, false
        )
        // at last we are returning our view holder
        return OtherRevenueViewHolder(itemView)
    }

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<OtherRevenue>) {
        revenueList = filterlist
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: OtherRevenueRecAdapter.OtherRevenueViewHolder, position: Int) {

        // on below line we are setting data to our text view and our image view.
        holder.revenueDesc.text =  revenueList.get(position).description
        holder.revenueDate.text = revenueList.get(position).created
        holder.revenueAmt.text =  revenueList.get(position).amount.toString()



    }

    override fun getItemCount(): Int {
        // on below line we are returning
        // our size of our list
        return revenueList.size
    }

    class OtherRevenueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val revenueDesc: TextView = itemView.findViewById(R.id.revenueDescTv)
        val revenueDate: TextView = itemView.findViewById(R.id.revenueDateTv)
        val revenueAmt: TextView = itemView.findViewById(R.id.revenueAmtTv)

    }

}