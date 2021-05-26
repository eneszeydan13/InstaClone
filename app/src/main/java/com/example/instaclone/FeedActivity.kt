package com.example.instaclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Adapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_feed.*
import java.sql.Timestamp

class FeedActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    var adapter : FeedRecyclerAdapter? = null

    var userEmailFromFirebase : ArrayList<String> = ArrayList()
    var userCaptionFromFirebase : ArrayList<String> = ArrayList()
    var userImageFromFirebase : ArrayList<String> = ArrayList()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.add_post){
            //Upload posts
            val intent = Intent(applicationContext, UploadActivity::class.java)
            startActivity(intent)
        } else if(item.itemId == R.id.logout){
            //Logout
            auth.signOut()

            val intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getDataFromFirestore()

        //RecyclerView
        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = FeedRecyclerAdapter(userEmailFromFirebase,userCaptionFromFirebase,userImageFromFirebase)
        recyclerView.adapter = adapter

    }

    fun getDataFromFirestore(){

        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->

            if (exception != null) {
                Toast.makeText(applicationContext,exception.localizedMessage.toString(),Toast.LENGTH_LONG).show()
            }else{

                if (snapshot!=null) {
                    if (!snapshot.isEmpty) {
                        userImageFromFirebase.clear()
                        userCaptionFromFirebase.clear()
                        userEmailFromFirebase.clear()

                        val documents = snapshot.documents

                        for (document in documents){

                            val caption = document.get("caption") as String
                            val timestamp = document.getTimestamp("date")
                            val imageUrl = document.get("imageUrl") as String
                            val userEmail = document.get("userEmail") as String
                            val date = timestamp!!.toDate()
                            userEmailFromFirebase.add(userEmail)
                            userCaptionFromFirebase.add(caption)
                            userImageFromFirebase.add(imageUrl)

                            adapter!!.notifyDataSetChanged()
                        }

                    }
                }

            }

        }

    }

}