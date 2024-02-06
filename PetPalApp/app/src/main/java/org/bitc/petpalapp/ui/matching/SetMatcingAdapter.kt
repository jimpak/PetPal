package org.bitc.petpalapp.ui.matching

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.databinding.GetMatchingItemBinding
import org.bitc.petpalapp.databinding.SetMatcingItemBinding
import org.bitc.petpalapp.model.UserInfo
import org.bitc.petpalapp.ui.myhome.dateToString
import org.bitc.petpalapp.ui.mypet.util.ApplicationItem
import java.text.SimpleDateFormat
import java.util.Date

class setMyViewHolder(val binding: SetMatcingItemBinding) : RecyclerView.ViewHolder(binding.root)

class setMatchingAdapter(val context: Context, val itemList: MutableList<ApplicationItem>) :
    RecyclerView.Adapter<setMyViewHolder>() {

    lateinit var petsitterId: String
    lateinit var applierId: String
    lateinit var userdocid: String
    lateinit var useraddress : String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): setMyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return setMyViewHolder(SetMatcingItemBinding.inflate(layoutInflater))
    }


    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: setMyViewHolder, position: Int) {
        val data = itemList.get(position)

        petsitterId = data.petsitterId.toString()
        applierId = data.applierId.toString()


        MyApplication.db.collection("users")
            .whereEqualTo("email", applierId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    userdocid = document.id

                    val user = document.toObject(UserInfo::class.java)
                    useraddress  =user.address.toString()

                    Log.d("확인", "$userdocid")
                }

                val imgRef = MyApplication.storage.reference.child("images/${userdocid}.jpg")
                imgRef.downloadUrl.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Glide.with(context)
                            .load(task.result)
                            .into(holder.binding.setMatchingImageView)
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("aaa", "서버 데이터 획득 실패", exception)
            }


            holder.binding.run {
                setMatchingNickname.text = "${data.applierNickname}"
                setAddress.text = useraddress
                acceptbtn.visibility = if (data.status == "대기중") View.VISIBLE else View.INVISIBLE
                accpettext.visibility = if (data.status == "수락") View.VISIBLE else View.INVISIBLE
            }

            holder.binding.acceptbtn.setOnClickListener {
                updateStore(data.docId.toString())
                holder.binding.run {
                    acceptbtn.visibility = View.INVISIBLE
                    accpettext.visibility = View.VISIBLE
                }

            }

        }
    }


    fun updateStore(docId: String) {

        // Firebase에 저장할 데이터 모델 객체 생성
        val application = ApplicationItem(
            applierId = applierId,
            petsitterId = petsitterId,
            status = "수락",
            date = dateToString(Date())
        )
        // Firebase에 저장
        MyApplication.db.collection("applications").document(docId)
            .set(application)
            .addOnSuccessListener {

                Log.d("Firestore", "Document updated with ID: $docId")
            }
            .addOnFailureListener {
                Log.d("kkang", "data update error", it)
            }
    }

    fun dateToString(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(date)
    }
}

