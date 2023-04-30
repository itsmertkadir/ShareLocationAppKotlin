package com.mertkadir.sharelocation.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.mertkadir.sharelocation.databinding.RecylerRowBinding
import com.mertkadir.sharelocation.model.Post
import com.mertkadir.sharelocation.view.FeedActivity
import java.io.Serializable

class RecyclerAdapter(val postList: ArrayList<Post>) : RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder>() {

    class RecyclerHolder(val recyclerRowBinding: RecylerRowBinding) : RecyclerView.ViewHolder(recyclerRowBinding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {

        val recyclerRowBinding = RecylerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RecyclerHolder(recyclerRowBinding)


    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.recyclerRowBinding.locationNameText.text = postList.get(position).locationName
        holder.recyclerRowBinding.commentText.text = postList.get(position).comment

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,FeedActivity::class.java)
            intent.putExtra("locationLatLng",postList.get(position))
            intent.putExtra("info","old")
            holder.itemView.context.startActivity(intent)

        }
    }

}