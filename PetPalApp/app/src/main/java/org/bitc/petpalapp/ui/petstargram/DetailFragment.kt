package org.bitc.petpalapp.ui.petstargram

import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentPetstargramDetailBinding
import org.bitc.petpalapp.model.PetstargamItem


class DetailFragment : Fragment() {
    lateinit var _binding: FragmentPetstargramDetailBinding
    lateinit var docId:String

    private val binding get()=_binding!!

    lateinit var filepath:String
    companion object {
        fun newInstance() = DetailFragment()
    }

    private lateinit var viewModel: DetailViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentPetstargramDetailBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        docId=arguments?.getString("docId").toString()
        Log.d("여기는","${docId}")

        if (docId != null){
            val docRef=MyApplication.db.collection("petstars").document(docId)

            docRef.get()
                .addOnSuccessListener {documentSnapsshot->
                    val item = documentSnapsshot.toObject(PetstargamItem::class.java)

                    binding.detailProfile.setText(item?.email)
                    Log.d("데이터 들어왔니","${item?.email}")
//                    binding.detailLikecount.setText(item?.goodCount!!)
                    binding.detailContent.setText(item?.content)

                    val imgRef=MyApplication.storage.reference.child("petstarimages/${docId}.jpg")

                    imgRef.downloadUrl.addOnCompleteListener { task->
                        if (task.isSuccessful){
                            Glide
                                .with(requireContext())
                                .load(task.result)
                                .into(binding.detailPetstarImg )
                        }
                    }
                }
                .addOnFailureListener { Exception->
                    Log.d("errrorrrrr","잘못된 다시!!")
                }
        }else{
            Log.d("eeeeeeeeeeroro","다시다시")
        }
        val requestLauncher=
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                Glide
                    .with(requireContext())
                    .load(it.data?.data)
                    .apply(RequestOptions().override(300,300))
                    .centerCrop()
                    .into(binding.detailPetstarImg)

                val cursor=requireActivity().contentResolver.query(
                    it.data?.data as Uri, arrayOf<String>(
                        MediaStore.Images.Media.DATA
                    ),null,null,null
                )
                cursor?.moveToFirst().let {
                    filepath=cursor?.getString(0) as String
                }
                Log.d("확익","filePath:$filepath")
            }

    }

}
