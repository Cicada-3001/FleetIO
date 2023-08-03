package com.example.test

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import java.util.concurrent.TimeUnit


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val LOGIN_REQUEST_CODE = 1080;
    }

    lateinit var btSignIn: SignInButton
    lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var providers: List<AuthUI.IdpConfig>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var listener: FirebaseAuth.AuthStateListener
    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private lateinit var registerTv: TextView
    private lateinit var forgotPswTv: TextView
    private lateinit var loginBtn: Button
    private lateinit var emailEdt: EditText
    private lateinit var passwordEdt: EditText
    private lateinit var googleCard: CardView
    private lateinit var phoneCard: CardView
    private lateinit var phoneNumberEdt:EditText
    private lateinit var otpEdt:EditText
    private lateinit var  progressBar: ProgressBar


    // string for storing our verification ID
    private var verificationId: String? = null

    // initializing our callbacks for on
    // verification callback method.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_login)

        registerTv = findViewById(R.id.registerTv)
        forgotPswTv = findViewById(R.id.forgotPswTv)
        loginBtn = findViewById(R.id.loginBtn)
        emailEdt = findViewById(R.id.editTextTextEmailAddress)
        passwordEdt = findViewById(R.id.editTextTextPassword)
        progressBar=findViewById(R.id.progress_bar)
        googleCard = findViewById(R.id.googleCv)
        phoneCard = findViewById(R.id.phoneCv)

        loginBtn.setOnClickListener(this)
        registerTv.setOnClickListener(this)
        forgotPswTv.setOnClickListener(this)


        // Initialize sign in options the client-id is copied form google-services.json file
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("590408217968-d0u5t0j30d45i8d71ep7t55r050tl1f6.apps.googleusercontent.com")
            .requestEmail()
            .build()

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(this@LoginActivity, googleSignInOptions)
        googleCard.setOnClickListener { // Initialize sign in intent
            val intent: Intent = googleSignInClient.signInIntent
            startActivityForResult(intent, 100)
        }

        phoneCard.setOnClickListener(this)

        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        // Initialize firebase user
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        // Check condition
        if (firebaseUser != null) {
            // When user already sign in redirect to profile activity
            startActivity(
                Intent(
                    this@LoginActivity,
                    StartActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            animate()
        }
        }


    private fun showPhoneLoginLayout() {
        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        val itemview = LayoutInflater.from(this)
            .inflate(R.layout.layout_phone_login, null)
         phoneNumberEdt =
            itemview.findViewById<View>(R.id.phoneNumberEdt) as EditText
         otpEdt =
            itemview.findViewById<View>(R.id.otpEdt) as EditText
        val getOtpBtn =
            itemview.findViewById<View>(R.id.getOtpBtn) as Button

        val verifyOtpBtn =
            itemview.findViewById<View>(R.id.verifyOtpBtn) as Button


        getOtpBtn.setOnClickListener {
            if (TextUtils.isEmpty(phoneNumberEdt.getText().toString())) {
                Toast.makeText(this, "Please enter a valid phone number.", Toast.LENGTH_SHORT)
                    .show();
            } else {
                // send OTP method for getting OTP from Firebase.
                val phone = "+254" + phoneNumberEdt.getText().toString();
                sendVerificationCode(phone);
            }
        }

        verifyOtpBtn.setOnClickListener{
            if (TextUtils.isEmpty(otpEdt.getText().toString())) {
                // if the OTP text field is empty display
                // a message to user to enter OTP
                Toast.makeText( this@LoginActivity, "Please enter OTP", Toast.LENGTH_SHORT).show();
            } else {
                verifyCode(otpEdt.getText().toString());
            }
        }

        builder.setView(itemview)
        val dialog = builder.create()
        dialog.show()
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {
                    // if the code is correct and the task is successful
                    // we are sending our user to new activity.
                    val i = Intent(this@LoginActivity, StartActivity::class.java)
                    startActivity(i)
                    animate()
                } else {
                    // if the code is not correct then we are
                    // displaying an error message to the user.
                    Toast.makeText(this@LoginActivity, task.exception!!.message, Toast.LENGTH_LONG)
                        .show()
                }
            })
    }

    private fun sendVerificationCode(number: String) {
        // this method is used for getting
        // OTP on user phone number.
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(mCallBack) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // callback method is called on Phone auth provider.
    }

    // callback method is called on Phone auth provider.
    val   // initializing our callbacks for on
    // verification callback method.
            mCallBack: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            // below method is used when
            // OTP is sent from Firebase
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                // when we receive the OTP it
                // contains a unique id which
                // we are storing in our string
                // which we have already created.
                verificationId = s
            }

            // this method is called when user
            // receive OTP from Firebase.
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                // below line is used for getting OTP code
                // which is sent in phone auth credentials.
                val code = phoneAuthCredential.smsCode

                // checking if the code
                // is null or not.
                if (code != null) {
                    // if the code is not null then
                    // we are setting that code to
                    // our OTP edittext field.
                    otpEdt.setText(code)

                    // after setting this code
                    // to OTP edittext field we
                    // are calling our verifycode method.
                    verifyCode(code)
                }
            }

            // this method is called when firebase doesn't
            // sends our OTP code due to any error or issue.
            override fun onVerificationFailed(e: FirebaseException) {
                // displaying error message with firebase exception.
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }

    // below method is use to verify code from Firebase.
    private fun verifyCode(code: String) {
        // below line is used for getting
        // credentials from our verification id and code.
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check condition
        if (requestCode == 100) {
            // When request code is equal to 100 initialize task
            val signInAccountTask: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)

            // check condition
            if (signInAccountTask.isSuccessful) {
                // When google sign in successful initialize string
                val s = "Google sign in successful"
                // Display Toast
                displayToast(s)
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    val googleSignInAccount = signInAccountTask.getResult(ApiException::class.java)
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null initialize auth credential
                        val authCredential: AuthCredential = GoogleAuthProvider.getCredential(
                            googleSignInAccount.idToken, null
                        )
                        // Check credential
                        firebaseAuth.signInWithCredential(authCredential)
                            .addOnCompleteListener(this) { task ->
                                // Check condition
                                if (task.isSuccessful) {
                                    // When task is successful redirect to profile activity
                                    startActivity(
                                        Intent(
                                            this@LoginActivity, StartActivity::class.java
                                        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                    animate()
                                    // Display Toast
                                    displayToast("Firebase authentication successful")
                                } else {
                                    // When task is unsuccessful display Toast
                                    displayToast(
                                        "Authentication Failed :" + task.exception?.message
                                    )
                                }
                            }
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }
    }


    private fun loginUserAccount() {
        progressBar.setVisibility(View.VISIBLE)
        // show the visibility of progress bar to show loading
        //progressbar.setVisibility(View.VISIBLE)
        // Take the value of two edit texts in Strings
        val email: String
        val password: String
        email = emailEdt.getText().toString()
        password = passwordEdt.getText().toString()

        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(
                applicationContext,
                "Please enter email!!",
                Toast.LENGTH_LONG
            )
                .show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(
                applicationContext,
                "Please enter password!!",
                Toast.LENGTH_LONG
            )
                .show()
            return
        }

        // signin existing user
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        progressBar.setVisibility(View.GONE)
                        Toast.makeText(
                            applicationContext,
                            "Login successful!!",
                            Toast.LENGTH_LONG
                        )
                            .show();
                        // hide the progress bar
                        //progressBar.setVisibility(View.GONE)

                        // if sign-in is successful
                        // intent to home activity
                        val intent = Intent(
                            this@LoginActivity,
                            LoginActivity::class.java
                        )
                        startActivity(intent)
                    } else {
                        progressBar.setVisibility(View.GONE)
                        // sign-in failed
                        Toast.makeText(
                            applicationContext,
                            "Login failed!!",
                            Toast.LENGTH_LONG
                        )
                            .show()

                        // hide the progress bar
                        //progressbar.setVisibility(View.GONE)
                    }
                })
    }






    private fun displayToast(s: String) {
        Toast.makeText(applicationContext, s, Toast.LENGTH_SHORT).show()
    }

    /*
    private fun showLoginLayout() {
        val authMethodPickerLayout = AuthMethodPickerLayout.Builder(com.google.firebase.database.R.layout.activity_login)
            .setPhoneButtonId(com.google.firebase.database.R.id.googleCv)
            .setGoogleButtonId(com.google.firebase.database.R.id.phoneCv)
            .build()

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(authMethodPickerLayout)
                .setTheme(com.google.firebase.database.R.style.loginTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build(), LOGIN_REQUEST_CODE
        )
    }
     */


    fun animate(){
        overridePendingTransition(R.anim.slide_in_right,
            R.anim.slide_out_left);
    }

    override fun onClick(v: View?) {
         var i:Intent

        if (v != null) {
            when (v.id) {
                R.id.registerTv -> {
                    i= Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(i)
                    animate()
                }
                R.id.forgotPswTv-> {
                    i=Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(i)
                    animate()
                }
                R.id.loginBtn->{
                    loginUserAccount()
                }
                R.id.phoneCv ->{
                    showPhoneLoginLayout()
                }
                else -> {
                    print("Error")
                }

            }
        }
    }
}