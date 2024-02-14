package org.bitc.petpalapp.recyclerviewAdapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bitc.petpalapp.ChatActivity
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.databinding.SetMatcingItemBinding
import org.bitc.petpalapp.model.UserInfo
import org.bitc.petpalapp.model.ApplicationItem
import org.bitc.petpalapp.ui.myhome.dateToString
import java.text.SimpleDateFormat
import java.util.Date


class setMyViewHolder(val binding: SetMatcingItemBinding) : RecyclerView.ViewHolder(binding.root)

class setMatchingAdapter(val context: Context, val itemList: MutableList<ApplicationItem>) :
    RecyclerView.Adapter<setMyViewHolder>() {

    lateinit var petsitterId: String
    lateinit var applierId: String
    lateinit var petsitternickname: String
    lateinit var appliernickname: String
    lateinit var userdocid: String
    lateinit var petsitterType: String
    lateinit var useraddress: String


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
        petsitternickname = data.petsitterNickname.toString()
        applierId = data.applierId.toString()
        appliernickname = data.applierNickname.toString()
        petsitterType = data.petsitterType.toString()

        // 데이터를 가져온 이후에 intent 생성 및 전달
        val intent = Intent(context, ChatActivity::class.java)
        //넘길 데이터
        intent.putExtra("applieremail", applierId)
        intent.putExtra("appliernickname", appliernickname)
        intent.putExtra("petsitternickname", petsitternickname)
        intent.putExtra("petsttteruid", petsitterId)

        holder.binding.setOpenChatroom.setOnClickListener {
            context.startActivity(intent)

        }


        MyApplication.db.collection("users")
            .whereEqualTo("email", applierId)
            .get()
            .addOnSuccessListener { documents ->
                documents.firstOrNull()?.let { document ->
                    userdocid = document.id
                    val user = document.toObject(UserInfo::class.java)
                    useraddress = user?.address.toString()

                    Log.d("확인", "$userdocid")

                    holder.binding.run {
                        setMatchingNickname.text = "${data.applierNickname}"
                        setAddress.text = useraddress
                        acceptbtn.visibility =
                            if (data.status == "대기중") View.VISIBLE else View.INVISIBLE
                        cardviewStatus.visibility =
                            if (data.status == "수락") View.VISIBLE else View.INVISIBLE
                        accpettext.visibility =
                            if (data.status == "수락") View.VISIBLE else View.INVISIBLE

                        acceptbtn.setOnClickListener {
                            updateStore(data.docId.toString())
                            holder.binding.run {
                                acceptbtn.visibility = View.INVISIBLE
                                accpettext.visibility = View.VISIBLE
                                cardviewStatus.visibility = View.VISIBLE

                            }
                        }
                    }

                    val imgRef = MyApplication.storage.reference.child("userimages/${userdocid}.jpg")
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

                }
            }
            .addOnFailureListener { exception ->
                // 에러 처리
                Log.e("Firebase", "Error getting documents: ", exception)
            }// MyApplication.db 끝
    }



    fun updateStore(docId: String) {

        // Firebase에 저장할 데이터 모델 객체 생성
        val application = ApplicationItem(
            applierId = applierId,
            applierNickname = appliernickname,
            petsitterId = petsitterId,
            petsitterNickname = petsitternickname,
            status = "수락",
            petsitterType = petsitterType,
            date = dateToString(Date())
        )
        // Firebase에 저장
        MyApplication.db.collection("applications").document(docId)
            .set(application)
            .addOnSuccessListener {

                Log.d("Firestore", "Document updated with ID: $docId")

                // users 컬렉션에서 email 필드가 userEmail과 일치하는 문서 찾기
                val userDocRef = MyApplication.db.collection("users").whereEqualTo("email", MyApplication.email)

                userDocRef.get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val user = document.toObject(UserInfo::class.java)
                            // 문서를 찾았을 경우 업데이트할 필드 설정
                            val newData = hashMapOf(
                                "point" to (user.point?.plus(50) ?: 50),
                                "petsittercount" to (user.petsittercount?.plus(1) ?: 1)
                            )

                            // 문서 업데이트
                            document.reference.update(newData as Map<String, Any>)
                                .addOnSuccessListener {
                                    // 업데이트 성공
                                    println("DocumentSnapshot successfully updated!")
                                }
                                .addOnFailureListener { e ->
                                    // 업데이트 실패
                                    println("Error updating document: $e")
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // 문서 조회 실패
                        println("Error getting documents: $exception")
                    }

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
