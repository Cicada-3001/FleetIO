package com.example.test

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.Driver
import com.example.test.models.DriverAdd
import com.example.test.models.Trip
import com.example.test.models.TripAdd
import com.example.test.repository.Repository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class DriversActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    // on below line we are creating variables for
    // our swipe to refresh layout, recycler view,
    // adapter and list.
    private lateinit var driverRV: RecyclerView
    private lateinit var searchView:SearchView
    var driversList: ArrayList<Driver> = ArrayList<Driver>()
    var  driverRecyclerAdapter: DriverRecyclerAdapter = DriverRecyclerAdapter(driversList,this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_drivers)

        val actionBar: ActionBar?
        actionBar = supportActionBar
        // with color hash code as its parameter
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))
        // Set BackgroundDrawable
        //  actionBar?.setElevation(0F);
        actionBar?.setBackgroundDrawable(colorDrawable)


        // on below line we are initializing our views with their ids.
        driverRV= findViewById(R.id.driversRV)
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel= ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)

        // on below line we are initializing our list
        driversList = ArrayList()
        viewModel.getDrivers()

        viewModel.driversResponse.observe(this, Observer{
            if(it.isSuccessful){
                driversList= it.body() as ArrayList<Driver>
                driverRecyclerAdapter = DriverRecyclerAdapter(driversList,this)
                driverRV.adapter = driverRecyclerAdapter
            }else{
                Toast.makeText(this,it.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
        })
        // on below line we are initializing our adapter
        // on below line we are setting adapter to our recycler view.
        driverRecyclerAdapter.notifyDataSetChanged()

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

                // below line is to get the position
                // of the item at that position.
                val position = viewHolder.adapterPosition

                val deletedDriver: Driver =
                    driversList.get(position)


                showAlertDialog(deletedDriver)

                // below line is to notify our item is removed from adapter.
                driverRecyclerAdapter.notifyDataSetChanged()
            }
        }).attachToRecyclerView(driverRV)
    }



    private fun   showAlertDialog(driver: Driver){
        MaterialAlertDialogBuilder(
            this,
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Background
        )
            .setTitle("Confirm Delete")
            .setMessage("Delete "+driver.firstName+ "  "+driver.lastName)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteDriver(driver._id!!)
                try {
                    viewModel.driverResponse.observe(this, Observer {
                        if (it.isSuccessful) {

                            val deletedDriver = it.body() as DriverAdd
                            Toast.makeText(
                                this,
                                "Driver  deleted successfully",
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






    fun animate(){
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                Toast.makeText(this@DriversActivity, "The query has been submitted", Toast.LENGTH_SHORT).show()
                Log.d("Message", "Text changed")
                if (p0 != null) {
                    filter(p0)
                }
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                Toast.makeText(this@DriversActivity, "The query has changed", Toast.LENGTH_SHORT).show()
                Log.d("Message", "Text changed")
                filter(msg)
                return false
            }
        })
        return true
    }

    private fun filter(text: String) {
        // creating a new array list to filter our data.
        val filteredlist: ArrayList<Driver> = ArrayList()

        // running a for loop to compare elements.
        for (item in driversList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.lastName?.toLowerCase()?.contains(text.toLowerCase().trim()) == true) {
                // if the item is matched we are
                // adding it to our filtered list.
                Log.d("Message", "Items matched")
                filteredlist.add(item)
                Log.d("List", filteredlist.toString())
            }
        }
            // at last we are passing that filtered
            // list to our adapter class.
            driverRecyclerAdapter.filterList(filteredlist)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.add -> {
                startActivity(Intent(this@DriversActivity, AddDriverActivity::class.java))
                animate()
                true
            }
            R.id.profile-> {
                startActivity(Intent(this@DriversActivity, ProfilesActivity::class.java))
                true
            }
            R.id.settings-> {
                startActivity(Intent(this@DriversActivity, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }










}
