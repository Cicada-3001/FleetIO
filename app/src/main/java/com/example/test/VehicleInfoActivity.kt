package com.example.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.test.models.Driver
import com.example.test.models.Route
import com.example.test.models.Trip
import com.example.test.models.Vehicle
import org.w3c.dom.Text
import kotlin.properties.Delegates

class VehicleInfoActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var editTv: TextView;
    private lateinit var markGeoFenceTv: TextView;
    private var vehicle: Vehicle? =null
    private lateinit var vehicleType: String
    private lateinit var plateNumber: String
    private lateinit var make:String
    private lateinit var model:String
    private lateinit var year:String
    private lateinit var vin:String
    private lateinit var fuelType:String
    private lateinit var imageUrl:String
    private var odometerReading:Double = 0.0
    private lateinit var currentLocation:String
    private  var driver: Driver? = null
    private  var route: Route? = null
    private lateinit var availability:String
    var tripsList: ArrayList<Trip> = ArrayList<Trip>()
    private lateinit var startTime:String
    private lateinit var endTime:String
    private lateinit var driverImageUrl:String
    private lateinit var driverFirstName:String
    private lateinit var driverLastName:String
    private lateinit var routeStartPoint:String
    private lateinit var routeEndPoint:String
    private lateinit var operationArea:String
    private var fuelConsumption:Double = 0.0
    private lateinit var seatCapacity:String
    private  var  geofenceRadius:Double = 0.0



    private lateinit var driverName:TextView
    private lateinit var startPoint:TextView
    private lateinit var endPoint:TextView
    private lateinit var fuelEfficiency:TextView
    private lateinit var maintenanceCost:TextView
    private lateinit var insuranceCost:TextView
    private lateinit var fuelCost:TextView
    private lateinit var id:String
    private lateinit var backBtn:ImageView
    var check:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_info)
        getSupportActionBar()?.hide();
        val intent = intent
        id = intent.getStringExtra("vehicle_id").toString()
        vehicleType= intent.getStringExtra("vehicleType").toString()
        plateNumber= intent.getStringExtra("plateNumber").toString()
        make= intent.getStringExtra("make").toString()
        model = intent.getStringExtra("model").toString()
        year = intent.getStringExtra("year").toString()
        vin = intent.getStringExtra("vin").toString()
        fuelType = intent.getStringExtra("fuelType").toString()
        odometerReading = intent.getDoubleExtra("odometerReading",0.0).toDouble()
        currentLocation= intent.getStringExtra("currentLocation").toString()
        availability= intent.getStringExtra("availability").toString()
        imageUrl = intent.getStringExtra("imageUrl").toString()
        driverImageUrl = intent.getStringExtra("driverImageUrl").toString()
        driverFirstName = intent.getStringExtra("driverFirstname").toString()
        driverLastName = intent.getStringExtra("driverLastname").toString()
        routeStartPoint = intent.getStringExtra("routeStartPoint").toString()
        routeEndPoint = intent.getStringExtra("routeEndPoint").toString()
        operationArea= intent.getStringExtra("operationArea").toString()
        geofenceRadius  = intent.getDoubleExtra("geofenceRadius", 0.0)
        fuelConsumption = intent.getDoubleExtra("fuelConsumption",0.0)
        seatCapacity = intent.getStringExtra("seatCapacity").toString()
        fuelType = intent.getStringExtra("fuelType").toString()

        Log.d("Driver Lastname", driverLastName)



        editTv = findViewById(R.id.editTv)
        markGeoFenceTv = findViewById(R.id.geofenceTv)
        editTv.setOnClickListener(this)
        markGeoFenceTv.setOnClickListener(this)

        driverName = findViewById(R.id.driverValueTv)
        startPoint = findViewById(R.id.startPointTv)
        endPoint = findViewById(R.id.endPointTv)
        fuelEfficiency = findViewById(R.id.fuelValueTv)
        maintenanceCost = findViewById(R.id.maintenanceValueTv)
        insuranceCost = findViewById(R.id.insuranceValueTv)
        fuelCost = findViewById(R.id.fuelCostValueTv)
        backBtn = findViewById(R.id.back_button_img)

        driverName.text= driverFirstName +" "+ driverLastName
        startPoint.text = routeStartPoint
        endPoint.text = routeEndPoint
        fuelEfficiency.text =  ""
        maintenanceCost.text = ""
        insuranceCost.text = ""
        fuelCost.text = ""

        Log.d("Geo Radius", geofenceRadius.toString())
        Log.d("Op  Area", operationArea)

        backBtn.setOnClickListener(this)
        editTv.setOnClickListener(this)
    }

    fun animate(){
        overridePendingTransition(R.anim.slide_in_right,
            R.anim.slide_out_left);
    }




    fun intentPutExtra(i:Intent){
        i.putExtra("vehicle_id", id)
        i.putExtra("vehicleType",vehicleType)
        i.putExtra("plateNumber",plateNumber)
        i.putExtra("make", make)
        i.putExtra("model",model)
        i.putExtra("year", year)
        i.putExtra("vin", vin)
        i.putExtra("fuelType", fuelType)
        i.putExtra("odometerReading",odometerReading)
        i.putExtra("currentLocation",currentLocation)
        i.putExtra("availability",availability)
        i.putExtra("imageUrl",imageUrl)
        i.putExtra("driverImageUrl", driverImageUrl)
        i.putExtra("driverFirstname",driverFirstName)
        i.putExtra("driverLastname",driverLastName)
        i.putExtra("routeStartPoint", routeStartPoint)
        i.putExtra("routeEndPoint",routeEndPoint)
        i.putExtra("fuelType", fuelType)
        i.putExtra("operationArea", operationArea)
        i.putExtra("geofenceRadius", geofenceRadius)
        if(check == 0) {
            i.putExtra("seatCapacity", seatCapacity.toInt())
        }
        else
            i.putExtra("seatCapacity", seatCapacity)
        i.putExtra("fuelConsumption", fuelConsumption)
    }

    override fun onClick(v: View?) {
        val i:Intent
        if (v != null) {
            when (v.id) {
                R.id.back_button_img -> {
                    i=Intent(this@VehicleInfoActivity, VehicleTracking2::class.java)
                    intentPutExtra(i)
                    startActivity(i)
                    animate()
                }

                R.id.editTv -> {
                    check = 1
                    i = Intent(this@VehicleInfoActivity, EditVehicleActivity::class.java)
                    intentPutExtra(i)
                    startActivity(i)
                    animate2()
                }
                R.id.geofenceTv -> {
                    i = Intent(this@VehicleInfoActivity, VehicleGeofenceActivity::class.java)
                    intentPutExtra(i)
                    startActivity(i)
                }
            }
        }
    }

    fun animate2(){
        overridePendingTransition(R.anim.slide_out_left,
            R.anim.slide_in_right);
    }
}


