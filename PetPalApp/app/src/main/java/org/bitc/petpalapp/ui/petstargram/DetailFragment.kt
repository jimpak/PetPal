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
import com.google.firebase.auth.FirebaseAuth
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentPetstargramDetailBinding
import org.bitc.petpalapp.model.PetstargamItem
import org.checkerframework.checker.units.qual.Current
import java.util.Date


class DetailFragment : Fragment() {
    lateinit var _binding: FragmentPetstargramDetailBinding
    lateinit var docId:String


    private val binding get()=_binding!!

    lateinit var filepath:String
    private var isLiked=false
   private var likeCount = 0
    private var gooduser: String? = null

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser


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
            val docRef=MyApplication.db.collection("petstars").document(docId)

            docRef.get()
                .addOnSuccessListener {documentSnapsshot->
                    val item = documentSnapsshot.toObject(PetstargamItem::class.java)

                    gooduser=item?.gooduser

                    binding.detailProfile.setText(item?.email)
                    binding.detailContent.setText(item?.content)

                    val imgRef=MyApplication.storage.reference.child("petstarimages/${docId}.jpg")
                    //프로필 이미지

                    //인스타 디테일 이미지
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

            binding.detailLikeBin.setOnClickListener {
                //클릭시 좋아요 수 증가, 하트 채우기
                isLiked= !isLiked
                if (isLiked) {
                    binding.detailLikeBin.setImageResource(R.drawable.ic_favorite)
                    likeCount++
                }else if(!isLiked){
                    binding.detailLikeBin.setImageResource(R.drawable.ic_favorite_border)
                    likeCount--

                }
                binding.detailLikecount.text = "Like"+likeCount.toString()
                Log.d("증가했니????","${likeCount}")


            }

            binding.backPetstar.setOnClickListener{
                likesave()
                val fragmentManager = requireActivity().supportFragmentManager

                fragmentManager.popBackStack()

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




    private fun likesave() {
        //좋아요 firebase저장
        val like = mapOf(
            "email" to MyApplication.email,
            "content" to binding.detailContent.text,
            "data" to dateToString(Date()),
            "likeCount" to binding.detailLikecount.text
        )

        MyApplication.db.collection("petstars")
            .document(docId)
            .set(like)
            .addOnSuccessListener {
                Log.d("들어갔니", "likeCount : $likeCount")
                heartcolor()
            }
            .addOnFailureListener {
                Log.d("실패했니", "likeCount : $docId")
            }
    }

    private fun heartcolor() {
        if (currentUser != null){
                Log.d("user","${currentUser?.email}")
            // 예를 들어 'users' 컬렉션에서 현재 사용자의 문서를 가져올 수 있습니다.
            MyApplication.db.collection("users").document(docId)
                .get()
                .addOnSuccessListener { document ->
                    Log.d("이건 id???","${currentUser.uid}")
                    Log.d("이건 docid??","${docId}")
                    if (document != null) {
                        val isLiked = document.getBoolean("isLiked") ?: false

                        if (isLiked) {
                            Log.d("사용자가 좋아요를 누른 상태입니다.", "${isLiked}")
                            binding.detailLikeBin.setImageResource(R.drawable.ic_favorite)
                        } else {
                            Log.d("사용자가 좋아요를 누르지 않은 상태입니다.", "${isLiked}")
                            binding.detailLikeBin.setImageResource(R.drawable.ic_favorite_border)
                        }
                    } else {
                        binding.detailLikeBin.setImageResource(R.drawable.ic_favorite_border)
                    }
                }
                .addOnFailureListener { exception ->
                    // 문서를 가져오는 데 실패한 경우
                    Log.d("사용자 정보를 가져오는데 실패했습니다.", "다시 확인", exception)
                    // 기본적으로 하트를 비어있는 상태로 설정합니다.
                    binding.detailLikeBin.setImageResource(R.drawable.ic_favorite_border)
                }
                }
        }
    }




