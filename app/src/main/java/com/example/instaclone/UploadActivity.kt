package com.example.instaclone

import android.app.Activity
import android.app.ZygotePreload
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_upload.*
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest

class UploadActivity : AppCompatActivity() {

    var selectedPicture : Uri? = null
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()



    }

    fun imageViewClicked(view: View){

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        } else{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,2)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if(requestCode == 1){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,2)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
            selectedPicture = data.data

            try {
                if (selectedPicture != null){
                    //For sdk above 28
                    if(Build.VERSION.SDK_INT >= 28){

                        val source = ImageDecoder.createSource(contentResolver,selectedPicture!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        uploadedImage.setImageBitmap(bitmap)

                    }else{
                        //For sdk below 28
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,selectedPicture)
                        uploadedImage.setImageBitmap(bitmap)
                    }

                }
            } catch (e: Exception){
                e.printStackTrace()
            }


        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun uploadClicked(view:View){

        //UUID -> Unique Image Name
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val storage = Firebase.storage
        val reference = storage.reference
        val imagesRef = reference.child("images").child(imageName)
        if (selectedPicture != null) {
            imagesRef.putFile(selectedPicture!!).addOnSuccessListener { taskSnapshot ->

                //Save to Firestore
                val uploadedPicRef = FirebaseStorage.getInstance().reference.child("images").child(imageName)
                uploadedPicRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()

                    val postMap = hashMapOf<String,Any>(
                        "imageUrl" to downloadUrl,
                        "userEmail" to auth.currentUser.email.toString(),
                        "caption" to editText.text.toString(),
                        "date" to Timestamp.now()
                    )

                    db.collection("Posts").add(postMap).addOnCompleteListener { task ->
                        if(task.isComplete && task.isSuccessful){
                            finish()
                        }
                    }.addOnFailureListener { e ->
                        Log.w("Error:","Error adding document", e)
                    }

                }
                
            }
        }
    }

}