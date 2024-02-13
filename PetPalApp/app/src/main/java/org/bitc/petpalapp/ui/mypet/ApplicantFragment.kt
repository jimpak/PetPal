package org.bitc.petpalapp.ui.mypet

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi


import com.bumptech.glide.Glide
import org.bitc.petpalapp.ChatActivity
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.databinding.FragmentApplicantBinding
import org.bitc.petpalapp.model.UserInfo
import org.bitc.petpalapp.model.PetsitterItem
import org.bitc.petpalapp.model.ApplicationItem
import java.text.SimpleDateFormat
import java.util.Date

class ApplicantFragment : Fragment() {
    private var _binding: FragmentApplicantBinding? = null
    private var petsitterId: String? = null
    private var userdocid: String? = null
    private var petsitternickname: String? = null
    private var appliernickname: String? = null
    private var petsitterType: String? = null

    lateinit var docId: String
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApplicantBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // 프래그먼트로 전달된 데이터를 Bundle에서 꺼내기
        docId = arguments?.getString("docId").toString()
        // docId를 사용하여 필요한 작업 수행
        if (docId != null) {
            val docRef = MyApplication.db.collection("petsitters").document(docId)
            docRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    val item = documentSnapshot.toObject(PetsitterItem::class.java)

                    //'신청' db 저장용 펫시어터 아이디 얻기
                    userdocid = item?.userdocid
                    petsitternickname = item?.petsitternickname
                    petsitterId = item?.petsitterId
                    petsitterType = item?.caretype

                    //view에 데이터 넣기
                    binding.tvresultNickname.setText(item?.petsitternickname)
                    binding.tvresultType.setText(item?.caretype)
                    binding.resiltAddress.text = item?.address
                    binding.resiltMyinfo.text = item?.myinfo

                    if (item?.service1 != null) {
                        binding.servicetext1.text = "⌨신분증 제공"
                    }

                    if (item?.service2 != null) {
                        binding.servicetext2.text = "✨놀아주기"
                    }

                    if (item?.service1 != null) {
                        binding.servicetext1.text = "🎠산책 서비스"
                    }
                    if (item?.service1 != null) {
                        binding.servicetext1.text = "❣건강 케어"
                    }

                    //펫시터의 프로필 이미지 넣기
                    val imgRef = MyApplication.storage.reference.child("userimages/${userdocid}.jpg")
                    imgRef.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Glide.with(requireContext())
                                .load(task.result)
                                .into(binding.petsitterProfileimg)
                        }
                    }

                }

                .addOnFailureListener { Exception ->
                    Log.d("bbb", "오류", Exception)
                }

            Log.d("bbb", "Received docId: $docId")
        } else {
            Log.e("bbb", "No docId received.")
        }


        //돌보미 받기 신청
        binding.addFab.setOnClickListener {
            saveApplication()
            Toast.makeText(requireContext(), "신청 완료!!", Toast.LENGTH_SHORT).show()
        }


        //채팅방 열기
        binding.openChatBtn.setOnClickListener {
            val intent = Intent(activity, ChatActivity::class.java)
            //넘길 데이터
            intent.putExtra("appliernickname", appliernickname)
            intent.putExtra("petsitternickname", petsitternickname)
            intent.putExtra("petsttteruid", petsitterId)
            startActivity(intent)
        }


    } //onViewCreated 끝


    //돌봄 신청하기 버튼 누르면 db에 '신청' 컬렉션에 저장
    private fun saveApplication() {
     // Firebase의 users 컬렉션에서 정보 가져오기

        MyApplication.db.collection("users")
            .whereEqualTo("email", MyApplication.email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    userdocid = document.id
                    val user = document.toObject(UserInfo::class.java)
                    appliernickname = user.nickname.toString()

                    Log.d("확인", "$userdocid")

                    // 데이터를 가져온 후에 작업을 수행하도록 호출
                    DataFirstSave()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("aaa", "서버 데이터 획득 실패", exception)
            }
    }


    // Firebase에 저장할 데이터 모델 객체 생성
    private fun DataFirstSave() {

        val application = ApplicationItem(
            applierId = MyApplication.email,
            applierNickname = appliernickname,
            petsitterId = petsitterId,
            petsitterNickname = petsitternickname,
            petsitterType = petsitterType,
            status = "대기중",
            date = dateToString(Date())
        )

        // Firebase에 저장
        MyApplication.db.collection("applications").add(application)
            .addOnSuccessListener { documentReference ->
                val docId = documentReference.id
                Log.d("Firestore", "Document added with ID: $docId")

            }
            .addOnFailureListener {
                Log.d("fail", "data save error", it)
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun dateToString(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(date)
    }
}