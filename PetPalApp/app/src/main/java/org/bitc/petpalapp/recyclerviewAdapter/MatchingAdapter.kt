package org.bitc.petpalapp.recyclerviewAdapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.databinding.GetMatchingItemBinding
import org.bitc.petpalapp.model.ApplicationItem
import org.bitc.petpalapp.model.UserInfo

class GetMacthingViewHolder(val binding: GetMatchingItemBinding) :
    RecyclerView.ViewHolder(binding.root)

class MatchingAdapter(val context: Context, val itemList: MutableList<ApplicationItem>) :
    RecyclerView.Adapter<GetMacthingViewHolder>() {

    lateinit var petsitterid: String
    lateinit var usersdocId: String
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetMacthingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return GetMacthingViewHolder(GetMatchingItemBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: GetMacthingViewHolder, position: Int) {
        val data = itemList.get(position)
        petsitterid = data.petsitterId.toString()
        holder.binding.run {
            getMachingNickname.text = "${data.petsitterNickname}"
            getMachingStauts.text = "${data.status}"



            MyApplication.db.collection("users")
                .whereEqualTo("email", petsitterid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        usersdocId = document.id
                        val user = document.toObject(UserInfo::class.java)
                    }

                    val imgRef = MyApplication.storage.reference.child("images/${usersdocId}.jpg")
                    imgRef.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Glide.with(context)
                                .load(task.result)
                                .into(holder.binding.getMachingImageView)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("aaa", "서버 데이터 획득 실패", exception)
                }


        }
    }
}
