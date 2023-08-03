package com.example.test

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.Maintenance
import com.example.test.models.Trip
import com.example.test.models.Vehicle
import com.example.test.repository.Repository
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MaintenancesActivity : AppCompatActivity(), View.OnClickListener, OnItemClickListener {
    private lateinit var viewModel: MainViewModel

    // on below line we are creating variables forN
    // our swipe to refresh layout, recycler view,

    private lateinit var maintenanceRv: RecyclerView
    private lateinit var dateFilter:ImageView
    private lateinit var vehicleFilter:ImageView
    var vehiclesList = ArrayList<Vehicle>()
    var  vehicleRecyclerAdapter: VehicleRecyclerAdapter = VehicleRecyclerAdapter(vehiclesList,this,1,this)

    var maintenancesList: ArrayList<Maintenance> = ArrayList<Maintenance>()
    var maintenanceRecyclerAdapter: MaintenanceRecyclerAdapter =
        MaintenanceRecyclerAdapter(maintenancesList,this,0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actionBar: ActionBar?
        actionBar = supportActionBar

        // with color hash code as its parameter
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))
        // Set BackgroundDrawable
        actionBar?.setBackgroundDrawable(colorDrawable)
        actionBar?.setElevation(0F)

        setContentView(R.layout.activity_maintenances)

        // on below line we are initializing our views with their ids.
        maintenanceRv = findViewById(R.id.maintenanceRV)
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        // on below line we are initializing our list
        maintenancesList = ArrayList()
        viewModel.getMaintenances()

        viewModel.maintenancesResponse.observe(this, Observer {
            if (it.isSuccessful) {
                maintenancesList = it.body() as ArrayList<Maintenance>
                maintenanceRecyclerAdapter = MaintenanceRecyclerAdapter(maintenancesList,this,0)
                maintenanceRv.adapter = maintenanceRecyclerAdapter

            } else {
                Toast.makeText(this, it.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
        })
        // on below line we are initializing our adapter
        // on below line we are setting adapter to our recycler view.
        maintenanceRecyclerAdapter.notifyDataSetChanged()
        dateFilter = findViewById(R.id.periodFilter)
        vehicleFilter = findViewById(R.id.vehicleFilter)

        dateFilter.setOnClickListener(this)
        vehicleFilter.setOnClickListener(this)


        // on below line we are creating a method to create item touch helper
        // method for adding swipe to delete functionality.
        // in this we are specifying drag direction and position to right
        // on below line we are creating a method to create item touch helper
        // method for adding swipe to delete functionality.
        // in this we are specifying drag direction and position to right
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override  fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // this method is called
                // when the item is moved.
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // this method is called when we swipe our item to right direction.
                // on below line we are getting the item at a particular position.
                val deletedMaintenance: Maintenance =
                    maintenancesList.get(viewHolder.adapterPosition)

                // below line is to get the position
                // of the item at that position.
                val position = viewHolder.adapterPosition

                // this method is called when item is swiped.
                // below line is to remove item from our array list.
                maintenancesList.removeAt(viewHolder.adapterPosition)

                // below line is to notify our item is removed from adapter.
                maintenanceRecyclerAdapter.notifyItemRemoved(viewHolder.adapterPosition)

                // below line is to display our snackbar with action.
                Snackbar.make(maintenanceRv, "Deleted " + deletedMaintenance.maintenanceType, Snackbar.LENGTH_LONG)
                    .setAction(
                        "Undo",
                        View.OnClickListener {
                            // adding on click listener to our action of snack bar.
                            // below line is to add our item to array list with a position.
                            maintenancesList.add(position, deletedMaintenance)

                            // below line is to notify item is
                            // added to our adapter class.
                            maintenanceRecyclerAdapter.notifyItemInserted(position)
                        }).show()
            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(maintenanceRv)
    }

    fun animate(){
        overridePendingTransition(R.anim.slide_in_right,
            R.anim.slide_out_left);
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // Get the result from intent
                val result = intent.getStringExtra("result")
            }
        }
    }

    // calling on create option menu
    // layout to inflate our menu file.
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // below line is to get our inflater
        val inflater = menuInflater

        // inside inflater we are inflating our menu file.
        inflater.inflate(R.menu.menu, menu)
        // below line is to get our menu item.
        val searchItem: MenuItem = menu.findItem(R.id.search)
        // getting search view of our item.
        val searchView: SearchView = searchItem.getActionView() as SearchView

        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(msg)
                return false
            }
        })
        return true
    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredlist: ArrayList<Maintenance> = ArrayList()
        // running a for loop to compare elements.
        for (item in maintenancesList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.maintenanceType.toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
            // at last we are passing that filtered
            // list to our adapter class.
            maintenanceRecyclerAdapter.filterList(filteredlist)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.add -> {
                startActivity(Intent(this@MaintenancesActivity,AddMaintenanceActivity::class.java))
                animate()
                true
            }
            R.id.profile -> {
                startActivity(Intent(this@MaintenancesActivity,ProfilesActivity::class.java))
                animate()
                true
            }
            R.id.settings -> {
                startActivity(Intent(this@MaintenancesActivity,SettingsActivity::class.java))
                animate()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showDateFilter(){
        // on below line we are creating a new bottom sheet dialog.
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_date_filter, null)
        // closing of dialog box when clicking on the screen.
        dialog.setCancelable(true)
        // content view to our view.
        dialog.setContentView(view)
        dialog.show()
    }

    fun showVehicleFilter(){
        // on below line we are creating a new bottom sheet dialog.
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.select_vehicle_bottom_sheet, null)
        val vehicleSelect= view.findViewById<RecyclerView>(R.id.vehiclesRV)

        // on below line we are initializing our list
        vehiclesList = ArrayList()
        viewModel.getVehicles()

        try{
            viewModel.vehiclesResponse.observe(this, Observer{
                if(it.isSuccessful){
                    vehiclesList= it.body() as ArrayList<Vehicle>
                    vehicleRecyclerAdapter = VehicleRecyclerAdapter(vehiclesList,this,1,this)
                    vehicleSelect.adapter = vehicleRecyclerAdapter
                    Log.d("LIST",vehiclesList.size.toString())
                }else{
                    Toast.makeText(this,it.errorBody().toString(),Toast.LENGTH_SHORT).show()
                }

            })
        }catch (e:Exception){
            Log.d("Exception",vehiclesList.size.toString())
        }
        Log.d("LIST",vehiclesList.size.toString())
        // on below line we are setting adapter to our recycler view.
        vehicleRecyclerAdapter.notifyDataSetChanged()
        // closing of dialog box when clicking on the screen.
        dialog.setCancelable(true)
        // content view to our view.
        dialog.setContentView(view)
        dialog.show()
    }


    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.periodFilter-> {
                    showDateFilter()
                }
                R.id.vehicleFilter-> {
                    showVehicleFilter()
                }
                else -> {
                    print("Error")
                }

            }
        }
    }

    override fun onItemClick(objectRep: HashMap<String, String>) {

    }


}