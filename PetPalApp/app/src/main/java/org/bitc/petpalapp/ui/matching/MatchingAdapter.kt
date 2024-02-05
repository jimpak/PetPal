package org.bitc.petpalapp.ui.matching

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.bitc.petpalapp.databinding.GetMatchingItemBinding
import org.bitc.petpalapp.databinding.MachingItemBinding
import org.bitc.petpalapp.ui.myhome.PetsitterItem
import org.bitc.petpalapp.ui.mypet.util.ApplicationItem
import org.bitc.petpalapp.ui.mypet.util.OnItemClickListener

class MyViewHolder(val binding : GetMatchingItemBinding) : RecyclerView.ViewHolder(binding.root)

class MatchingAdapter(val context: Context, val itemList:MutableList<ApplicationItem>) : RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MyViewHolder(GetMatchingItemBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = itemList.get(position)
        holder.binding.run{
            getMachingNickname.text = "${data.petsitterId}"
             getMachingStauts.text = "${data.status}"

        }

//        val imgRef = MyApplication.storage.reference.child("images/${data.docId}.jpg")
//        imgRef.downloadUrl.addOnCompleteListener{ task ->
//            if(task.isSuccessful){
//                Glide.with(context)
//                    .load(task.result)
//                    .into(holder.binding.itemImageView)
//
//            }

//
//        holder.binding.root.setOnClickListener{
//            // 클릭 이벤트 발생 시 인터페이스를 통해 데이터 전달
//            listener.onItemClick(data.docId)
//        }
    }
}
