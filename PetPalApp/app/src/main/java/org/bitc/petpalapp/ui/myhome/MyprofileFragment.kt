package org.bitc.petpalapp.ui.myhome

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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentMyprofileBinding
import org.bitc.petpalapp.model.UserInfo
import java.io.File


class MyprofileFragment : Fragment() {
    lateinit var filePath: String
    lateinit var docId: String
    lateinit var imagePath: String
    private var _binding: FragmentMyprofileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMyprofileBinding.inflate(inflater, container, false)

        val requestLancher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode === android.app.Activity.RESULT_OK) {
                Glide
                    .with(requireActivity())
                    .load(it.data?.data)
                    .apply(RequestOptions().override(250, 200))
                    .centerCrop()
                    .into(binding.profileImageView)

                val cursor = requireActivity().contentResolver.query(
                    it.data?.data as Uri,
                    arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null
                );
                cursor?.moveToFirst().let {
                    filePath = cursor?.getString(0) as String
                }
            }
        }

        // 마이프로필 등록되어있다면 정보 가져오기
        val docRef = MyApplication.db.collection("users")

        docRef
            .whereEqualTo("email", MyApplication.email)
            .get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<UserInfo>()
                for (document in result) {
                    val item = document.toObject(UserInfo::class.java)
                    item.docId = document.id
                    docId = item.docId.toString()
                    itemList.add(item)

                    binding.editNickname.setText(item.nickname)
                    binding.editName.setText(item.username)
                    binding.editAddress.setText(item.address)
                    binding.editPhone.setText(item.phone)
                   binding.editselfPr.setText(item.mypr)

                    val imgRef =
                        MyApplication.storage.reference.child("userimages/${item.docId}.jpg")

                    imgRef.downloadUrl.addOnCompleteListener { task ->
                        imagePath = task.result.toString()
                        if (task.isSuccessful) {
                            Glide
                                .with(requireContext())
                                .load(task.result)
                                .into(binding.profileImageView)
                        }
                    }
                }

                // 여기서 docId를 사용하여 uploadImage 호출
                if (::docId.isInitialized) {
                    binding.changeProfileImageButton.setOnClickListener {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*"
                        )
                        requestLancher.launch(intent)
                    }

                    binding.btnSave.setOnClickListener {
                        val nickname = binding.editNickname.text.toString()
                        val username = binding.editName.text.toString()
                        val mypr = binding.editselfPr.text.toString()
                        val phone = binding.editPhone.text.toString()
                        val address = binding.editAddress.text.toString()

                        val data = mapOf(
                            "email" to MyApplication.email,
                            "nickname" to nickname,
                            "username" to username,
                            "mypr" to mypr,
                            "phone" to phone,
                            "address" to address
                        )

                        MyApplication.db.collection("users").document(docId)
                            .update(data)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "수정 완료!", Toast.LENGTH_SHORT)
                                    .show()
                                uploadImage(docId)

                                val bundle = Bundle().apply {
                                    putString("imagePath", filePath)
                                }

                                findNavController().navigate(R.id.action_Myprofile_to_myhome, bundle)
                            }
                            .addOnFailureListener {
                                Log.d("aaa", "data save error", it)
                                Toast.makeText(requireContext(), "수정 실패ㅠㅠ", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                } else {
                    // docId가 없는 경우에 대한 처리
                    binding.changeProfileImageButton.setOnClickListener {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*"
                        )
                        requestLancher.launch(intent)
                    }

                    binding.btnSave.setOnClickListener {
                        val nickname = binding.editNickname.text.toString()
                        val username = binding.editName.text.toString()
                        val mypr = binding.editselfPr.text.toString()
                        val phone = binding.editPhone.text.toString()
                        val address = binding.editAddress.text.toString()

                        val data = mapOf(
                            "email" to MyApplication.email,
                            "nickname" to nickname,
                            "username" to username,
                            "mypr" to mypr,
                            "phone" to phone,
                            "address" to address,
                        )

                        MyApplication.db.collection("users")
                            .add(data)
                            .addOnSuccessListener {
                                uploadImage(it.id)
                                Toast.makeText(requireContext(), "저장 완료!", Toast.LENGTH_SHORT)
                                    .show()
                                val bundle = Bundle().apply {
                                    putString("imagePath", filePath)
                                }

                                findNavController().navigate(R.id.action_Myprofile_to_myhome, bundle)
                            }
                            .addOnFailureListener {
                                Log.d("aaa", "data save error", it)
                                Toast.makeText(requireContext(), "저장 실패ㅠㅠ", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                }
            }
            .addOnFailureListener { Exception ->
                Log.d("bbb", "오류", Exception)
            }

        return binding.root
    }

    fun uploadImage(docId: String) {
        val storage = MyApplication.storage
        val storageRef = storage.reference
        val imgRef = storageRef.child("userimages/${docId}.jpg")
        val file = Uri.fromFile(File(filePath))
        imgRef.putFile(file) // 파일업로드
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "save ok..", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d("kkang", "file save error", it)
            }
    }
}