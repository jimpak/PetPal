package org.bitc.petpalapp.ui

import android.app.Activity
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyprofileBinding.inflate(inflater, container, false)

        val requestLancher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode === Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    handleImageSelection(uri)
                }
            }
        }

        val docRef = MyApplication.db.collection("users")
            .whereEqualTo("email", MyApplication.email)

        lifecycleScope.launch {
            try {
                val result = docRef.get().await()

                if (result.documents.isNotEmpty()) {
                    val item = result.documents[0].toObject(UserInfo::class.java)
                    item?.docId = result.documents[0].id
                    docId = item?.docId.toString()

                    binding.editNickname.setText(item?.nickname)
                    binding.editName.setText(item?.username)
                    binding.editAddress.setText(item?.address)
                    binding.editPhone.setText(item?.phone)
                    binding.editPassword.setText(item?.pass)

                    val imgRef = MyApplication.storage.reference.child("images/${item?.docId}.jpg")

                    Glide.with(requireContext())
                        .load(imgRef)
                        .into(binding.profileImageView)
                }

            } catch (e: Exception) {
                Log.d("bbb", "오류", e)
            }
        }

        binding.changeProfileImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            requestLancher.launch(intent)
        }

        binding.btnSave.setOnClickListener {
            val nickname = binding.editNickname.text.toString()
            val username = binding.editName.text.toString()
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

            if (docId.isNotEmpty()) {
                MyApplication.db.collection("users").document(docId)
                    .set(data)
                    .addOnSuccessListener {
                        uploadImage(docId)
                        parentFragmentManager.popBackStack()
                    }
                    .addOnFailureListener {
                        Log.d("pgm", "data save error", it)
                    }
            } else {
                MyApplication.db.collection("users")
                    .add(data)
                    .addOnSuccessListener {
                        uploadImage(it.id)
                        Toast.makeText(requireContext(), "저장 성공", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                    .addOnFailureListener {
                        Log.d("pgm", "data save error", it)
                    }


            }
        }

        return binding.root
    }

    private fun handleImageSelection(uri: Uri) {
        Glide.with(requireActivity())
            .load(uri)
            .apply(RequestOptions().override(250, 200))
            .centerCrop()
            .into(binding.profileImageView)

        val cursor = requireActivity().contentResolver.query(
            uri,
            arrayOf(MediaStore.Images.Media.DATA),
            null, null, null
        )
        cursor?.moveToFirst()?.let {
            filePath = cursor.getString(0)
        }
    }

    private fun uploadImage(docId: String) {
        GlobalScope.launch {
            try {
                val storageRef = MyApplication.storage.reference
                val imgRef = storageRef.child("images/$docId.jpg")
                val file = Uri.fromFile(File(filePath))

                imgRef.putFile(file).await()

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "저장 성공", Toast.LENGTH_SHORT).show()

                }
            } catch (e: Exception) {
                Log.d("kkang", "file save error", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}