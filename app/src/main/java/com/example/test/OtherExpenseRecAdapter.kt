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
import com.example.test.models.OtherExpense
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso


class OtherExpenseRecAdapter (private var expensesList: ArrayList<OtherExpense>, val context: Context) : RecyclerView.Adapter<OtherExpenseRecAdapter.OtherExpenseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  OtherExpenseRecAdapter.OtherExpenseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_other_expense_card,
            parent, false
        )
        // at last we are returning our view holder
        return OtherExpenseViewHolder(itemView)
    }

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<OtherExpense>) {
        expensesList = filterlist
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: OtherExpenseRecAdapter.OtherExpenseViewHolder, position: Int) {

        // on below line we are setting data to our text view and our image view.
        holder.expenseDesc.text = expensesList.get(position).description
        holder.expenseDate.text = expensesList.get(position).created
        holder.expenseAmt.text = expensesList.get(position).amount.toString()

    }

    override fun getItemCount(): Int {
        // on below line we are returning
        // our size of our list
        return expensesList.size
    }

    class OtherExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val expenseDesc: TextView = itemView.findViewById(R.id.expenseDescTv)
        val expenseDate: TextView = itemView.findViewById(R.id.expenseDateTv)
        val expenseAmt: TextView = itemView.findViewById(R.id.expenseAmtTv)

    }


}