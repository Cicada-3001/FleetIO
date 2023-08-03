package com.example.test

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.test.models.User
import com.example.test.repository.Repository
import com.example.test.util.Constants.Companion.userId
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth


class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var  fullnameEdt: EditText
    private lateinit var  emailEdt: EditText
    private lateinit var phoneEdt: EditText
    private lateinit var passwordEdt: EditText
    private lateinit var  conf_passwordEdt: EditText
    private lateinit var loginTv: TextView
    private lateinit var registerBtn: Button
    private lateinit var backButton: ImageView
    private lateinit var  firebaseAuth: FirebaseAuth
    private lateinit var  progressBar: ProgressBar
    private lateinit var viewModel: MainViewModel
    private  var inserted:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // This method is used so t
        fullnameEdt= findViewById(R.id.editTextTextFullName)
        emailEdt= findViewById(R.id.editTextTextEmailAddress)
        phoneEdt= findViewById(R.id.editTextTextPhone)
        passwordEdt= findViewById(R.id.editTextTextPassword)
        conf_passwordEdt= findViewById(R.id.editTextConfirmPassword)
        loginTv= findViewById(R.id.loginTv)
        registerBtn= findViewById(R.id.registerBtn)
        backButton= findViewById(R.id.back_button_img)
        progressBar=findViewById(R.id.progress_bar)
        registerBtn.setOnClickListener(this)
        loginTv.setOnClickListener(this)
        backButton.setOnClickListener(this)
        firebaseAuth= FirebaseAuth.getInstance()
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel= ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)
    }

    //ViewModelProvider

    private fun registerNewUser() {
        // show the visibility of progress bar to show loading
        progressBar.setVisibility(View.VISIBLE)
        // Take the value of two edit texts in Strings
        val email: String
        val password: String
        val fullname: String
        val confirmPsw: String
        val phone: String

        fullname = emailEdt.getText().toString()
        phone = phoneEdt.getText().toString()
        confirmPsw= conf_passwordEdt.getText().toString()
        email = emailEdt.getText().toString()
        password = passwordEdt.getText().toString()

        // Validations for input email and password
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

        val allFields = checkFields()

        // create new user or register new user
        if(allFields){
            firebaseAuth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, " Firebase Registration successful!", Toast.LENGTH_LONG).show()
                        val newUser =  User(fullname, phone, email,
                            password
                        )
                        viewModel.registerUser(newUser)
                        viewModel.userResponse.observe(this, Observer{
                            if(it.isSuccessful){
                                Toast.makeText(applicationContext, "Express Registration successful!", Toast.LENGTH_LONG).show()
                                val user= it.body() as  User
                                userId = user._id
                                inserted = true
                                progressBar.setVisibility(View.GONE)
                                startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
                                animate()
                            }else{
                                Toast.makeText(this,it.errorBody()?.string(),Toast.LENGTH_LONG).show()
                                progressBar.setVisibility(View.GONE)
                            }
                        })
                    } else {
                        // Registration failed
                        Toast.makeText(applicationContext, "Registration failed!!" + " Please try again later", Toast.LENGTH_LONG)
                            .show()
                        // hide the progress bar
                        progressBar.setVisibility(View.GONE)
                    }
                })
        }
    }


    // function which checks all the text fields
    // are filled or not by the user.
    // when user clicks on the PROCEED button
    // this function is triggered.
    private fun checkFields(): Boolean {
        if (fullnameEdt.length() === 0) {
            fullnameEdt.setError("This field is required")
            Toast.makeText(this,"Fullname is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (emailEdt.length() === 0) {
           emailEdt.setError("This field is required")
            Toast.makeText(this,"Email is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (phoneEdt.length() === 0) {
            phoneEdt.setError("Phone required")
            Toast.makeText(this,"Phone is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (passwordEdt.length() === 0) {
            passwordEdt.setError("Password is required")
            Toast.makeText(this,"Password is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if(passwordEdt.getText().equals(conf_passwordEdt.getText())){
            passwordEdt.setError("Passwords don't match")
            Toast.makeText(this,"Passwords don't match" +passwordEdt.getText() +" "+conf_passwordEdt.getText(), Toast.LENGTH_SHORT).show()
            return false
        }
        else if (passwordEdt.length() < 6) {
            passwordEdt.setError("Password must be minimum 6 characters")
            Toast.makeText(this,"Password must be minimum 6 characters", Toast.LENGTH_SHORT).show()

            return false
        }

        // after all validation return true.
        return true
    }

    fun animate(){
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);
    }

    override fun onClick(v: View?) {
        var i: Intent

        if (v != null) {
            when (v.id) {
                R.id.loginTv -> {
                    i= Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(i)
                   animate()
                }
                R.id.back_button_img-> {
                    i= Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(i)
                    animate()
                }
                R.id.registerBtn->{
                    registerNewUser()
                }
                else -> {
                    print("Error")
                }
            }
        }
    }
}