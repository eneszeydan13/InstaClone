package com.example.instaclone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class FeedRecyclerAdapter(private val userEmailArray: ArrayList<String>, private val userCaptionArray: ArrayList<String>, private val userImageArray: ArrayList<String>) : RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_view_row,parent,false)
        return PostHolder(view)

    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        holder.recyclerEmailText?.text = userEmailArray[position]
        holder.recyclerCaption?.text = userCaptionArray[position]
        Picasso.get().load(userImageArray[position]).into(holder.recyclerImageView)
    }

    override fun getItemCount(): Int {

        return userEmailArray.size

    }
    class PostHolder( view: View) : RecyclerView.ViewHolder(view){

        var recyclerEmailText : TextView? = null
        var recyclerCaption : TextView? = null
        var recyclerImageView : ImageView? = null

        init {
            recyclerEmailText = view.findViewById(R.id.recyclerEmailText)
            recyclerCaption = view.findViewById(R.id.recyclerCaption)
            recyclerImageView = view.findViewById(R.id.recyclerImageView)
        }

    }

}