package org.bitc.petpalapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.bitc.petpalapp.databinding.ActivityChatTestBinding
import org.bitc.petpalapp.model.ChatRoom
import org.bitc.petpalapp.model.Messages
import org.bitc.petpalapp.model.UserInfo
import org.bitc.petpalapp.recyclerviewAdapter.MessageAdapter2


class ChatActivity : AppCompatActivity() {
    private lateinit var receiverNickName: String
    private lateinit var receiverUid: String
    private val messageList = mutableListOf<Messages>()

    //바인딩 객체
    private lateinit var binding: ActivityChatTestBinding

    private lateinit var auth: FirebaseAuth //인증 객체
    private lateinit var rdb: DatabaseReference //DB 객체


    private lateinit var senderNickName: String
    private lateinit var senderUid: String
    private lateinit var messageAdapter: MessageAdapter2

    private lateinit var roomId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatTestBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    override fun onStart() {


        super.onStart()
        //초기화
        auth = FirebaseAuth.getInstance()
        rdb = FirebaseDatabase.getInstance().reference

        MyApplication.db.collection("users")
            .whereEqualTo("email", MyApplication.email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = document.toObject(UserInfo::class.java)
                    senderNickName = user.nickname.toString()
                    senderUid = MyApplication.email.toString()

                    //넘어온 데이터 변수에 담기
                    receiverNickName = intent.getStringExtra("petsitternickname").toString()
                    receiverUid = intent.getStringExtra("petsttteruid").toString()


                    val message = binding.edtMessage.text.toString()
                    // 채팅방이 이미 존재하는지 확인하고, 존재한다면 해당 채팅방 ID를 가져옴
                    roomId = getExistingChatRoomId(senderNickName, receiverNickName, message)

                    messageAdapter = MessageAdapter2(this, messageList, senderNickName)
                    //리사이클러뷰
                    binding.recyclerMessages.layoutManager = LinearLayoutManager(this)
                    binding.recyclerMessages.adapter = messageAdapter

                    Log.d("Uid", "$receiverUid")


                    //액션바에 상대방 이름 보여주기
                    supportActionBar?.title = "To.${receiverNickName}"

                    receiveMessages(roomId, messageList, messageAdapter)

                    binding.btnSubmit.setOnClickListener {
                        val message = binding.edtMessage.text.toString()
                        if (roomId == "-1") {
                            val roomid = createChatRoom(senderNickName, receiverNickName, message)
                            receiveMessages(roomid, messageList, messageAdapter)
                        } else {
                            sendMessage(roomId, senderNickName, message)
                            // 메시지 수신 및 RecyclerView 업데이트
                            receiveMessages(roomId, messageList, messageAdapter)
                        }
                        //입력값 초기화
                        binding.edtMessage.setText("")

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

}

