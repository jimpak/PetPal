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
import org.bitc.petpalapp.databinding.FragmentPetDetailBinding
import org.bitc.petpalapp.databinding.FragmentPetRegisterBinding
import org.bitc.petpalapp.ui.mypet.util.ItemData
import org.bitc.petpalapp.ui.mypet.util.dateToString
import java.io.File
import java.util.Date

class PetDetailFragment : Fragment() {
    private var _binding: FragmentPetDetailBinding? = null
    lateinit var filePath: String
    lateinit var docId : String
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentPetDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // 프래그먼트로 전달된 데이터를 Bundle에서 꺼내기
        docId = arguments?.getString("docId").toString()
        // docId를 사용하여 필요한 작업 수행
        if (docId != null) {
            val docRef = MyApplication.db.collection("pets").document(docId)
            docRef.get()
                .addOnSuccessListener {documentSnapshot ->
                    val item = documentSnapshot.toObject(ItemData::class.java)
                    binding.editName2.setText(item?.petname)
                    binding.editType2.setText(item?.weight)
                    binding.editBir2.setText(item?.birthday)
                    binding.editType2.setText(item?.type)
                    binding.editHos2.setText(item?.hospital)
                    if(item?.gender =="여아"){
                        binding.ckGen12.isChecked = true
                        binding.ckGen22.isChecked = false
                    }else{binding.ckGen12.isChecked = false
                        binding.ckGen22.isChecked = true
                    }

                    if(item?.neutered =="했어요"){
                        binding.ckNeu12.isChecked = true
                        binding.ckNeu22.isChecked = false
                    }else{
                        binding.ckNeu12.isChecked = false
                        binding.ckNeu22.isChecked = true}


                    val imgRef = MyApplication.storage.reference.child("images/${docId}.jpg")
                    imgRef.downloadUrl.addOnCompleteListener{ task ->
                        if(task.isSuccessful){
                            Glide.with(requireContext())
                                .load(task.result)
                                .into(binding.imgresult2)
                        }
                    }
                }
                .addOnFailureListener {
                        Exception->
                    Log.d("bbb", "오류", Exception)
                }

            Log.d("bbb", "Received docId: $docId")
        } else {
            Log.e("bbb", "No docId received.")
        }

        val requestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        {
            Glide
                .with(requireActivity())
                .load(it.data?.data)
                .apply(RequestOptions().override(150, 150))
                .centerCrop()
                .into(binding.imgresult2)


            val cursor =  requireActivity().contentResolver.query(it.data?.data as Uri,
                arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null);
            cursor?.moveToFirst().let {
                filePath=cursor?.getString(0) as String
            }
            Log.d("aaa", "filePath : $filePath")

        }

        binding.changeProfileImageButton2.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            requestLauncher.launch(intent)
        }


        // db에 재등록
        binding.btnModify.setOnClickListener {
            updateStore(docId)
            //등록 완료 후 뒤로가기
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
            Toast.makeText(requireContext(), "수정 완료!!", Toast.LENGTH_SHORT).show()
        }


        binding.btnDelete.setOnClickListener {
            deleteDocument(docId)
            //삭제 완료 후 뒤로가기
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
            Toast.makeText(requireContext(), "삭제 완료!!", Toast.LENGTH_SHORT).show()
        }

    }





    private fun updateStore(docId: String){

        // Firebase에 저장할 데이터 모델 객체 생성
        val pet = ItemData(
            email = MyApplication.email,
            date = dateToString(Date()),
            petname = binding.editName2.text.toString(),
            gender = if (binding.ckGen12.isChecked) "여아" else "남아",
            type = binding.editType2.text.toString(),
            birthday = binding.editBir2.text.toString(),
            weight = binding.editKg2.text.toString(),
            neutered = if (binding.ckNeu12.isChecked) "했어요" else "안했어요",
            hospital = binding.editHos2.text.toString()
        )

        // Firebase에 저장
        MyApplication.db.collection("pets").document(docId)
            .set(pet)
            .addOnSuccessListener {
                Log.d("Firestore", "Document updated with ID: $docId")
                // 이미지 업데이트
                uploadImage(docId)
            }
            .addOnFailureListener {
                Log.d("kkang", "data update error", it)
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
                Log.d("aaaa", "이미지 수정 성공")

            }
            .addOnFailureListener{
                Log.d("aaaa", "file save error", it)
            }

    }

    private fun deleteDocument(docId: String) {
        // Firebase에서 문서 삭제
        MyApplication.db.collection("pets").document(docId)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Document deleted with ID: $docId")
                // 문서 삭제 후 이미지도 삭제
                deleteImage(docId)
            }
            .addOnFailureListener {
                Log.d("kkang", "Document deletion error", it)
            }
    }

    private fun deleteImage(docId: String) {
        // 이미지 삭제를 위한 코드
        val storage = MyApplication.storage
        val storageRef = storage.reference
        val imgRef = storageRef.child("images/$docId.jpg")

        // 이미지 삭제
        imgRef.delete()
            .addOnSuccessListener {
                Log.d("aaaa", "Image file deleted successfully")
            }
            .addOnFailureListener { exception ->
                Log.d("aaaa", "Image file deletion error", exception)
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}