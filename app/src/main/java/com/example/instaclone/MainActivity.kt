package com.example.instaclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import io.perfmark.Tag
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth


    override fun onStart() {
        super.onStart()
        //Checking if user is signed in
        val currentUser = auth.currentUser
        if (currentUser != null){
            val intent = Intent(applicationContext,FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

    }


    fun signInClicked(view:View){

        val email = userEmail.text.toString()
        val password = passwordText.text.toString()

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->

            if (task.isSuccessful){
                Log.d("SUCCESS MESSAGE","signInWithEmail:success")
                val user = auth.currentUser
                val intent = Intent(applicationContext,FeedActivity::class.java)
                startActivity(intent)
                finish()
            } else {

                Log.w("FAIL MESSAGE","signInWithEmail:failure",task.exception)


            }
        }.addOnFailureListener { exception ->

            if(exception != null){
                Toast.makeText(applicationContext,exception.localizedMessage.toString(),Toast.LENGTH_LONG).show()
            }

        }

    }

    fun signUpClicked(view: View){

        val email = userEmail.text.toString()
        val password = passwordText.text.toString()

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val intent = Intent(applicationContext,FeedActivity::class.java)
                Log.d("Success Message","createUserWithEmail:success")
                val user = auth.currentUser
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception->
            if(exception != null){
                Toast.makeText(applicationContext,exception.localizedMessage.toString(),Toast.LENGTH_LONG).show()
            }
        }


    }

}