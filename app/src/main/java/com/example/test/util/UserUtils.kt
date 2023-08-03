package com.example.test.util

import android.content.Context
import android.view.View
import android.widget.Toast
import com.example.test.Common
import com.example.test.Common.USER_INFO_REFERENCE
import com.example.test.models.Token
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


object UserUtils {

    fun updateUser(
        view: View,
        updateData: Map<String, Any>
    ){
        FirebaseDatabase
            .getInstance()
            .getReference(USER_INFO_REFERENCE)
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .updateChildren(updateData)
            .addOnFailureListener{
                Snackbar.make(view, it.message!!, Snackbar.LENGTH_LONG).show()
            }.addOnSuccessListener {
                Snackbar.make(view, "Update information successful", Snackbar.LENGTH_LONG).show()
            }
    }


    fun updateToken(context:Context, token:String) {
        val tokenModel = Token()
        tokenModel.token = token

        FirebaseAuth.getInstance().currentUser?.let {
            FirebaseAuth.getInstance().currentUser?.let { it1 ->
                FirebaseDatabase.getInstance()
                    .getReference(Common.TOKEN_REFERENCE)
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .setValue(token)
                    .addOnFailureListener {
                        Toast.makeText(context,it.message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnSuccessListener {

                    }

            }

        }
    }
}