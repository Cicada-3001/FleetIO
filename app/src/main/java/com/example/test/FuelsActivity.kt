package com.example.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.Fuel
import com.example.test.models.Maintenance
import com.example.test.repository.Repository
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FuelsActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    // on below line we are creating variables for
    // our swipe to refresh layout, recycler view,
    // adapter and list.
    private lateinit var addFab: FloatingActionButton
    private lateinit var fuelRv: RecyclerView
    var fuelsList: ArrayList<Fuel> = ArrayList<Fuel>()
    var fuelRecyclerAdapter: FuelRecyclerAdapter =
       FuelRecyclerAdapter(fuelsList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_fuels)

        // on below line we are initializing our views with their ids.
        fuelRv = findViewById(R.id.fuelRV)
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        // on below line we are initializing our list
        fuelsList = ArrayList()
        viewModel.getMaintenances()

        viewModel.tripResponse.observe(this, Observer {
            if (it.isSuccessful) {
                fuelsList = it.body() as ArrayList<Fuel>
                fuelRecyclerAdapter = FuelRecyclerAdapter(fuelsList)
                fuelRv.adapter = fuelRecyclerAdapter
                Toast.makeText(this, fuelsList.size.toString(), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, it.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }
        })
        Toast.makeText(
            this,
            "After the success method" +fuelsList.size.toString(),
            Toast.LENGTH_LONG
        ).show()
        // on below line we are initializing our adapter
        // on below line we are setting adapter to our recycler view.
        fuelRecyclerAdapter.notifyDataSetChanged()
        addFab= findViewById(R.id.addFab)
        addFab.setOnClickListener{
            val intent = Intent(this, AddDriverActivity::class.java)
            // 0 is request code
            startActivityForResult(intent, 0)
            animate()
        }

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
        val filteredlist: ArrayList<Fuel> = ArrayList()

        // running a for loop to compare elements.
        for (item in fuelsList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.fuelType.toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            fuelRecyclerAdapter.filterList(filteredlist)
        }

    }
}











