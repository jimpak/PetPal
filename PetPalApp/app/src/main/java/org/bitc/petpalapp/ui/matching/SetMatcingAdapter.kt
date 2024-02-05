package org.bitc.petpalapp.ui.matching

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.databinding.GetMatchingItemBinding
import org.bitc.petpalapp.databinding.SetMatcingItemBinding
import org.bitc.petpalapp.ui.mypet.util.ApplicationItem
import java.text.SimpleDateFormat
import java.util.Date

class setMyViewHolder(val binding : SetMatcingItemBinding) : RecyclerView.ViewHolder(binding.root)

class setMatchingAdapter(val context: Context, val itemList:MutableList<ApplicationItem>) : RecyclerView.Adapter<setMyViewHolder>() {

    lateinit var petsitterId : String
    lateinit var applierId : String
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
        applierId= data.applierId.toString()
        holder.binding.run{
            setMatchingNickname.text = "${data.applierId}"
            acceptbtn.visibility = if (data.status == "대기중") View.VISIBLE else View.INVISIBLE
            accpettext.visibility=if(data.status=="수락") View.VISIBLE else View.INVISIBLE
        }

        holder.binding.acceptbtn.setOnClickListener {
            updateStore(data.docId.toString())

        }



    }


    private fun updateStore(docId: String){

        // Firebase에 저장할 데이터 모델 객체 생성
        val application= ApplicationItem(
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
