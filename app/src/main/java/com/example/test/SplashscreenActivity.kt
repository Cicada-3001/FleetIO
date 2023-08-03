package com.example.test


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.test.Common.USER_INFO_REFERENCE
import com.example.test.Common.currentUser
import com.example.test.models.User
import com.example.test.util.Constants
import com.example.test.util.UserUtils
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.common.internal.service.Common
import com.google.android.gms.maps.model.Dash
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.installations.FirebaseInstallations
import java.util.*


class SplashscreenActivity : AppCompatActivity() {
    private lateinit var userRef: DatabaseReference
    private lateinit var providers: List<AuthUI.IdpConfig>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var listener: FirebaseAuth.AuthStateListener
    private lateinit var database: FirebaseDatabase
    private lateinit var userInfoRef:DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen)
        // This method is used so that your splash activity can cover the entire screen.
        Handler().postDelayed({
            // Intent is used to switch from one activity to anothers
                startActivity(Intent(this@SplashscreenActivity, LoginActivity::class.java))
            // the current activity will get finished.
        }, SPLASH_SCREEN_TIME_OUT.toLong())
    }

    companion object {
        private const val SPLASH_SCREEN_TIME_OUT =
            5000 // After completion of 2000 ms, the next activity will get started.
    }

    fun animate(){
        overridePendingTransition(R.anim.slide_in_right,
            R.anim.slide_out_left);
    }










}
