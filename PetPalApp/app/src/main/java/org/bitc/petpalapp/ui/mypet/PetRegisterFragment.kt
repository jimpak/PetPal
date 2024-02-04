package org.bitc.petpalapp.ui.mypet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.MyApplication.Companion.storage
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentPetRegisterBinding
import org.bitc.petpalapp.ui.mypet.util.ImageAdapter
import org.bitc.petpalapp.ui.mypet.util.ImageModel
import org.bitc.petpalapp.ui.mypet.util.ItemData
import org.bitc.petpalapp.ui.mypet.util.dateToString
import org.bitc.petpalapp.ui.mypet.util.myCheckPermission
import java.io.File
import java.util.Date
import java.util.UUID


class PetRegisterFragment : Fragment() {
    private var _binding: FragmentPetRegisterBinding? = null
    lateinit var filePath: String
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentPetRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val requestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        {
                Glide
                    .with(requireActivity())
                    .load(it.data?.data)
                    .apply(RequestOptions().override(150, 150))
                    .centerCrop()
                    .into(binding.imgresult)


                val cursor =  requireActivity().contentResolver.query(it.data?.data as Uri,
                    arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null);
                cursor?.moveToFirst().let {
                    filePath=cursor?.getString(0) as String
                }
                Log.d("aaa", "filePath : $filePath")

        }

        binding.changeProfileImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            requestLauncher.launch(intent)
        }


        // db에 등록
        binding.btnRegister2.setOnClickListener {
            saveStore()
            //등록 완료 후 뒤로가기
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

    }




    private fun saveStore() {

        // Firebase에 저장할 데이터 모델 객체 생성
        val pet = ItemData(
            email = MyApplication.email,
            date = dateToString(Date()),
            petname = binding.editName.text.toString(),
            gender = if (binding.ckGen1.isChecked) "여아" else "남아",
            type = binding.editType.text.toString(),
            birthday = binding.editBir.text.toString(),
            weight = binding.editKg.text.toString(),
            neutered = if (binding.ckNeu1.isChecked) "했어요" else "안했어요",
            hospital = binding.editHos.text.toString()
        )

        // Firebase에 저장
        MyApplication.db.collection("pets").add(pet)
            .addOnSuccessListener {documentReference ->
                val docId = documentReference.id
                Log.d("Firestore", "Document added with ID: $docId")
                uploadImage(docId)
            }
            .addOnFailureListener{
                Log.d("kkang", "data save error", it)
            }

    }
    private fun uploadImage(docId: String){
        //add............................
        val storage = MyApplication.storage
        val storageRef = storage.reference
        val imgRef = storageRef.child("images/${docId}.jpg")

        val file = Uri.fromFile(File(filePath))
        imgRef.putFile(file)
            .addOnSuccessListener {


            }
            .addOnFailureListener{
                Log.d("aaaa", "file save error", it)
            }

    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}