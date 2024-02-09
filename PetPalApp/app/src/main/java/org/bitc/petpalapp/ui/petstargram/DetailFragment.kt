package org.bitc.petpalapp.ui.petstargram

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentPetstargramDetailBinding


class DetailFragment : Fragment() {
    lateinit var _binding: FragmentPetstargramDetailBinding
    lateinit var docId:String

    private val binding get()=_binding!!
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

        if (docId != null){
            val instraRef=MyApplication.db.collection("news").document(docId)
            instraRef.get()
                .addOnSuccessListener {documentSnapsshot->
                    val item = documentSnapsshot.toObject(itemPetstar::class.java)

                    binding.detailProfile.setText(item?.email)
//                    item?.faviruteCount?.let { binding.detailLikecount.setText(it) }
                    binding.detailContent.setText(item?.content)

                    val imgRef=MyApplication.storage.reference.child("images/${docId}.jpg")

                    imgRef.downloadUrl.addOnCompleteListener { task->
                        if (task.isSuccessful){
                            Glide
                                .with(requireContext())
                                .load(task.result)
                                .into(binding.petstargramDetail)
                        }
                    }
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
    }

}
