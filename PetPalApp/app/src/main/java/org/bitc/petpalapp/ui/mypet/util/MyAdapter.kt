package org.bitc.petpalapp.ui.mypet.util

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.databinding.ItemMypetBinding
import java.time.LocalDate
import java.time.Period
import java.util.Date

class MyViewHolder(val binding : ItemMypetBinding) : RecyclerView.ViewHolder(binding.root)

class MyAdapter(val context: Context, val itemList:MutableList<ItemData>, private val listener: OnItemClickListener) : RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MyViewHolder(ItemMypetBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = itemList.get(position)
        holder.binding.run{
            textName.text = data.petname
            textType.text=data.type
            textXY.text=data.gender
            textBirtyday.text = data.birthday
        }


        val imgRef = MyApplication.storage.reference.child("images/${data.docId}.jpg")
        imgRef.downloadUrl.addOnCompleteListener{ task ->
            if(task.isSuccessful){
                Glide.with(context)
                    .load(task.result)
                    .into(holder.binding.petImg)

            }
        }

        holder.binding.root.setOnClickListener{

                // 클릭 이벤트 발생 시 인터페이스를 통해 데이터 전달
                listener.onItemClick(data.docId)
            }
        }
    }
