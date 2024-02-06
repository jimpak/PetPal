package org.bitc.petpalapp.ui.petstargram

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.contentValuesOf
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentWriteBinding
//import org.bitc.petpalapp.util.dateToString
import java.io.File
import java.util.Date

class WriteFragment : Fragment() {
    var _binding:FragmentWriteBinding? =null
    var filePath:String?=null
    var storage:FirebaseStorage? = null

    val binding get() = _binding!!

    companion object {
        fun newInstance() = WriteFragment()
    }

    private lateinit var viewModel: WriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding= FragmentWriteBinding.inflate(inflater, container,false)
        val root:View = binding.root

       return inflater.inflate(R.layout.fragment_write, container, false)
    }

    val requestLancher=registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){

        if(it.resultCode===android.app.Activity.RESULT_OK){
//            이미지 한개 넣기 완성할 경우 여러개 넣기도 해보기
            Glide
                .with(requireContext())
                .load(it.data?.data)//리스트로 만든 어댑터로 만들어서 넣기
                .apply(RequestOptions.overrideOf(250,200))
                .centerCrop()
                .into(binding.petstarImg)//이미지뷰1개만

            val cursor=requireContext().contentResolver.query(
                it.data?.data as Uri,
                arrayOf(MediaStore.Images.Media.DATA), null,null,null)
            cursor?.moveToFirst().let{
                filePath=cursor?.getString(0) as String
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.galleyOpen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            requestLancher.launch(intent)
        }
        binding.petStarUpload.setOnClickListener {
          if (binding.petstarImg.drawable != null&&binding.etPetstar.text.isNotEmpty()){
              saveStor()
          }else{
              Log.d("error","데이터가 입력되지 않음")
          }

        }
        super.onViewCreated(view, savedInstanceState)
    }
    fun saveStor(){
        val data= mapOf(
            "email" to MyApplication.email,
            "content" to _binding!!.etPetstar.text.toString(),
//            "date" to dateToString(Date())
        )
        MyApplication.db.collection("news")
            .add(data)
            .addOnSuccessListener {
                uploadImage(it.id)
            }
            .addOnFailureListener{
                Log.d("Fail","data save error", it)
            }
    }

    private fun uploadImage(id: String) {
        val storage=MyApplication.storage
        val storageRef=storage.reference
        val imgRef=storageRef.child("image/${id}.jpg")
        val file=Uri.fromFile(File(filePath))
        imgRef.putFile(file)
            .addOnSuccessListener {
                Toast.makeText(this.requireContext(),"save Completion",Toast.LENGTH_SHORT).show()
                onDestroy()
            }
            .addOnFailureListener{
                Log.d("errorrrr","file Completion error",it)
            }
    }




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WriteViewModel::class.java)
        // TODO: Use the ViewModel
    }

}