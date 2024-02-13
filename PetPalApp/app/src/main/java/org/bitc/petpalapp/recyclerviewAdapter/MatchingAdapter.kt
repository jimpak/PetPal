package org.bitc.petpalapp.recyclerviewAdapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bitc.petpalapp.ChatActivity
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.databinding.GetMatchingItemBinding

import org.bitc.petpalapp.model.ApplicationItem
import org.bitc.petpalapp.model.UserInfo

class GetMacthingViewHolder(val binding: GetMatchingItemBinding) :
    RecyclerView.ViewHolder(binding.root)

class MatchingAdapter(val context: Context, val itemList: MutableList<ApplicationItem>) :
    RecyclerView.Adapter<GetMacthingViewHolder>() {

    lateinit var petsitterId: String
    lateinit var petsitternickname: String
    lateinit var usersdocId: String
    lateinit var appliernickname: String
    lateinit var applierId: String
    lateinit var petsitterType: String
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetMacthingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return GetMacthingViewHolder(GetMatchingItemBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: GetMacthingViewHolder, position: Int) {
        val data = itemList.get(position)
        petsitterId = data.petsitterId.toString()
        petsitternickname = data.petsitterNickname.toString()
        usersdocId = data.applierId.toString()
        appliernickname = data.applierNickname.toString()
        applierId = data.applierId.toString()
        petsitterType = data.petsitterType.toString()



        holder.binding.run {
            getMatchingNickname.text = "${data.petsitterNickname}"
            getMachingStauts.text = "${data.status}"
            getMachingType.setText(petsitterType)



            MyApplication.db.collection("users")
                .whereEqualTo("email", petsitterId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        usersdocId = document.id
                        val user = document.toObject(UserInfo::class.java)
                    }

                    val imgRef = MyApplication.storage.reference.child("userimages/${usersdocId}.jpg")
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

            getOpenChatroom.setOnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                //넘길 데이터
                intent.putExtra("applieremail", applierId)
                intent.putExtra("appliernickname", appliernickname)
                intent.putExtra("petsitternickname", petsitternickname)
                intent.putExtra("petsttteruid", petsitterId)
                context.startActivity(intent)
            }
        }

    }
}
