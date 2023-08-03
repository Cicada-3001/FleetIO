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
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.Route
import com.example.test.models.Vehicle
import com.example.test.repository.Repository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class RoutesActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var viewModel: MainViewModel
    // on below line we are creating variables for
    private lateinit var routeRV: RecyclerView
    var  routesList: ArrayList<Route> = ArrayList<Route>()
    var  routeRecyclerAdapter: RoutesRecyclerAdapter = RoutesRecyclerAdapter(routesList,this, 0, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        val actionBar: ActionBar?
        actionBar = supportActionBar


        // with color hash code as its parameter
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))
        // Set BackgroundDrawable
        // actionBar?.setElevation(0F);
        actionBar?.setBackgroundDrawable(colorDrawable)

        setContentView(R.layout.activity_vehicles2)
        // on below line we are initializing our views with their ids.
        routeRV= findViewById(R.id.vehiclesRV)
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel= ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)

        // on below line we are initializing our list
        routesList = ArrayList()
        viewModel.getRoutes()

        viewModel.routesResponse.observe(this, Observer{
            if(it.isSuccessful){
                routesList= it.body() as ArrayList<Route>
                routeRecyclerAdapter = RoutesRecyclerAdapter(routesList,this,0, this)
                routeRV.adapter = routeRecyclerAdapter
                Log.d("LIST",routesList.size.toString())
            }else{
                Toast.makeText(this,it.errorBody().toString(), Toast.LENGTH_SHORT).show()
            }

        })
        Log.d("LIST",routesList.size.toString())
        // on below line we are setting adapter to our recycler view.
        routeRecyclerAdapter.notifyDataSetChanged()

        fun animate(){
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
        }



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
                // this method is called when we swipe our item to right direction.
                // on below line we are getting the item at a particular position.
                val deletedRoute: Route =
                    routesList.get(viewHolder.adapterPosition)

                // below line is to get the position
                // of the item at that position.
                val position = viewHolder.adapterPosition

                // this method is called when item is swiped.
                // below line is to remove item from our array list.
                routesList.removeAt(viewHolder.adapterPosition)
                // below line is to notify our item is removed from adapter.
                routeRecyclerAdapter.notifyItemRemoved(viewHolder.adapterPosition)

                // below line is to display our snackbar with action.
                // below line is to display our snackbar with action.
                // below line is to display our snackbar with action.
                Snackbar.make(routeRV, "Deleted " + deletedRoute.startPoint +" "+deletedRoute.endPoint, Snackbar.LENGTH_LONG)
                    .setAction(
                        "Undo",
                        View.OnClickListener {
                            // adding on click listener to our action of snack bar.
                            // below line is to add our item to array list with a position.
                            routesList.add(position, deletedRoute)

                            // below line is to notify item is
                            // added to our adapter class.
                            routeRecyclerAdapter.notifyItemInserted(position)
                        }).show()
            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(routeRV)
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

    fun animate(){
        overridePendingTransition(R.anim.slide_in_right,
            R.anim.slide_out_left);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.add -> {
                startActivity(Intent(this@RoutesActivity,AddRouteActivity::class.java))
                animate()
                true
            }
            R.id.profile -> {
                startActivity(Intent(this@RoutesActivity,ProfilesActivity::class.java))
                animate()
                true
            }
            R.id.settings -> {
                startActivity(Intent(this@RoutesActivity,SettingsActivity::class.java))
                animate()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
        val filteredlist: ArrayList<Route> = ArrayList()

        // running a for loop to compare elements.
        for (item in routesList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.startPoint?.toLowerCase()!!.contains(text.toLowerCase()) ||
                item.endPoint?.toLowerCase()!!.contains(text.toLowerCase()) ||
                item.estimateDistance.toString().toLowerCase().contains(text.toLowerCase()) ||
                    item.estimateTime!!.toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        // at last we are passing that filtered
        // list to our adapter class.
        routeRecyclerAdapter.filterList(filteredlist)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
    }

    override fun onItemClick(objectRep: HashMap<String, String>) {
        TODO("Not yet implemented")
    }


}