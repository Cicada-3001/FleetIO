package com.example.test


import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.DriverAdd
import com.example.test.models.Vehicle
import com.example.test.models.VehicleAdd
import com.example.test.repository.Repository
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar


class VehiclesActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    // on below line we are creating variables for
    private lateinit var vehicleRV: RecyclerView
    var vehiclesList: ArrayList<Vehicle> = ArrayList<Vehicle>()
    var  vehicleRecyclerAdapter: VehicleRecyclerAdapter = VehicleRecyclerAdapter(vehiclesList,this,0, null)
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar?
        actionBar = supportActionBar

        // with colo hash code as its parameter
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))

        // Set BackgroundDrawable
       // actionBar?.setElevation(0F);
        actionBar?.setBackgroundDrawable(colorDrawable)
        setContentView(R.layout.activity_vehicles2)
        // on below line we are initializing our views with their ids.
        vehicleRV= findViewById(R.id.vehiclesRV)
        val repository = Repository()
        progressBar = findViewById(R.id.progress_circular)
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel= ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)

        getVehicles()

        // in this we are specifying drag direction and position to right
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // this method is called
                // when the item is moved.
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // on below line we are getting the item at a particular position.
                val deletedVehicle: Vehicle =
                    vehiclesList.get(viewHolder.adapterPosition)

                // below line is to get the position
                // of the item at that position.
                val position = viewHolder.adapterPosition
                showAlertDialog(vehiclesList[position])
            }
        }).attachToRecyclerView(vehicleRV)
    }

    fun getVehicles(){
        progressBar.visibility = View.VISIBLE
        Handler().postDelayed({

            // on below line we are initializing our list
            vehiclesList = ArrayList()
            viewModel.getVehicles()

            try {
                viewModel.vehiclesResponse.observe(this, Observer {
                    if (it.isSuccessful) {
                        vehiclesList = it.body() as ArrayList<Vehicle>
                        vehicleRecyclerAdapter = VehicleRecyclerAdapter(vehiclesList, this, 0, null)
                        vehicleRV.adapter = vehicleRecyclerAdapter
                        Log.d("LIST", vehiclesList.size.toString())
                    } else {
                        Toast.makeText(this, it.errorBody().toString(), Toast.LENGTH_SHORT).show()
                    }

                })
            } catch (e: Exception) {
                Log.d("Exception", vehiclesList.size.toString())
            }

            Log.d("LIST", vehiclesList.size.toString())
            // on below line we are setting adapter to our recycler view.
             progressBar.visibility = View.GONE
            vehicleRecyclerAdapter.notifyDataSetChanged()
        }, 3000)
    }


    private fun   showAlertDialog(vehicle: Vehicle){
        MaterialAlertDialogBuilder(
            this,
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Background
        )
            .setTitle("Confirm Delete")
            .setMessage("Delete "+vehicle.make+ " "+vehicle.model)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteVehicle(vehicle._id!!)
                try {
                    viewModel.vehicleResponse.observe(this, Observer {
                        if (it.isSuccessful) {

                            val deleteVehicle= it.body() as VehicleAdd
                            Toast.makeText(
                                this,
                                "Vehicle  deleted successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                it.errorBody()?.string(),
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("Error", it.errorBody()?.string()!!)
                        }
                    })
                } catch (e: Exception) {
                    print(e.toString())
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
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
        val filteredlist: ArrayList<Vehicle> = ArrayList()

        // running a for loop to compare elements.
        for (item in vehiclesList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.vehicleType!!.toLowerCase().contains(text.toLowerCase())
                || item.make!!.toLowerCase().contains(text.toLowerCase())
                || item.model!!.toLowerCase().contains(text.toLowerCase())
                || item.year!!.toLowerCase().contains(text.toLowerCase())
            ) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        // at last we are passing that filtered
        // list to our adapter class.
        vehicleRecyclerAdapter.filterList(filteredlist)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
    }

    fun animate(){
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.add -> {
                startActivity(Intent(this@VehiclesActivity,AddVehicleActivity::class.java))
                animate()
                true
            }
            R.id.profile -> {
                startActivity(Intent(this@VehiclesActivity,ProfilesActivity::class.java))
                animate()
                true
            }
            R.id.settings -> {
                startActivity(Intent(this@VehiclesActivity,SettingsActivity::class.java))
                animate()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}

