package org.bitc.petpalapp.ui.myhome

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.databinding.FragmentCareRegisterBinding
import org.bitc.petpalapp.model.PetsitterItem
import org.bitc.petpalapp.model.UserInfo

class CareRegisterFragment : Fragment() {

    private var _binding: FragmentCareRegisterBinding? = null
    lateinit var usernickname: String
    lateinit var userphone: String
    lateinit var usersdocId: String


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCareRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnRegiPetsitter2.setOnClickListener {
            saveStore()
        }
    }

    private fun saveStore() {
        // Firebase에 저장할 데이터 모델 객체 생성
        // Firebase의 users 컬렉션에서 정보 가져오기

        MyApplication.db.collection("users")
            .whereEqualTo("email", MyApplication.email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    usersdocId = document.id
                    val user = document.toObject(UserInfo::class.java)
                    usernickname = user.nickname.toString()
                    userphone = user.phone.toString()

                    Log.d("확인", "$usersdocId")

                    // 데이터를 가져온 후에 작업을 수행하도록 호출
                    continueStoreSave()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("aaa", "서버 데이터 획득 실패", exception)
            }
    }

    private fun continueStoreSave() {
        Log.d("확인", "$usersdocId")

        val petsitter = PetsitterItem(
            userdocid = usersdocId,
            petsitterId = MyApplication.email,
            petsitternickname = usernickname,
            phone = userphone,
            service1 = if (binding.switch1.isChecked) "신분증 제공" else "null",
            service2 = if (binding.switch2.isChecked) "놀아주기" else "null",
            service3 = if (binding.switch3.isChecked) "산책서비스" else "null",
            service4 = if (binding.switch4.isChecked) "건강 케어" else "null",
            caretype = if (binding.chDog.isChecked) "댕이돌봄" else "냥이돌봄",
            possibletime = binding.editPossibletime.text.toString(),
            address = binding.editAddress.text.toString(),
            myinfo = binding.editMyinfo.text.toString()
        )

        // Firebase에 저장
        MyApplication.db.collection("petsitters").add(petsitter)
            .addOnSuccessListener { documentReference ->
                val docId = documentReference.id
                Log.d("Firestore", "Document added with ID: $docId")
                Toast.makeText(requireContext(), "등록 완료!", Toast.LENGTH_SHORT)

                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Log.d("fail", "data save error", it)
                Toast.makeText(requireContext(), "등록 실패ㅠㅠ", Toast.LENGTH_SHORT)
            }
    }
}
