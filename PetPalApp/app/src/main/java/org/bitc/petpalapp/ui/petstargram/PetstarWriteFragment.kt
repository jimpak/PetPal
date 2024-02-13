package org.bitc.petpalapp.ui.petstargram

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentPetstarWriteBinding
import org.bitc.petpalapp.databinding.FragmentPetstargramTestBinding
import org.bitc.petpalapp.model.UserInfo
import org.bitc.petpalapp.recyclerviewAdapter.GridAdapter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class PetstarWriteFragment : Fragment() {
    private var _binding: FragmentPetstarWriteBinding? = null
    lateinit var filePath:String

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPetstarWriteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val requestLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                Glide
                    .with(requireActivity())
                    .load(it.data?.data)
                    .apply(RequestOptions().override(300, 300))
                    .centerCrop()
                    .into(binding.petstarImg)

                val cursor = requireActivity().contentResolver.query(
                    it.data?.data as Uri, arrayOf<String>(
                        MediaStore.Images.Media.DATA
                    ), null, null, null
                )
                cursor?.moveToFirst().let {
                    filePath = cursor?.getString(0) as String
                }
                Log.d("aaa", "filePath : $filePath")
            }

        binding.petstarOpengallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            requestLauncher.launch(intent)
        }

        binding.petstarInsert.setOnClickListener {
            //firebase저장하기
            savePetstarStor()
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




    //함수 정의
    fun savePetstarStor() {
        val petstar = mapOf(
            "email" to MyApplication.email,
            "content" to binding.petstarEdit.text.toString(),
            "date" to dateToString(Date())
        )

        MyApplication.db.collection("petstars")
            .add(petstar)
            .addOnSuccessListener {
                uploadImage(it.id)

                // users 컬렉션에서 email 필드가 userEmail과 일치하는 문서 찾기
                val userDocRef = MyApplication.db.collection("users").whereEqualTo("email", MyApplication.email)

                userDocRef.get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val user = document.toObject(UserInfo::class.java)
                            // 문서를 찾았을 경우 업데이트할 필드 설정
                            val newData = hashMapOf(
                                "point" to (user.point?.plus(20) ?: 20),
                                "petstarcount" to (user.petstarcount?.plus(1) ?: 1)
                            )

                            // 문서 업데이트
                            document.reference.update(newData as Map<String, Any>)
                                .addOnSuccessListener {
                                    // 업데이트 성공
                                    println("DocumentSnapshot successfully updated!")
                                }
                                .addOnFailureListener { e ->
                                    // 업데이트 실패
                                    println("Error updating document: $e")
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // 문서 조회 실패
                        println("Error getting documents: $exception")
                    }

            }
            .addOnFailureListener {
                Log.d("aaaaaaaaa", "data save error", it)
            }
    }

    fun uploadImage(docId: String) {
        val storage = MyApplication.storage
        val storageRef = storage.reference
        val imgRef = storageRef.child("petstarimages/${docId}.jpg")
        Log.d("aaaa", "${imgRef}")
        val file = Uri.fromFile(File(filePath))
        imgRef.putFile(file) //파일 업로드
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "업로드 완료!!", Toast.LENGTH_SHORT).show()
                //등록 완료 후 뒤로가기
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Log.d("aaa", "file save error", it)
            }
    }
}


fun dateToString(date: Date): String {
    val format = SimpleDateFormat("yyyy-MM-dd")
    return format.format(date)
}



