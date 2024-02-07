package org.bitc.petpalapp.recyclerviewAdapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bitc.petpalapp.ChatActivity
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.databinding.MachingItemBinding

import org.bitc.petpalapp.model.PetsitterItem
import org.bitc.petpalapp.ui.mypet.util.OnItemClickListener


class PetsitterViewHolder(val binding: MachingItemBinding) : RecyclerView.ViewHolder(binding.root)

class PetsiiterAdapter(
    val context: Context,
    val itemList: MutableList<PetsitterItem>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<PetsitterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetsitterViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PetsitterViewHolder(MachingItemBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: PetsitterViewHolder, position: Int) {
        val data = itemList.get(position)
        holder.binding.run {
            machingNickname.text = "${data.petsitternickname}"
            machingType.text=data.caretype
            machingType.text = data.caretype
        }


        val imgRef = MyApplication.storage.reference.child("images/${data.userdocid}.jpg")
        imgRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(context)
                    .load(task.result)
                    .into(holder.binding.machingImageView)
            }

            holder.binding.root.setOnClickListener {
                // 클릭 이벤트 발생 시 인터페이스를 통해 데이터 전달
                listener.onItemClick(data.docId)
            }


        }
    }
}
