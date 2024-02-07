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
import org.bitc.petpalapp.MyApplication.Companion.auth
import org.bitc.petpalapp.databinding.ActivityChatBinding
import org.bitc.petpalapp.databinding.ActivityChatTest2Binding


import org.bitc.petpalapp.model.Message
import org.bitc.petpalapp.model.UserInfo
import org.bitc.petpalapp.recyclerviewAdapter.MessageAdapter

class ChatTest2Activity : AppCompatActivity() {

    private lateinit var receiverName: String
    private lateinit var receiverUid: String

    //바인딩 객체
    private lateinit var binding: ActivityChatTest2Binding

    private lateinit var auth: FirebaseAuth //인증 객체
    private lateinit var rdb: DatabaseReference //DB 객체

    private lateinit var receiverRoom: String //받는 대화방
    private lateinit var senderRoom: String // 보낸 대화방

    private lateinit var messageList: ArrayList<Message>
    private lateinit var senderNickname: String
    private lateinit var senderUid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatTest2Binding.inflate(layoutInflater)
        setContentView(binding.root)

//        초기화
        messageList = ArrayList()
        val messageAdapter: MessageAdapter = MessageAdapter(this, messageList)

        //리사이클러뷰
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messageAdapter

        //넘어온 데이터 변수에 담기

        receiverName = intent.getStringExtra("petsitternickname").toString()
        receiverUid = intent.getStringExtra("petsttteruid").toString()

        Log.d("Uid", "$receiverUid")

        auth = FirebaseAuth.getInstance()
        rdb = FirebaseDatabase.getInstance().reference


        MyApplication.db.collection("users")
            .whereEqualTo("email", MyApplication.email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = document.toObject(UserInfo::class.java)
                    senderNickname = user.nickname.toString()
                    senderUid = MyApplication.email.toString()


                    //보낸이방
                    senderRoom = receiverName + senderNickname


                    //받는이방
                    receiverRoom = senderNickname + receiverName


                    //액션바에 상대방 이름 보여주기
                    supportActionBar?.title = receiverName


                    binding.sendBtn.setOnClickListener {
                        val message = binding.messageEdit.text.toString()
                        val messageObject = Message(message, senderUid)

                        //데이터 저장
                        rdb.child("chats").child(senderRoom).child("messages").push()
                            .setValue(messageObject).addOnSuccessListener {
                                //저장 성공하면
                                rdb.child("chats").child(receiverRoom).child("messages").push()
                                    .setValue(messageObject)
                            }

                        //입력값 초기화
                        binding.messageEdit.setText("")
                    }

                    //메시지 가져오기
                    rdb.child("chats").child(senderRoom).child("messages")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                messageList.clear()

                                for (postSnapshot in snapshot.children) {

                                    val message = postSnapshot.getValue(Message::class.java)
                                    messageList.add(message!!)
                                }
                                //적용
                                messageAdapter.notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })


                }
            }
            .addOnFailureListener { exception ->
                Log.d("aaa", "서버 데이터 획득 실패", exception)
            }


    }
}