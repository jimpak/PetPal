package org.bitc.petpalapp.ui.mypet

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION_CODES.R
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.renderscript.ScriptGroup.Binding
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.flow.callbackFlow
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentPetDetailBinding
import org.bitc.petpalapp.databinding.MypetItemBinding
import org.checkerframework.checker.index.qual.LengthOf
import java.io.File

class PetDetailFragment : Fragment() {
    lateinit var _binding: FragmentPetDetailBinding
    lateinit var filePath:String

    lateinit var petId: String
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = PetDetailFragment()
    }

    private lateinit var viewModel: PetDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPetDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val requestLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    data?.data?.let { uri ->
                        filePath = getRealPathFromURI(uri)
                        Log.d("aaa", "filePath : $filePath")
                        Glide
                            .with(requireActivity())
                            .load(uri)
                            .apply(RequestOptions().override(150, 150))
                            .centerCrop()
                            .into(binding.imageView)
                    }
                }
            }

        // 프래그먼트로 전달된 데이터를 Bundle에서 꺼내기
        petId = arguments?.getString("petId").toString()
        // docId를 사용하여 필요한 작업 수행
        if (petId != null) {
            val docRef = MyApplication.db.collection("pets").document(petId)
            docRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    val item = documentSnapshot.toObject(PetData::class.java)

                    binding.editTextText.setText(item?.petname)
                    binding.editType.setText(item?.type)
                    binding.editBirth.setText(item?.birthday)
                    binding.editWeight.setText(item?.weight)
                    binding.editHospital.setText(item?.hospital)

                    if (item?.gender == "여아") {
                        binding.genRadioButton.isChecked = false
                        binding.genRadioButton2.isChecked = true
                    } else {
                        binding.genRadioButton.isChecked = true
                        binding.genRadioButton2.isChecked = false
                    }

                    if (item?.neutered == "안했어요") {
                        binding.neuRadioButton.isChecked = false
                        binding.neuRadioButton2.isChecked = true
                    } else {
                        binding.neuRadioButton.isChecked = true
                        binding.neuRadioButton2.isChecked = false
                    }

                    Log.d("aaaaa","1234 ${item?.neutered}")
                    Log.d("aaaaa","1234 ${item?.gender}")

                    val imgRef = MyApplication.storage.reference.child("images/${petId}.jpg")

                    imgRef.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            Glide
                                .with(requireContext())
                                .load(task.result)
                                .into(binding.imageView)
                        }
                    }

                    binding.btnChangeImage.setOnClickListener {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*"
                        )
                        requestLauncher.launch(intent)
                    }

                    binding.btnModify.setOnClickListener {
                        val pet = mapOf(
                            "email" to MyApplication.email,
                            "petname" to binding.editTextText.text.toString(),
                            "gender" to if (binding.genRadioButton.isChecked) "남아" else "여아",
                            "type" to binding.editType.text.toString(),
                            "birthday" to binding.editBirth.text.toString(),
                            "weight" to binding.editWeight.text.toString(),
                            "neutered" to if (binding.genRadioButton.isChecked) "했어요" else "안했어요",
                            "hospital" to binding.editHospital.text.toString()
                        )

                        MyApplication.db.collection("pets")
                            .document(petId)
                            .set(pet)
                            .addOnSuccessListener {
                                Log.d("bbbbbb", "modify : $petId")
                                uploadImage(petId)

                                val bundle = Bundle().apply {
                                    putString("imagePath", filePath)
                                }
                                findNavController().navigate(org.bitc.petpalapp.R.id.action_Mypetdetail_to_mypet, bundle)
                            }
                            .addOnFailureListener {
                                Log.d("aaaaaaaaa", "data save error", it)
                            }
                        val fragmentManager = requireActivity().supportFragmentManager

                        Toast.makeText(requireContext(),"수정 완료!",Toast.LENGTH_SHORT).show()
                    }

                    binding.btnDelete.setOnClickListener {
                        deleteDate(petId)

                        val fragmentManager = requireActivity().supportFragmentManager

                        fragmentManager.popBackStack()

                        Toast.makeText(requireContext(),"삭제 완료!",Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {Exception ->
                    Log.d("bbb", "오류", Exception)
                }

            Log.d("aaaaaa","Received petId: $petId")
        } else {
            Log.d("aaaaaa","No Received petId")
        }

    }

    fun modifyStor() {
        val pet = mapOf(
            "email" to MyApplication.email,
            "petname" to binding.editTextText.text.toString(),
            "gender" to if (binding.genRadioButton.isChecked) "남아" else "여아",
            "type" to binding.editType.text.toString(),
            "birthday" to binding.editBirth.text.toString(),
            "weight" to binding.editWeight.text.toString(),
            "neutered" to if (binding.genRadioButton.isChecked) "했어요" else "안했어요",
            "hospital" to binding.editHospital.text.toString()
        )

        MyApplication.db.collection("pets")
            .document(petId)
            .set(pet)
            .addOnSuccessListener {
                Log.d("bbbbbb", "modify : $petId")
                uploadImage(petId)
            }
            .addOnFailureListener {
                Log.d("aaaaaaaaa", "data save error", it)
            }
    }

    fun uploadImage(petId:String){
        val storage = MyApplication.storage
        val storageRef = storage.reference
        val imgRef = storageRef.child("images/${petId}.jpg")
        Log.d("aaaa","${imgRef}")

        val file= Uri.fromFile(File(filePath))
        imgRef.putFile(file) //파일 업로드
            .addOnSuccessListener {
                Log.d("aaaaaa","이미지 수집 성공")
                setFragmentResult("requestKey", bundleOf("petImageUpdated" to true))
            }
            .addOnFailureListener{
                Log.d("kkang","file save error", it)
            }
    }

    fun deleteDate(petId: String){
        MyApplication.db.collection("pets").document(petId)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Document deleted with ID: $petId")
                // 문서 삭제 후 이미지도 삭제
                deleteImage(petId)
            }
            .addOnFailureListener {
                Log.d("kkang", "Document deletion error", it)
            }
    }

    fun deleteImage(petId: String){

        val storage = MyApplication.storage
        val storageRef = storage.reference
        val imgRef = storageRef.child("images/${petId}.jpg")
        Log.d("aaaaaa","${imgRef}")

        imgRef.delete()
            .addOnSuccessListener {
                Log.d("aaaaaa","delete succcess!")
            }
            .addOnFailureListener {exception ->
                Log.d("aaaaaaa","delete fail..",exception)
            }
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireActivity().contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(columnIndex)
        }
        return ""
    }

}