package org.bitc.petpalapp.ui.mypet

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Binder
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
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentPetRegisterBinding
import java.io.File
import java.util.Date

class PetRegisterFragment : Fragment() {
    lateinit var binding:FragmentPetRegisterBinding
    lateinit var filePath:String

    companion object {
        fun newInstance() = PetRegisterFragment()
    }

    private lateinit var viewModel: PetRegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPetRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val requestLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                Glide
                    .with(requireActivity())
                    .load(it.data?.data)
                    .apply(RequestOptions().override(150, 150))
                    .centerCrop()
                    .into(binding.profileImageView)

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

        binding.changeProfileImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            requestLauncher.launch(intent)
        }

        binding.btnPetregister.setOnClickListener {
            saveStor()
            //등록 완료 후 뒤로가기
            //val fragmentManager = requireActivity().supportFragmentManager
            //fragmentManager.popBackStack()

            findNavController().navigate(R.id.action_MypetRegister_to_mypet)
        }
    }

    fun saveStor(){
        val pet= mapOf(
            "email" to MyApplication.email,
            "petname" to binding.editName.text.toString(),
            "gender" to if(binding.ckGen1.isChecked) "남아" else "여아",
            "type" to binding.editType.text.toString(),
            "birthday" to binding.editBir.text.toString(),
            "weight" to binding.editKg.text.toString(),
            "neutered" to if(binding.ckNeu1.isChecked) "했어요" else "안했어요",
            "hospital" to binding.editHos.text.toString()
        )
        Log.d("aaaaaa","0000${binding.ckNeu1}")

        MyApplication.db.collection("pets")
            .add(pet)
            .addOnSuccessListener{
                uploadImage(it.id)
            }
            .addOnFailureListener{
                Log.d("aaaaaaaaa","data save error",it)
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
                setFragmentResult("requestKey", bundleOf("petImageRegister" to true))
            }
            .addOnFailureListener{
                Log.d("kkang","file save error", it)
            }
    }
}