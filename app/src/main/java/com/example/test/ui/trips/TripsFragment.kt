package com.example.test.ui.trips

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.test.*
import com.example.test.models.Trip
import com.example.test.models.Vehicle
import com.example.test.repository.Repository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


class TripsFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    // on below line we are creating variables for
    private lateinit var tripsRV: RecyclerView
    var tripsList: ArrayList<Trip> = ArrayList<Trip>()


   lateinit var  tripsRecyclerAdapter: TripRecyclerAdapter
    private lateinit var addFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tripsRecyclerAdapter=TripRecyclerAdapter(tripsList, requireActivity())

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_trips, container, false)

        return view
    }
}