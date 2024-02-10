package org.bitc.petpalapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.bitc.petpalapp.databinding.ActivityChatBinding

import org.bitc.petpalapp.model.ChatRoom
import org.bitc.petpalapp.model.Messages
import org.bitc.petpalapp.model.UserInfo
import org.bitc.petpalapp.recyclerviewAdapter.MessageAdapter2


class ChatActivity : AppCompatActivity() {

    private lateinit var receiverNickName: String
    private lateinit var receiverEmail: String
    private lateinit var receiverUid: String
    private lateinit var senderNickName: String
    private lateinit var senderEmail: String
    private lateinit var senderUid: String


    private val messageList = mutableListOf<Messages>()

    //바인딩 객체
    private lateinit var binding: ActivityChatBinding

    private lateinit var auth: FirebaseAuth //인증 객체
    private lateinit var rdb: DatabaseReference //DB 객체



    private lateinit var messageAdapter: MessageAdapter2

    private lateinit var roomId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        //초기화
        auth = FirebaseAuth.getInstance()
        rdb = FirebaseDatabase.getInstance().reference

        // 채팅 입력 후 스크롤을 맨 아래로 이동
        scrollToBottom()

        MyApplication.db.collection("users")
            .whereEqualTo("email", MyApplication.email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = document.toObject(UserInfo::class.java)


                    if(!intent.getStringExtra("noti_senderEmail").isNullOrEmpty()){
                        senderEmail = intent.getStringExtra("noti_senderEmail").toString()
                        senderNickName = intent.getStringExtra("noti_senderNickName").toString()
                        senderUid = removeSpecialCharacters(senderEmail)
                        receiverEmail =  intent.getStringExtra("noti_receiverEmail").toString()
                        receiverNickName = intent.getStringExtra("noti_receiverNickName").toString()
                       receiverUid = removeSpecialCharacters(receiverEmail)
                    } else{
                        //넘어온 데이터 변수에 담기
                        senderNickName = user.nickname.toString()
                        senderEmail= MyApplication.email.toString()
                        senderUid = removeSpecialCharacters(senderEmail)
                        receiverNickName = intent.getStringExtra("petsitternickname").toString()
                        receiverEmail = intent.getStringExtra("petsttteruid").toString()
                        receiverUid = removeSpecialCharacters(receiverEmail)

                    }

                    if (senderEmail==receiverEmail){
                        receiverNickName = intent.getStringExtra("appliernickname").toString()
                        receiverEmail = intent.getStringExtra("applieremail").toString()
                        receiverUid = removeSpecialCharacters(receiverEmail)
                    }


                    val message = binding.edtMessage.text.toString()
                    // 채팅방이 이미 존재하는지 확인하고, 존재한다면 해당 채팅방 ID를 가져옴
                    roomId = getExistingChatRoomId(senderUid, receiverUid, message)

                    messageAdapter = MessageAdapter2(this, messageList, senderUid)
                    //리사이클러뷰
                    binding.recyclerMessages.layoutManager = LinearLayoutManager(this)
                    binding.recyclerMessages.adapter = messageAdapter
                    scrollToBottom()

                    Log.d("Uid", "$receiverUid")


                    //상대방 이름 보여주기
                    binding.recevierName.text = "${receiverNickName}님과의 대화"

                    receiveMessages(roomId, messageList, messageAdapter)

                    binding.btnSubmit.setOnClickListener {
                        val message = binding.edtMessage.text.toString()
                        if (roomId == "-1") {
                            val roomid = createChatRoom(senderUid, receiverUid, message)
                            receiveMessages(roomid, messageList, messageAdapter)
                        } else {
                            sendMessage(roomId, senderUid, message)
                            // 메시지 수신 및 RecyclerView 업데이트
                            receiveMessages(roomId, messageList, messageAdapter)
                        }
                        //입력값 초기화
                        binding.edtMessage.setText("")
                        // 키패드 숨기기
                        hideKeyboard(binding.edtMessage)

                    }

                }
            }
    }


    // 메시지 전송 함수
    fun sendMessage(roomId: String, senderId: String, text: String) {
        val timestamp = System.currentTimeMillis()
        val messages = Messages(senderId, text, timestamp)
        //초기화
        val rdb = FirebaseDatabase.getInstance().reference

        // chatrooms/{roomId}/messages 경로에 메시지 정보 저장
        rdb.child("chatrooms").child(roomId).child("messages").push().setValue(messages)
    }

    // 메시지 수신 및 리사이클러뷰 업데이트 함수
    fun receiveMessages(
        roomId: String,
        messageList: MutableList<Messages>,
        adapter: MessageAdapter2
    ) {
        //초기화
        val rdb = FirebaseDatabase.getInstance().reference
        // chatrooms/{roomId}/messages 경로에 대한 ValueEventListener 등록
        rdb.child("chatrooms").child(roomId).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Messages::class.java)
                        message?.let {
                            messageList.add(it)


                            // 새로운 메시지가 도착할 때마다 알림 띄우기
                            if (it.senderId != senderUid) {
                                showNotification(it.text)
                            }
                        }
                    }

                    // 리사이클러뷰 업데이트
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // 처리 중 오류 발생 시 처리
                }
            })
    }

    // 채팅룸 생성 및 메시지 전송
    fun createChatRoom(participant1: String, participant2: String, text: String): String {
        val participants = listOf(participant1, participant2).sorted()
        val roomId = participants.joinToString("_")

        val chatRoom = ChatRoom(roomId, participants)

        // chatrooms 경로에 채팅룸 정보 저장
        //초기화
        val rdb = FirebaseDatabase.getInstance().reference
        rdb.child("chatrooms").child(roomId).setValue(chatRoom)

        // 메시지 전송
        //participant1가 sender
        sendMessage(roomId, participant1, text)
        return roomId
    }


    // 기존에 생성된 채팅방이 있는지 확인하고, 있다면 해당 채팅방의 ID를 반환
    private fun getExistingChatRoomId(
        participant1: String,
        participant2: String,
        messsage: String
    ): String {
        val participants = listOf(participant1, participant2).sorted()
        var roomId = participants.joinToString("_")

        //초기화
        val rdb = FirebaseDatabase.getInstance().reference
        // chatrooms/{roomId} 경로에 대한 ValueEventListener 등록
        rdb.child("chatrooms").child(roomId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // 채팅방이 이미 존재하는 경우
                        // 해당 채팅방의 ID를 저장
                        val existingRoomId =
                            snapshot.child("roomId").getValue(String::class.java) ?: ""
                        roomId = existingRoomId


                    } else {
                        // 채팅방이 존재하지 않는 경우

                        roomId = "-1"

                    }

                    var messageList = mutableListOf<Messages>()
                    messageList = mutableListOf()

                    val messageAdapter =
                        MessageAdapter2(ChatActivity(), messageList, participant1)

                    // 초기 메시지 수신 및 RecyclerView 업데이트
                    receiveMessages(roomId, messageList, messageAdapter)
                }

                override fun onCancelled(error: DatabaseError) {
                    // 처리 중 오류 발생 시 처리
                }
            }
            )
        return roomId
    }
    // 키패드를 숨기는 함수
    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //이메일에서 특수문자 제거 하는 함수
    fun removeSpecialCharacters(email: String): String {
        return email.replace("[@.]".toRegex(), "")
    }

    // RecyclerView를 맨 아래로 스크롤하는 함수
    private fun scrollToBottom() {
        binding.recyclerMessages.scrollToPosition(messageList.size - 1)
    }


    // 알림 띄우기 함수
    private fun showNotification(message: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0 이상인 경우 Notification Channel을 설정
            val channelId = "channel_id"
            val channel = NotificationChannel(
                channelId,
                "Channel Name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.setShowBadge(true)
            channel.lightColor = Color.RED
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("noti_senderEmail", MyApplication.email.toString())
        intent.putExtra("noti_senderNickName", senderNickName)
        intent.putExtra("noti_receiverNickName", receiverNickName)
        intent.putExtra("noti_receiverEmail", receiverEmail)


        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, "channel_id")
            .setContentTitle(receiverNickName)
            .setContentText(message)
            .setSmallIcon(R.drawable.petpal_small)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}

