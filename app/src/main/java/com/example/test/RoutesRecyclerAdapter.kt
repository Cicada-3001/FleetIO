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
import com.example.test.models.Route
import com.example.test.models.Vehicle

class RoutesRecyclerAdapter(private var routesList: ArrayList<Route>, var context: Context, var  viewType:Int, var  onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
         var  itemView: View? = null

        when(viewType){
            0 -> {
                // below line we are inflating user message layout.
                 itemView = LayoutInflater.from(parent.context).inflate(
                    R.layout.routes_recycler,
                    parent, false
                )
                // at last we are returning our view holder
                return RouteViewHolder(itemView)
            }

            1 -> {

                 itemView = LayoutInflater.from(parent.context).inflate(
                    R.layout.route_select_recycler,
                    parent, false
                )
                // at last we are returning our view holder
                return RouteFilterHolder(itemView)

            }
        }
        return RouteViewHolder(itemView!!)
    }

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<Route>) {
        routesList = filterlist
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (viewType) {
            0 -> {
                holder as RouteViewHolder
                // on below line we are setting data to our text view and our image view.
                holder.startPoint.text = routesList.get(position).startPoint.toString()
                holder.endPoint.text = routesList.get(position).endPoint.toString()
                holder.estimateFare.text = routesList.get(position).estimateFareAmt.toString()+" /="


                holder.itemView.setOnClickListener(View.OnClickListener {
                    var intent = Intent(context, RouteInfoActivity::class.java)
                    intent.putExtra("routeId", routesList.get(position)._id)
                    intent.putExtra("startPoint", routesList.get(position).startPoint)
                    intent.putExtra("endPoint", routesList.get(position).endPoint)
                    intent.putExtra("estimateFare", routesList.get(position).estimateFareAmt)
                    intent.putExtra("estimateDistance", routesList.get(position).estimateDistance)
                    intent.putExtra("estimateTime", routesList.get(position).estimateTime)
                    context.startActivity(intent)
                })
            }
            1 -> {
                holder as RouteFilterHolder
                // on below line we are setting data to our text view and our image view.
                holder.startPoint.text = routesList.get(position).startPoint.toString()
                holder.endPoint.text = routesList.get(position).endPoint.toString()

                holder.itemView.setOnClickListener(View.OnClickListener {
                    //  holder.cardHolder.isChecked = !holder.cardHolder.isChecked
                    var hashMap : HashMap<String, String>
                            = HashMap<String, String> ()

                    // put() function
                    hashMap.put("id" ,routesList.get(position)._id!!)
                    hashMap.put("name" , routesList.get(position).startPoint!!+"  -  "+routesList.get(position).endPoint!!)
                    onItemClickListener?.onItemClick(hashMap)
                })
            }




            }
        }


    override fun getItemCount(): Int {
        // on below line we are returning
        // our size of our list
        return routesList.size
    }

    override fun getItemViewType(position: Int): Int {
        // below line of code is to set position.
        return when (viewType) {
            0 -> 0
            1 -> 1
            else -> 0
        }
    }

    class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val startPoint: TextView = itemView.findViewById(R.id.startPointTv)
        val endPoint: TextView = itemView.findViewById(R.id.endPointTv)
        val estimateFare: TextView = itemView.findViewById(R.id.fareAmtTv)
    }


    class RouteFilterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        // on below line we are initializing our course name text view and our image view.
        val startPoint: TextView = itemView.findViewById(R.id.startPointTv)
        val endPoint: TextView = itemView.findViewById(R.id.endPointTv)
    }




}

