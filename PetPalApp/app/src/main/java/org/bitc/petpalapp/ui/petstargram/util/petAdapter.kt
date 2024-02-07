package org.bitc.petpalapp.ui.petstargram.util

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.databinding.ItempetstarBinding
import org.bitc.petpalapp.ui.petstargram.itemPetstar

class PetViewHolder(val binding: ItempetstarBinding):RecyclerView.ViewHolder(binding.root)

class MyAdapter(val context: Context, val datas:MutableList<itemPetstar>
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        return PetViewHolder(ItempetstarBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data= datas.get(position)
        val binding=(holder as PetViewHolder).binding
        val imgpet=MyApplication.storage.reference.child("images/${data?.docId}.jpg")
        imgpet.downloadUrl.addOnCompleteListener { task->
            if(task.isSuccessful){
                Glide
                    .with(context)
                    .load(task.result)
                    .into(binding.petImg1)
            }
        }
    }


}