package com.example.spy

import android.content.Intent
import android.graphics.drawable.DrawableWrapper
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.spy.databinding.ActivityCopHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import utils.UserUtils

class CopHomeActivity : AppCompatActivity() {
    companion object {
        val PICK_IMAGE_REQUEST =1080
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityCopHomeBinding
    private lateinit var profile_img:ImageView
    private var image_Uri: Uri? = null
    private lateinit var waitingDialog: AlertDialog
    private lateinit var navView:NavigationView
    private lateinit var storageReference:StorageReference
    private lateinit var drawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCopHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarCopHome.toolbar)
        
        drawerLayout = binding.drawerLayout
        navView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_cop_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_signout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        init()

    }

    private fun init(){
        storageReference= FirebaseStorage.getInstance().getReference()
        waitingDialog= AlertDialog.Builder(this)
            .setMessage("Waiting..")
            .setCancelable(false).create()
        val headerView = navView.getHeaderView(0)

        profile_img= headerView.findViewById(R.id.imageView) as ImageView
        if(Common.currentUser != null){
           // Glide.with(this).load(Common.currentUser!!.profileImg)
               // .into(profile_img)
        }
        profile_img.setOnClickListener{
            val intent= Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent,"Select profile"),PICK_IMAGE_REQUEST)

        }

        navView.setNavigationItemSelectedListener {
                it->
            if(it.itemId == R.id.nav_signout){
                val builder =AlertDialog.Builder(this@CopHomeActivity)
                builder.setTitle("Sign out")
                    .setMessage("Do you really want to sign out")
                    .setNegativeButton("CANCEL", {dialogInterface, _->dialogInterface.dismiss()})
                    .setPositiveButton("SIGN OUT", {dialogInterface,_->FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this@CopHomeActivity, SplashScreenActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }).setCancelable(false)

                val dialog= builder.create()
                dialog.setOnShowListener {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(resources.getColor(android.R.color.holo_red_dark))
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(resources.getColor(R.color.black))
                }
                dialog.show()

            }
            true
        }



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            if(data !=null && data.data !=null){
                image_Uri= data.data
                profile_img.setImageURI(image_Uri)
                showUploadDialog()

            }
        }

    }

    private fun showUploadDialog() {
        val builder =AlertDialog.Builder(this@CopHomeActivity)
        builder.setTitle("Change Profile Image")
            .setMessage("Do you really want to change 'profile image")
            .setNegativeButton("CANCEL", {dialogInterface, _->dialogInterface.dismiss()})
            .setPositiveButton("CHANGE", {dialogInterface,_->
                if(image_Uri !=null){
                    waitingDialog.show()
                    val profileImgFolder= storageReference.child("profileImg/"+FirebaseAuth.getInstance().currentUser!!.uid)
                    profileImgFolder.putFile(image_Uri!!)
                        .addOnFailureListener{
                            Snackbar.make(drawerLayout, it.message!!, Snackbar.LENGTH_SHORT).show()
                            waitingDialog.dismiss()
                        }
                        .addOnCompleteListener{
                            if(it.isSuccessful){
                                profileImgFolder.downloadUrl.addOnSuccessListener {
                                    val update_data= HashMap<String,Any>()
                                    update_data.put("profile-pic",image_Uri.toString())
                                    UserUtils.updateUser(drawerLayout,update_data)
                                    Snackbar.make(drawerLayout,"Upload successful", Snackbar.LENGTH_SHORT).show()
                                }
                                waitingDialog.dismiss()

                            }
                        }
                        .addOnProgressListener{
                            val progress= (100.0*it.bytesTransferred / it.totalByteCount)
                            waitingDialog.setMessage(StringBuilder("Uploading  ").append(progress).append("%"))
                        }
                }
            }).setCancelable(false)

        val dialog= builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(android.R.color.holo_red_dark))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(resources.getColor(R.color.black))
        }
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.cop_home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_cop_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    }
}