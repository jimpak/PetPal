package org.bitc.petpalapp.recyclerviewAdapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bitc.petpalapp.databinding.GridItemBinding


class GridViewHolder(val binding: GridItemBinding) : RecyclerView.ViewHolder(binding.root)

class GridAdapter(val context: Context, val itemList: MutableList<String>) :
    RecyclerView.Adapter<GridViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return GridViewHolder(GridItemBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return itemList.size

    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val data = itemList.get(position)
        holder.binding.run {

            petstarTitle.text=data

        }

//        val imgRef = MyApplication.storage.reference.child("images/${data.docId}.jpg")
//        imgRef.downloadUrl.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Glide.with(context)
//                    .load(task.result)
//                    .into(holder.binding.petstarImg)
//            }
//        }

        holder.binding.root.setOnClickListener {


        }
    }
}


