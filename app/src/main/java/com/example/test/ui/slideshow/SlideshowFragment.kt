package com.example.test.ui.slideshow

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.test.*
import com.example.test.databinding.FragmentSlideshowBinding
import com.example.test.models.DriverStat
import com.example.test.models.RouteStat
import com.example.test.models.StatDriver
import com.example.test.models.VehicleStat
import com.example.test.repository.Repository
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class SlideshowFragment : Fragment(), View.OnClickListener {
    private lateinit var progress:ProgressBar
    private var _binding: FragmentSlideshowBinding? = null
    // on below line we are creating
    // a variable for our image view.
    lateinit var generatePDFBtn: Button
    lateinit var file: File
    lateinit var reportTypeRG: RadioGroup



    @RequiresApi(Build.VERSION_CODES.O)
    val formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    @RequiresApi(Build.VERSION_CODES.O)
    val today = LocalDateTime.now().format(formatter1)



    @RequiresApi(Build.VERSION_CODES.O)
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    @RequiresApi(Build.VERSION_CODES.O)
    val current = LocalDateTime.now().format(formatter)



    var  reportTypeBtns: ArrayList<RadioButton> = ArrayList<RadioButton>()
    lateinit var vehWiseRb:RadioButton
    lateinit var routeWiseRb:RadioButton
    lateinit var allEntriesRb:RadioButton
    lateinit var driverWiseRb:RadioButton
    lateinit var revenueWiseRb:RadioButton


    @RequiresApi(Build.VERSION_CODES.O)
    var startDate:String= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.now().minusYears(1).format(formatter)
    } else {
        LocalDateTime.now().minusYears(1).format(formatter)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    var endDate:String= current
    var vehiclesStats: ArrayList<VehicleStat> = ArrayList<VehicleStat>()
    var driverStats: ArrayList<StatDriver> = ArrayList<StatDriver>()
    var routeStats: ArrayList<RouteStat> = ArrayList<RouteStat>()


    val informationArray = arrayOf("Name", "Company Name", "Address", "Phone",
        "Email")

    // declaring width and height
    // for our PDF file.
    var pageHeight = 400
    var pageWidth = 250
    // creating a bitmap variable
    // for storing our images
    lateinit var bmp: Bitmap
    lateinit var scaledbmp: Bitmap
    // on below line we are creating a
    // constant code for runtime permissions.
    var PERMISSION_CODE = 1001


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dateFilterTv:TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val view: View = binding.root
        dateFilterTv = view.findViewById(R.id.dateFilterTv)
        dateFilterTv.setOnClickListener(this)
        generatePDFBtn = view.findViewById(R.id.genPdfBtn)
        reportTypeRG =  view.findViewById(R.id.reportTypeRG)
        reportTypeRG.setOnClickListener(this)



        vehWiseRb= view.findViewById(R.id.vehicleWiseRb)
        routeWiseRb= view.findViewById(R.id.routeWiseRb)
        allEntriesRb= view.findViewById(R.id.choiceRb)
        revenueWiseRb= view.findViewById(R.id.revenueRb)
        driverWiseRb= view.findViewById(R.id.driverWiseRb)

        revenueWiseRb.setOnClickListener(this)
        driverWiseRb.setOnClickListener(this)

        allEntriesRb.setOnClickListener(this)
        routeWiseRb.setOnClickListener(this)
        vehWiseRb.setOnClickListener(this)


        reportTypeBtns.add(allEntriesRb)
        reportTypeBtns.add(vehWiseRb)
        reportTypeBtns.add(routeWiseRb)
        reportTypeBtns.add(driverWiseRb)


        progress = view.findViewById(R.id.progressBar)
        // on below line we are initializing our bitmap and scaled bitmap.
        bmp = BitmapFactory.decodeResource(resources, R.drawable.android)
        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false)

        // on below line we are checking permission
        if (checkPermissions()) {
            // if permission is granted we are displaying a toast message.
            Toast.makeText(requireActivity(), "Permissions Granted..", Toast.LENGTH_SHORT).show()
        } else {
            // if the permission is not granted
              requestPermission()
        }

        // on below line we are adding on click listener for our generate button.
        generatePDFBtn.setOnClickListener(this)


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.dateFilterTv -> {
                    showDateFilter()
                }
                R.id.choiceRb -> {
                    allEntriesRb.isChecked = true
                    routeWiseRb.isChecked = false
                    vehWiseRb.isChecked = false
                    driverWiseRb.isChecked = false
                    revenueWiseRb.isChecked = false
                    //Toast.makeText(context, "The all entries radio has been clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.routeWiseRb -> {
                    vehWiseRb.isChecked = false
                    allEntriesRb.isChecked = false
                    routeWiseRb.isChecked = true
                    driverWiseRb.isChecked = false
                    revenueWiseRb.isChecked = false
                    // Toast.makeText(context, "Route wise radio has been clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.vehicleWiseRb -> {
                    vehWiseRb.isChecked = true
                    routeWiseRb.isChecked = false
                    allEntriesRb.isChecked = false
                    driverWiseRb.isChecked = false
                    revenueWiseRb.isChecked = false
                  //  Toast.makeText(context, "Vehicle wise radio has been clicked", Toast.LENGTH_SHORT).show()
                }

                R.id.driverWiseRb -> {
                    driverWiseRb.isChecked = true
                    allEntriesRb.isChecked = false
                    revenueWiseRb.isChecked = false
                    routeWiseRb.isChecked = false
                    vehWiseRb.isChecked = false
                    // Toast.makeText(context, "Route wise radio has been clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.revenueRb -> {
                    revenueWiseRb.isChecked = true
                    routeWiseRb.isChecked = false
                    driverWiseRb.isChecked = false
                    allEntriesRb.isChecked = false
                    vehWiseRb.isChecked = false
                    //  Toast.makeText(context, "Vehicle wise radio has been clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.genPdfBtn -> {
                    when (checkReportType()){
                        R.id.choiceRb -> {
                            reportVehicles()
                        }
                        R.id.routeWiseRb -> {
                           reportRoutes()
                        }
                        R.id.vehicleWiseRb -> {
                            reportVehicles()
                        }

                        R.id.driverWiseRb -> {
                            Toast.makeText(activity, "Driver wise", Toast.LENGTH_SHORT).show()
                            reportDrivers()
                        }


                        R.id.revenueRb -> {
                            reportRevenue()
                        }
                    }
                }
                else -> {
                    print("Error")
                }

            }
        }
    }

    private fun reportRevenue() {

    }


    fun checkReportType():Int {
        for (btn: RadioButton in reportTypeBtns) {
            if (btn.isChecked)
                return btn.id
        }
        return allEntriesRb.id
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showDateFilter() {
        Toast.makeText(activity, "Show bottom sheet", Toast.LENGTH_SHORT).show()
        // on below line we are creating a new bottom sheet dialog.
        val dialog = BottomSheetDialog(requireActivity())
        val view = layoutInflater.inflate(R.layout.layout_date_filter, null)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGrp)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.alltimeRb-> {
                    startDate = LocalDateTime.now().minusYears(1).format(formatter)
                    endDate= current
                    dateFilterTv.text= "All Time"
                }

                R.id.todayRb -> {
                    startDate = current
                    endDate = current
                    dateFilterTv.text= "Today"
                }
                R.id.yesterdayRb -> {
                    startDate = LocalDateTime.now().minusDays(1).format(formatter)
                    endDate = startDate
                    dateFilterTv.text= "Yesterday"
                }
                R.id.thisMthRb -> {
                    startDate = LocalDateTime.now().withDayOfMonth(1).format(formatter);
                    endDate = current
                    dateFilterTv.text= "This Month"
                }
                R.id.lastMthRb -> {
                    startDate =
                        LocalDateTime.now().minusMonths(1).withDayOfMonth(1).format(formatter);
                    endDate = LocalDateTime.now().withDayOfMonth(1).minusDays(1).format(formatter)
                    dateFilterTv.text= "Last Month"

                }
            }
        }
        // closing of dialog box when clicking on the screen.
        dialog.setCancelable(true)
        // content view to our view.
        dialog.setContentView(view)
        dialog.show()

    }
     public fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.alltimeRb ->
                    if (checked) {
                        // Pirates are the best
                    }
                R.id.todayRb ->
                    if (checked) {
                        // Ninjas rule
                    }
                R.id.yesterdayRb ->
                    if (checked) {
                    }
                R.id.thisMthRb ->
                    if (checked) {
                    }
                R.id.lastMthRb ->
                    if (checked) {


                    }
                R.id.singleDayRb ->
                    if (checked) {
                        // the instance of our calendar.
                        val c = Calendar.getInstance()

                        // on below line we are getting
                        // our day, month and year.
                        val year = c.get(Calendar.YEAR)
                        val month = c.get(Calendar.MONTH)
                        val day = c.get(Calendar.DAY_OF_MONTH)

                        // on below line we are creating a
                        // variable for date picker dialog.
                        val datePickerDialog = DatePickerDialog(
                            // on below line we are passing context.
                            requireActivity(),
                            { view, year, monthOfYear, dayOfMonth ->
                                // on below line we are setting
                                // date to our edit text.
                                val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                                dateFilterTv.setText(dat)
                            },
                            // on below line we are passing year, month
                            // and day for the selected date in our date picker.
                            year,
                            month,
                            day
                        )
                        // at last we are calling show
                        // to display our date picker dialog.
                        datePickerDialog.show()
                    }
                R.id.dateRangeRb ->
                    if (checked) {
                        // Ninjas rule
                    }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getVehicleStats(){
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        val viewModel= ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)
        // on below line we are initializing our list
        viewModel.getVehicleStats(startDate, endDate)
        try{
            viewModel.vehicleStatsResponse.observe(this
            ) {
                if (it.isSuccessful) {
                    vehiclesStats = it.body() as ArrayList<VehicleStat>
                    Log.d("LIST", vehiclesStats.size.toString())
                } else {
                    Toast.makeText(requireActivity(), it.errorBody()?.string(), Toast.LENGTH_LONG)
                        .show()
                    Log.d("Error", it.errorBody()?.string()!!)
                }
            }
        }
        catch (e:Exception){
            Log.d("Exception",vehiclesStats.size.toString())
        }
    }





    @RequiresApi(Build.VERSION_CODES.O)
    fun getDriverStats(){
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        val viewModel= ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)
        // on below line we are initializing our list
        viewModel.getDriverStats(startDate, endDate)
        try{
            viewModel.driverStatsResponse.observe(this
            ) {
                if (it.isSuccessful) {
                    driverStats = it.body() as ArrayList<StatDriver>
                    Log.d("LIST", driverStats.size.toString())
                } else {
                    Toast.makeText(requireActivity(), it.errorBody()?.string(), Toast.LENGTH_LONG)
                        .show()
                    Log.d("Error", it.errorBody()?.string()!!)
                }
            }
        }
        catch (e:Exception){
            Log.d("Exception", driverStats.size.toString())
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getRouteStats(){
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        val viewModel= ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)
        // on below line we are initializing our list
        viewModel.getRouteStats(startDate, endDate)
        try{
            viewModel.routeStatsResponse.observe(this
            ) {
                if (it.isSuccessful) {
                    routeStats = it.body() as ArrayList<RouteStat>
                    Log.d("Routis",routeStats.size.toString())
                    Log.d("LIST", routeStats.size.toString())
                } else {
                    Toast.makeText(requireActivity(), it.errorBody()?.string(), Toast.LENGTH_LONG)
                        .show()
                    Log.d("Error", it.errorBody()?.string()!!)
                }
            }
        }
        catch (e:Exception){
            Log.d("Exception", routeStats.size.toString())
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllStats(){
        getVehicleStats()
    }


    fun checkPermissions(): Boolean {
        // on below line we are creating a variable for both of our permissions.
        // on below line we are creating a variable for
        // writing to external storage permission
        var writeStoragePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            WRITE_EXTERNAL_STORAGE
        )
        // on below line we are creating a variable
        // for reading external storage permission
        var readStoragePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            READ_EXTERNAL_STORAGE
        )
        // on below line we are returning true if both the
        // permissions are granted and returning false
        // if permissions are not granted.
        return writeStoragePermission == PackageManager.PERMISSION_GRANTED
                && readStoragePermission == PackageManager.PERMISSION_GRANTED
    }

    // on below line we are creating a function to request permission.
    fun requestPermission() {
        // on below line we are requesting read and write to
        // storage permission for our application.
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), PERMISSION_CODE
        )
    }

    // on below line we are calling
    // on request permission result.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // on below line we are checking if the
        // request code is equal to permission code.
        if (requestCode == PERMISSION_CODE) {

            // on below line we are checking if result size is > 0
            if (grantResults.size > 0) {

                // on below line we are checking
                // if both the permissions are granted.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]
                    == PackageManager.PERMISSION_GRANTED) {

                    // if permissions are granted we are displaying a toast message.
                    Toast.makeText(requireActivity(), "Permission Granted..", Toast.LENGTH_SHORT).show()

                } else {

                    // if permissions are not granted we are
                    // displaying a toast message as permission denied.
                    Toast.makeText(requireActivity(), "Permission Denied..", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setTitleDate(view: View, title:String){
        view.findViewById<TextView>(R.id.reportTypeTv).text = title
        view.findViewById<TextView>(R.id.reportDateTv).text = today
        view.findViewById<TextView>(R.id.reportPeriodTv).text = startDate+ " - "+endDate
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun reportVehicles(){
        progress.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getVehicleStats()
        }
        Handler().postDelayed({
            val inflater = LayoutInflater.from(requireActivity())
            val view = inflater.inflate(R.layout.layout_fleet_summary, null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setTitleDate(view,"Fleet Summary Report")
            }

            val table:TableLayout = view.findViewById(R.id.table)
            val i:Int =0;
            for (stat in vehiclesStats) {

                // Inflate your row "template" and fill out the fields.
                val row = LayoutInflater.from(requireActivity())
                    .inflate(R.layout.fleet_report_row, null) as TableRow
                if( i % 2 == 0)
                    row.setBackgroundResource(R.color.white);
                else
                    row.setBackgroundResource(R.color.report_blue);
                (row.findViewById(R.id.vehNameTv) as TextView).text = stat.vehicle.make+ " "+ stat.vehicle.model
                (row.findViewById(R.id.distanceValueTv) as TextView).text = stat.totalDistance.toString()
                (row.findViewById(R.id.timeValueTv) as TextView).text = stat.totalTime
                (row.findViewById(R.id.tripNoValueTv) as TextView).text = stat.trips.toString()
                (row.findViewById(R.id.fuelValueTv) as TextView).text = stat.totalFuelCost.toString()
                (row.findViewById(R.id.revenueValueTv) as TextView).text = stat.totalFareRevenue.toString()
                table.addView(row)
                i.inc()
            }
            table.requestLayout()
            generatePdf(view)

        }, 4000)
    }




    @RequiresApi(Build.VERSION_CODES.O)
    fun reportRoutes(){
        progress.visibility = View.VISIBLE
        getRouteStats()
        Handler().postDelayed({
            Log.d("ROUTES", routeStats.size.toString())
            val inflater = LayoutInflater.from(requireActivity())
            val view = inflater.inflate(R.layout.layout_fleet_summary, null)
            setTitleDate(view,"Routes Summary Report")
            val table:TableLayout = view.findViewById(R.id.table)
            val i:Int =0;
            for (stat in routeStats) {

                // Inflate your row "template" and fill out the fields.
                val row = LayoutInflater.from(requireActivity())
                    .inflate(R.layout.route_report_row, null) as TableRow
                if( i % 2 == 0)
                    row.setBackgroundResource(R.color.white);
                else
                    row.setBackgroundResource(R.color.report_blue);
                (row.findViewById(R.id.routeNameTv) as TextView).text = stat.route.startPoint+ " -  "+ stat.route.endPoint
                (row.findViewById(R.id.distanceValueTv) as TextView).text = stat.totalDistance.toString()
                (row.findViewById(R.id.timeValueTv) as TextView).text = stat.totalTime
                (row.findViewById(R.id.tripNoValueTv) as TextView).text = stat.trips.toString()
                (row.findViewById(R.id.fuelValueTv) as TextView).text = stat.totalFuelCost.toString()
                (row.findViewById(R.id.revenueValueTv) as TextView).text = stat.totalFareRevenue.toString()
                table.addView(row)
                i.inc()
            }
            table.requestLayout()
            generatePdf(view)
        },5000)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun reportDrivers(){
        progress.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getDriverStats()
        }
        Toast.makeText(activity,"Drivers report is being generated", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({
            val inflater = LayoutInflater.from(requireActivity())
            val view = inflater.inflate(R.layout.layout_fleet_summary, null)
            setTitleDate(view,"Drivers Summary Report")
            val table:TableLayout = view.findViewById(R.id.table)
            for (stat in driverStats) {
                var i:Int = 0
                // Inflate your row "template" and fill out the fields.
                val row = LayoutInflater.from(requireActivity())
                    .inflate(R.layout.driver_report_row, null) as TableRow
                    (row.findViewById(R.id.driverNameTv) as TextView).text = stat.driver.firstName+ " "+ stat.driver.lastName
                    (row.findViewById(R.id.distanceValueTv) as TextView).text = stat.totalDistance.toString()
                    (row.findViewById(R.id.timeValueTv) as TextView).text = stat.totalTime
                    (row.findViewById(R.id.tripNoValueTv) as TextView).text = stat.trips.toString()
                    (row.findViewById(R.id.fuelValueTv) as TextView).text = stat.totalFuelCost.toString()
                    (row.findViewById(R.id.revenueValueTv) as TextView).text = stat.totalFareRevenue.toString()
                table.addView(row)
                i += 1
            }
            table.requestLayout()
            generatePdf(view)
        }, 4000)

    }



    fun generatePdf(view: View){
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.display?.getRealMetrics(displayMetrics)
            displayMetrics.densityDpi
        }
        else{
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        }
        view?.measure(
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.widthPixels, View.MeasureSpec.EXACTLY
            ),
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.heightPixels, View.MeasureSpec.EXACTLY
            )
        )

        view?.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        val bitmap = Bitmap.createBitmap(view?.measuredWidth!!, view.measuredHeight , Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view?.draw(canvas)
        Bitmap.createScaledBitmap(bitmap, view.measuredWidth, view.measuredHeight, true)
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(view.measuredWidth, view.measuredHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0F, 0F, null)
        pdfDocument.finishPage(page)
        val filePath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "example.pdf")
        pdfDocument.writeTo(FileOutputStream(filePath))
        pdfDocument.close()
        Toast.makeText(requireContext(),"Report generate", Toast.LENGTH_LONG)
        val intent = Intent(requireActivity(),PdfViewerActivity::class.java)
        intent.putExtra("file", filePath)
        startActivity(intent)
        animate()


    }

    fun animate(){
        activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }




}