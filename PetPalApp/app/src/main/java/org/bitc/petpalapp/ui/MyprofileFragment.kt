package org.bitc.petpalapp.ui

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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentMyhomeBinding
import org.bitc.petpalapp.databinding.FragmentMyprofileBinding
import org.bitc.petpalapp.model.UserInfo
import org.bitc.petpalapp.ui.myhome.MyhomeViewModel
import org.bitc.petpalapp.ui.mypet.PetData
import java.io.File
import java.util.Date


class MyprofileFragment : Fragment() {

    lateinit var filePath: String
    lateinit var docId: String
    private var _binding: FragmentMyprofileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


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


        //마이프로필 등록되어있다면 정보 가져오기
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
                    binding.editPassword.setText(item.pass)

                    val imgRef =
                        MyApplication.storage.reference.child("images/${item.docId}.jpg")

                    imgRef.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Glide
                                .with(requireContext())
                                .load(task.result)
                                .into(binding.profileImageView)
                        }
                    }
                }


                binding.changeProfileImageButton.setOnClickListener {

                    val intent = Intent(Intent.ACTION_PICK)
                    intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*"
                    )
                    requestLancher.launch(intent)
                }

                binding.btnSave.setOnClickListener {
                    val nickname = binding.editName.text.toString()
                    val username = binding.editNickname.text.toString()
                    val pass = binding.editPassword.text.toString()
                    val phone = binding.editPhone.text.toString()
                    val address = binding.editAddress.text.toString()

                    val data = mapOf(
                        "email" to MyApplication.email,
                        "nickname" to nickname,
                        "usersname" to username,
                        "pass" to pass,
                        "phone" to phone,
                        "address" to address,
                    )

                    if (docId != null) {
                        MyApplication.db.collection("users").document(docId)
                            .set(data)
                            .addOnSuccessListener {
                                uploadImage(docId)
                            }
                            .addOnFailureListener {
                                Log.d("pgm", "data save error", it)
                            }

                    } else {
                        MyApplication.db.collection("users")
                            .add(data)
                            .addOnSuccessListener {
                                uploadImage(it.id)
                            }
                            .addOnFailureListener {
                                Log.d("pgm", "data save error", it)
                            }


                        findNavController().navigate(R.id.action_Myprofile_to_myhome)

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
        val imgRef = storageRef.child("images/${docId}.jpg")
        val file = Uri.fromFile(File(filePath))
        imgRef.putFile(file) //파일업로드
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "save ok..", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d("kkang", "file save error", it)
            }
    }


}
