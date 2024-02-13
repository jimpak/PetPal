package org.bitc.petpalapp.recyclerviewAdapter

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bitc.petpalapp.ChatActivity
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
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
            itemview.background= ContextCompat.getDrawable(context, R.drawable.item_background)
        }


        val imgRef = MyApplication.storage.reference.child("userimages/${data.userdocid}.jpg")
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


// ItemDecoration 클래스 생성
class ItemDecorator(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position != RecyclerView.NO_POSITION) {
            // 아이템의 높이를 가져와서 간격을 적용
            val itemHeight = view.height
            val itemwWidth = view.width
            outRect.left = space
            outRect.right = space
            outRect.top = space
            outRect.bottom = space
        }
    }
}