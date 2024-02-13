package org.bitc.petpalapp.ui.mypet

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


import com.bumptech.glide.Glide
import org.bitc.petpalapp.ChatActivity
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
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
    private var phoneNumber: String? = null

    lateinit var docId: String
    private val binding get() = _binding!!

    private val CALL_PHONE_PERMISSION_REQUEST_CODE = 123
    private val SEND_SMS_PERMISSION_REQUEST_CODE = 456

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

                     phoneNumber = item?.phone.toString()
                    binding.opneCall.setOnClickListener(View.OnClickListener {
                        // 전화 거는 이벤트
                        if (isCallPhonePermissionGranted()) {
                            makePhoneCall(phoneNumber!!)
                        } else {
                            requestCallPhonePermission()
                        }
                    })

                    binding.openMessage.setOnClickListener {
                        // 문자 메시지 보내기 이벤트
                        if (isSendSmsPermissionGranted()) {
                            sendSms()
                        } else {
                            requestSendSmsPermission()
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


        //펫시터 받기 신청
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isCallPhonePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.CALL_PHONE
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestCallPhonePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.CALL_PHONE),
            CALL_PHONE_PERMISSION_REQUEST_CODE
        )
    }

    private fun makePhoneCall(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startActivity(callIntent)
        } else {
            // 권한이 없는 경우
            requestCallPhonePermission()
        }
    }


    // SMS 전송 권한이 있는지 확인하는 함수
    private fun isSendSmsPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    // SMS 전송 권한을 요청하는 함수
    private fun requestSendSmsPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.SEND_SMS),
            SEND_SMS_PERMISSION_REQUEST_CODE
        )
    }

    // SMS를 보내는 함수
    private fun sendSms() {
        val smsIntent = Intent(Intent.ACTION_SENDTO)
        smsIntent.data = Uri.parse("smsto:$phoneNumber")
        smsIntent.putExtra("sms_body", "안녕하세요, 문자 메시지 내용을 입력하세요.")
        startActivity(smsIntent)
    }


    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            SEND_SMS_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 사용자가 SMS 전송 권한을 허용한 경우
                    sendSms()
                } else {
                    // 사용자가 SMS 전송 권한을 거부한 경우
                    Toast.makeText(
                        requireContext(),
                        "SMS 전송 권한이 필요합니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            // 다른 권한에 대한 처리도 추가 가능
        }
    }

    fun dateToString(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(date)
    }
}