package org.bitc.petpalapp.ui.mypet

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentApplicantBinding
import org.bitc.petpalapp.databinding.FragmentPetDetailBinding
import org.bitc.petpalapp.ui.myhome.PetsitterItem
import org.bitc.petpalapp.ui.mypet.util.ApplicationItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class ApplicantFragment : Fragment() {
    private var _binding: FragmentApplicantBinding? = null
    private var petsitterId : String? = null

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

                    binding.tvresultNickname.setText(item?.petsitternickname)
                    binding.tvresultType.setText(item?.caretype)
                    binding.resiltAddress.text = item?.address
                    binding.resiltMyinfo.text= item?.myinfo

                    if (item?.service1!=null) {
                        binding.servicetext1.text = "⌨신분증 제공"
                    }

                    if (item?.service2!=null) {
                        binding.servicetext2.text = "✨놀아주기"
                    }

                    if (item?.service1!=null) {
                        binding.servicetext1.text = "🎠산책 서비스"
                    }
                    if (item?.service1!=null) {
                        binding.servicetext1.text = "❣노견 케어"
                    }

                    //'신청' db 저장용 펫시어터 아이디 얻기
                    petsitterId = item?.petsitterId
                }

                .addOnFailureListener { Exception ->
                    Log.d("bbb", "오류", Exception)
                }

            Log.d("bbb", "Received docId: $docId")
        } else {
            Log.e("bbb", "No docId received.")
        }

        binding.addFab.setOnClickListener {
            saveApplication()
            Toast.makeText(requireContext(), "신청 완료!!", Toast.LENGTH_SHORT).show()
        }
    }


    //돌봄 신청하기 버튼 누르면 db에 '신청' 컬렉션에 저장
    private fun saveApplication() {
        // Firebase에 저장할 데이터 모델 객체 생성
        val application= ApplicationItem(
            applierId = MyApplication.email,
            petsitterId = petsitterId,
            status = "대기중",
            date = dateToString(Date())
        )

        // Firebase에 저장
        MyApplication.db.collection("applications").add(application)
            .addOnSuccessListener {documentReference ->
                val docId = documentReference.id
                Log.d("Firestore", "Document added with ID: $docId")

            }
            .addOnFailureListener{
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