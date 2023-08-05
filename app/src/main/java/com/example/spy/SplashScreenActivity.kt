package com.example.spy

import Model.Vehicle
import Model.VehicleInfoModel
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.spy.ViewModels.MainViewModel
import com.example.spy.ViewModels.MainViewModelFactory
import com.example.spy.repository.Repository
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.installations.FirebaseInstallations
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import utils.UserUtils
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class SplashScreenActivity : AppCompatActivity() {
    companion object{
        private val LOGIN_REQUEST_CODE =7171
    }

    //firebase auth
    private lateinit var providers: List<AuthUI.IdpConfig>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var listener:FirebaseAuth.AuthStateListener

    //firebase database
    private lateinit var database:FirebaseDatabase
    private lateinit var vehicleInfoRef:DatabaseReference
    private lateinit var progress_bar:ProgressBar



    override fun onStart() {
        super.onStart()
        delaySplashScreen()
    }

    override fun onStop() {
        if(firebaseAuth !=null && listener !==null)
            firebaseAuth.removeAuthStateListener {listener}
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        progress_bar= findViewById(R.id.progress_bar)
        init()
    }


    private fun init(){
        database = FirebaseDatabase.getInstance()
        vehicleInfoRef= database.getReference(Common.VEHICLE_INFO_REFERENCE)
        providers= Arrays.asList(
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        firebaseAuth= FirebaseAuth.getInstance()
        listener= FirebaseAuth.AuthStateListener { myFirebaseAuth->
           // val user= myFirebaseAuth.currentUser
           // val model = VehicleInfoModel()
           // if(user !=null) {
               // FirebaseInstallations.getInstance()
                  //  .id
                  //  .addOnFailureListener{
                  //      e->Toast.makeText(this@SplashScreenActivity, e.message, Toast.LENGTH_SHORT).show()
                 //   }
                  //  .addOnSuccessListener {
                   //         Log.d("TOKEN", it)
                   //         UserUtils.updateToken(this, it)
              //      }
                checkUserFromFirebase()
           // }
           // else
             //   showLoginLayout()
        }
    }

    private fun checkUserFromFirebase(){
        try{
            vehicleInfoRef.child(Common.vehicle_id).
            addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val  model=  snapshot.getValue(VehicleInfoModel::class.java)
                        goToHomeActivity(model)
                    }else{
                        showRegisterLayout()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SplashScreenActivity,"database error",Toast.LENGTH_SHORT).show()
                } })
        }catch (e:Exception){
            showRegisterLayout()
        }
    }

    private fun goToHomeActivity(model: VehicleInfoModel?) {
        Common.currentUser=model
        startActivity(Intent(this,CopHomeActivity::class.java))
        finish()
    }

    private fun showRegisterLayout() {
        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        var itemview = LayoutInflater.from(this).inflate(R.layout.layout_register, null)
        val edt_vehicleName = itemview.findViewById<View>(R.id.edt_vehicleName) as EditText
        val edt_vehicleVIN = itemview.findViewById<View>(R.id.edt_vehicleVIN) as EditText
        val edt_vehiclePlate = itemview.findViewById<View>(R.id.edt_vehiclePlate) as EditText
        val btn_continue = itemview.findViewById<Button>(R.id.btn_register) as Button
        builder.setView(itemview)
        //set data
       // if(FirebaseAuth.getInstance().currentUser!!.phoneNumber !=null && TextUtils.isDigitsOnly(FirebaseAuth.getInstance().currentUser!!.phoneNumber))
        //    edt_phone.setText(FirebaseAuth.getInstance().currentUser!!.phoneNumber)
        val dialog = builder.create()
        dialog.show()
       btn_continue.setOnClickListener {
           if (TextUtils.isEmpty(edt_vehicleName.getText().toString())){
               Toast.makeText(this, "Please enter vehicle name ", Toast.LENGTH_SHORT).show()
               return@setOnClickListener
           }else if(TextUtils.isEmpty(edt_vehicleVIN.getText().toString())){
               Toast.makeText(this, "Please enter vehicle vin", Toast.LENGTH_SHORT).show()
               return@setOnClickListener
           }else if(TextUtils.isEmpty(edt_vehiclePlate.getText().toString())){
               Toast.makeText(this, "Please enter vehicle number plate", Toast.LENGTH_SHORT).show()
               return@setOnClickListener
           }else{

               val model = VehicleInfoModel()
               model.vehicleName = edt_vehicleName.text.toString()
               model.vin = edt_vehicleVIN.text.toString()
               model.plateNumber = edt_vehiclePlate.text.toString()

               val repository = Repository()
               val viewModelFactory = MainViewModelFactory(repository)
               val  viewModel= ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)

               // on below line we are initializing our list
               var vehicle:ArrayList<Vehicle>
               viewModel.getVehicleByVin(model.vin!!)

               try{
                   viewModel.vehicleResponse.observe(this,Observer{
                       if(it.isSuccessful){
                           vehicle= it.body() as ArrayList<Vehicle>
                           Common.vehicle_id = vehicle[0]._id.toString()
                           vehicleInfoRef.child(Common.vehicle_id)
                               .setValue(model)
                               .addOnFailureListener {
                                   Toast.makeText(this@SplashScreenActivity, ""+it.message,Toast.LENGTH_SHORT).show()
                                   dialog.dismiss()
                                   progress_bar.visibility = View.GONE
                               }.addOnSuccessListener {
                                   Toast.makeText(this@SplashScreenActivity, "Vehicle Tracker Registration Success",Toast.LENGTH_SHORT).show()
                                   dialog.dismiss()
                                   progress_bar.visibility = View.GONE
                                   goToHomeActivity(model)
                               }
                       }else{
                           Toast.makeText(this,it.errorBody().toString(),Toast.LENGTH_SHORT).show()
                       }
                   }
                   )
               }catch (e:Exception){
                   Log.d("Exception",e.message.toString())
               }
           }
       }
    }


    private fun showLoginLayout(){
        val  authMethodPickerLayout=  AuthMethodPickerLayout.Builder(R.layout.layout_sign_in).setPhoneButtonId(R.id.btn_phone_sign_in)
            .setGoogleButtonId(R.id.btn_google_sign_in)
            .build()

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setAuthMethodPickerLayout(authMethodPickerLayout)
                .setTheme(R.style.loginTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build()
            , LOGIN_REQUEST_CODE)

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if(requestCode== LOGIN_REQUEST_CODE){
                val response= IdpResponse.fromResultIntent(data)
                if(resultCode== Activity.RESULT_OK){
                    val user = FirebaseAuth.getInstance().currentUser
                    }else{ Toast.makeText(this@SplashScreenActivity, ""+response!!.error!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    fun delaySplashScreen(){
        Completable.timer(3,TimeUnit.SECONDS,AndroidSchedulers.mainThread()).subscribe {
          firebaseAuth.addAuthStateListener(listener)
        //  Toast.makeText(this@SplashScreenActivity, "Testing delay", Toast.LENGTH_SHORT).show()
        }
    }

}


