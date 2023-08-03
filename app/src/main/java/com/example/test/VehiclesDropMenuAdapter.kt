package com.example.test
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.test.models.Vehicle


class VehiclesDropMenuAdapter  // invoke the suitable constructor of the ArrayAdapter class
    (context: Context, arrayList: ArrayList<Vehicle>?) :
    ArrayAdapter<Vehicle>(context, 0, arrayList!!) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // convertView which is recyclable view
        var currentItemView = convertView
        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView =
                LayoutInflater.from(context).inflate(R.layout.drop_down_item, parent, false)
        }
        // get the position of the view from the ArrayAdapter
        val currentVehicle: Vehicle? = getItem(position)

        // then according to the position of the view assign the desired TextView 1 for the same
        val vehicleNameTv = currentItemView?.findViewById<TextView>(R.id.entityNameTv)
         vehicleNameTv?.setText(currentVehicle?.make + " " +currentVehicle?.model + " "+currentVehicle?.plateNumber )

        // then return the recyclable view
        return currentItemView!!
    }


    override fun getItem(position: Int): Vehicle? {
        return super.getItem(position)
    }

}
