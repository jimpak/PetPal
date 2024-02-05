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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentMyprofileBinding
import java.io.File
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyprofileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyprofileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var filePath:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding=FragmentMyprofileBinding.inflate(inflater,container, false)

        val requestLancher=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode===android.app.Activity.RESULT_OK){
                Glide
                    .with(requireActivity())
                    .load(it.data?.data)
                    .apply(RequestOptions().override(250,200))
                    .centerCrop()
                    .into(binding.profileImageView)

                val cursor = contentResolver.query(it.data?.data as Uri,
                    arrayOf<String>(MediaStore.Images.Media.DATA), null, null,null);
                cursor?.moveToFirst().let{
                    filePath=cursor?.getString(0) as String
                }
            }
        }

        binding.changeProfileImageButton.setOnClickListener{

            val intent= Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            requestLancher.launch(intent)
        }

        binding.btnSave.setOnClickListener{
            val nickname=binding.editName.text.toString()
            val username=binding.editNickname.text.toString()
            val pass=binding.editPassword.text.toString()
            val email=binding.editEmail.text.toString()
            val phone=binding.editPhone.text.toString()
            val address=binding.editAddress.text.toString()
            val usecount=binding.editUsecount.text.toString()
            val petcount=binding.editPetcount.text.toString()
            val boardcount=binding.editBoardcount.text.toString()


            val data = mapOf(
                "email" to MyApplication.email,
                "nickname" to nickname,
                "usersname" to username,
                "pass" to pass,
                "phone" to phone,
                "address" to address,
                "usecount" to usecount,
                "petcout" to petcount,
                "boardcount" to boardcount
            )
            MyApplication.db.collection("users")
                .add(data)
                .addOnSuccessListener {
                    uploadImage(it.id)
                }
                .addOnFailureListener{
                    Log.d("pgm","data save error",it)
                }

        }
        return binding.root
    }


    fun uploadImage(docId:String){
        //add............................
        val storage = MyApplication.storage
        val storageRef = storage.reference
        val imgRef=storageRef.child("images/${docId}.jpg")
        val file= Uri.fromFile(File(filePath))
        imgRef.putFile(file) //파일업로드
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "save ok..", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Log.d("kkang", "file save error", it)
            }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyprofileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyprofileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}