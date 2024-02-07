package org.bitc.petpalapp.ui.petstargram

import android.annotation.SuppressLint
import android.app.Activity
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
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.ktx.Firebase
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentWriteBinding
import java.io.File
import java.util.Date
import com.google.firebase.storage.ktx.storage
import org.bitc.petpalapp.util.dateToString

class WriteFragment : Fragment() {

    private val REQUEST_CODE_PICK_IMAGE = 100
    val _binding: FragmentWriteBinding? =null
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private var selectedImageUri: Uri? = null

    val binding get() = _binding!!
    companion object {
        fun newInstance() = WriteFragment()
    }

    private lateinit var viewModel: WriteViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_write,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //갤러리 열기
        val buttongallery=view.findViewById<Button>(R.id.galleyOpen)
        buttongallery.setOnClickListener {
            openGallery()
        }

        val buttonUpload=view.findViewById<Button>(R.id.petStarUpload)
        buttonUpload.setOnClickListener {
            if (binding.petstarImg!==null && binding.etPetstar!==null) {
                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.remove(this)
                    ?.commit()
            }else{
                Log.d("aaaaaaa","데이터가 모두 입력되지 않음")
            }
        }


    }



    private fun openGallery() {
        val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type="image/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }
//
//    //업로드 눌렸을시 실행되는
//    private fun uploadPetstar() {
//        if (R.id.petstarImg !==null && R.id.etPetstar !==null){
//            saveStor()
//        }else{
//            Log.d("errrrror","데이터가 모두 입력되지 않음")
//        }
//    }
//
//    private fun saveStor() {
//        val data= mapOf(
//            "email" to MyApplication.email,
//            "content" to binding.etPetstar.text.toString(),
//            "data" to dateToString(Date())
//        )
////        MyApplication.db.collection("news")
////            .add(data)
////            .addOnSuccessListener {
////                uploadImage(it.id)
////            }
////            .addOnFailureListener {
////              Log.d("Errrrrrrrrror","data save error",it)
////            }
//    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            selectedImageUri?.let { uri ->
                uploadImage(uri)

            }
        }
    }

    private fun uploadImage(imageuri: Uri) {
        val fileName="images/${System.currentTimeMillis()}.jpg"
        val imgRef=storageRef.child(fileName)


        imgRef.putFile(imageuri)
            .addOnSuccessListener {
                Toast.makeText(requireContext(),"upload success",Toast.LENGTH_SHORT).show()
                onDestroy()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),"upload fail",Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WriteViewModel::class.java)
        // TODO: Use the ViewModel
    }





}

