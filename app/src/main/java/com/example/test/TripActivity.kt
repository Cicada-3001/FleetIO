package com.example.test

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.test.models.Trip
import com.example.test.models.TripAdd
import com.example.test.models.VehicleAdd
import com.example.test.repository.Repository
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TripActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var viewModel: MainViewModel
    // on below line we are creating variables for
    // our swipe to refresh layout, recycler view,
    // adapter and list.
    private lateinit var tripRv: RecyclerView
    var tripsList: ArrayList<Trip> = ArrayList<Trip>()
    var  tripRecyclerAdapter: TripRecyclerAdapter = TripRecyclerAdapter(tripsList, this)
    @RequiresApi(Build.VERSION_CODES.O)
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    @RequiresApi(Build.VERSION_CODES.O)
    val current = LocalDateTime.now().format(formatter)

    private lateinit var progressBar:ProgressBar
    private lateinit var datePickerBtn: ImageView
    private lateinit var dialog:BottomSheetDialog
    private lateinit var periodTv: TextView


    @RequiresApi(Build.VERSION_CODES.O)
    var startDate:String= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.now().minusYears(1).format(formatter)
    } else {
        LocalDateTime.now().minusYears(1).format(formatter)
    };
    @RequiresApi(Build.VERSION_CODES.O)
    var endDate:String= current

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip)
        val actionBar: ActionBar?
        actionBar = supportActionBar
        // with color hash code as its parameter
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))

        actionBar?.setBackgroundDrawable(colorDrawable)
        // on below line we are initializing our views with their ids.
        tripRv= findViewById(R.id.tripsRv)
        progressBar = findViewById(R.id.progress_circular)
        datePickerBtn = findViewById(R.id.periodDropImg)
        periodTv = findViewById(R.id.periodTv)


        dialog = BottomSheetDialog(this)
        datePickerBtn.setOnClickListener(this)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel= ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)
        getTrips()

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
                val deletedTrip: Trip =
                    tripsList.get(viewHolder.adapterPosition)
                // below line is to get the position
                // of the item at that position.
                val position = viewHolder.adapterPosition
                showAlertDialog(tripsList[position])

                tripRecyclerAdapter.notifyDataSetChanged()
            }
        }).attachToRecyclerView(tripRv)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun showDateFilter() {
        Toast.makeText(this@TripActivity, "Show bottom sheet", Toast.LENGTH_SHORT).show()
        // on below line we are creating a new bottom sheet dialog.
        val view = layoutInflater.inflate(R.layout.layout_date_filter, null)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGrp)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.alltimeRb-> {
                    startDate = LocalDateTime.now().minusYears(1).format(formatter)
                    endDate= current
                    periodTv.setText("All time")
                    getTrips()
                }

                R.id.todayRb -> {
                    startDate = current
                    endDate = current
                    periodTv.setText("Today")
                    getTrips()
                }

                R.id.yesterdayRb -> {
                    startDate = LocalDateTime.now().minusDays(1).format(formatter)
                    endDate = startDate
                    periodTv.setText("Yesterday")
                    getTrips()
                }

                R.id.thisMthRb -> {
                    startDate = LocalDateTime.now().withDayOfMonth(1).format(formatter);
                    endDate = current
                    periodTv.setText("This Month")
                    getTrips()
                }

                R.id.lastMthRb -> {
                    startDate =
                        LocalDateTime.now().minusMonths(1).withDayOfMonth(1).format(formatter);
                    endDate = LocalDateTime.now().withDayOfMonth(1).minusDays(1).format(formatter)
                    periodTv.setText("Last Month")

                }
            }
        }
        // closing of dialog box when clicking on the screen.
        dialog.setCancelable(true)
        // content view to our view.
        dialog.setContentView(view)
        dialog.show()

    }

    fun animate(){
        overridePendingTransition(R.anim.slide_in_right,
            R.anim.slide_out_left);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTrips(){
        progressBar.visibility = View.VISIBLE
        Handler().postDelayed({
            // on below line we are initializing our list
            if(dialog != null){
                dialog.dismiss()
            }
            tripsList = ArrayList()
            viewModel.getTrips(startDate, endDate)

            viewModel.tripsResponse.observe(this, Observer{
                if(it.isSuccessful){
                    tripsList= it.body() as ArrayList<Trip>
                    tripRecyclerAdapter = TripRecyclerAdapter(tripsList, this)
                    tripRv.adapter = tripRecyclerAdapter

                }else{
                    Toast.makeText(this,it.errorBody()?.string(), Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(this,tripsList.size.toString(), Toast.LENGTH_SHORT).show()
            })

            // on below line we are setting adapter to our recycler view.

        }, 3000)
        progressBar.visibility = View.GONE
        tripRecyclerAdapter.notifyDataSetChanged()
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
        val filteredlist: ArrayList<Trip> = ArrayList()

        // running a for loop to compare elements.
        for (item in tripsList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.vehicle.make!!.toLowerCase().contains(text.toLowerCase()) || item.vehicle.model!!.toLowerCase().contains(text.toLowerCase()) ||
                item.startTime.toLowerCase().contains(text.toLowerCase()) || item.endTime.toLowerCase().contains(text.toLowerCase()) ||
                item.vehicle.route!!.startPoint!!.toLowerCase().contains(text.toLowerCase()) || item.vehicle.route.endPoint!!.toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        // at last we are passing that filtered
        // list to our adapter class.
        tripRecyclerAdapter.filterList(filteredlist)
    }


    private fun   showAlertDialog(trip: Trip){
        MaterialAlertDialogBuilder(
            this,
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Background
        )
            .setTitle("Confirm Delete")
            .setMessage("Delete "+trip.startTime+ " to  "+trip.endTime)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteTrip(trip._id!!)
                try {
                    viewModel.tripResponse.observe(this, Observer {
                        if (it.isSuccessful) {

                            val deleteVehicle= it.body() as TripAdd
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
         when(v?.id){
             R.id.periodDropImg ->{
                 showDateFilter()
             }
         }
    }
}












