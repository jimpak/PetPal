package org.bitc.petpalapp.ui.mypet.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.databinding.MypetItemBinding
import org.bitc.petpalapp.ui.mypet.PetData
import org.bitc.petpalapp.ui.mypet.util.OnItemClickListener

class MyViewHolder(val binding: MypetItemBinding) : RecyclerView.ViewHolder(binding.root)

class MyAdapter(val context: Context, val petList: MutableList<PetData>,
                val listener: OnItemClickListener
): RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MyViewHolder(MypetItemBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return petList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data=petList.get(position)
        holder.binding.run {
            textName.text=data.petname
            textType.text=data.type
            textXY.text=data.gender
            textAge.text=data.birthday
        }

        val imgRef= MyApplication.storage.reference.child("images/${data.petId}.jpg")
        imgRef.downloadUrl.addOnCompleteListener{task ->
            if (task.isSuccessful){
                Glide.with(context)
                    .load(task.result)
                    .into(holder.binding.petImg)
            }
        }

        holder.binding.root.setOnClickListener{

            listener.onItemClick(data.petId)
        }
    }
}