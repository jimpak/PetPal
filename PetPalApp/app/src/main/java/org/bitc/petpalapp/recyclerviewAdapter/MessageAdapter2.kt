package org.bitc.petpalapp.recyclerviewAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.bitc.petpalapp.R
import org.bitc.petpalapp.model.Messages
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MessageAdapter2(private val context: Context, private val messageList: MutableList<Messages>, senderUid: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val senderUid = senderUid
    private val receive = 1 // 받는 타입
    private val send = 2 // 보내는 타입

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == receive) { // 받는 화면
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.receive_message_item, parent, false)
            ReceiveViewHolder(view)
        } else { // 보내는 화면
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.send_message_item, parent, false)
            SendViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        if (holder.itemViewType == send) {
            val viewHolder = holder as SendViewHolder
            viewHolder.sendMessage.text = currentMessage.text
            viewHolder.sendTime.text = formatTime(currentMessage.timestamp)
        } else { // 받는 데이터
            val viewHolder = holder as ReceiveViewHolder
            viewHolder.receiveMessage.text = currentMessage.text
            viewHolder.receiveTime.text = formatTime(currentMessage.timestamp)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].senderId == senderUid) {
            send
        } else {
            receive
        }
    }

    // 보낸 쪽
    class SendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sendMessage: TextView = itemView.findViewById(R.id.send_message_text2)
        val sendTime: TextView = itemView.findViewById(R.id.txt_date)
    }

    // 받는 쪽
    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage: TextView = itemView.findViewById(R.id.receive_message_text2)
        val receiveTime: TextView = itemView.findViewById(R.id.txt_date)
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return sdf.format(calendar.time)
    }
}