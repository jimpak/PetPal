package org.bitc.petpalapp.ui.myhome

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentCareRegisterBinding
import java.util.Date

class CareRegisterFragment : Fragment() {

    private var _binding: FragmentCareRegisterBinding? = null

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
        val petsitter= PetsitterItem(
            petsitterId = MyApplication.email,
            petsitternickname = binding.editNickname.text.toString(),
            phone = binding.editPhone.text.toString(),
            service1 = if (binding.switch1.isChecked) "신분증 제공" else "null",
            service2 = if (binding.switch2.isChecked) "놀아주기" else "null",
            service3 = if (binding.switch3.isChecked) "산책서비스" else "null",
            service4 = if (binding.switch4.isChecked) "노견케어" else "null",
            caretype =  if (binding.chDog.isChecked) "댕이돌봄" else "냥이돌봄",
            possibletime = binding.editPossibletime.text.toString(),
            address = binding.editAddress.text.toString(),
            myinfo = binding.editMyinfo.text.toString(),
        )

        // Firebase에 저장
        MyApplication.db.collection("petsitters").add(petsitter)
            .addOnSuccessListener {documentReference ->
                val docId = documentReference.id
                Log.d("Firestore", "Document added with ID: $docId")

            }
            .addOnFailureListener{
                Log.d("fail", "data save error", it)
            }

    }
}