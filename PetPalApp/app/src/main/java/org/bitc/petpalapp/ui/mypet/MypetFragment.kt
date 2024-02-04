package org.bitc.petpalapp.ui.mypet

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentMypetBinding
import org.bitc.petpalapp.databinding.FragmentPetstargramBinding
import org.bitc.petpalapp.ui.petstargram.PetstargramViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.ui.mypet.util.ItemData
import org.bitc.petpalapp.ui.mypet.util.MyAdapter
import org.bitc.petpalapp.ui.mypet.util.OnItemClickListener

class MypetFragment : Fragment() , OnItemClickListener {

    private var _binding: FragmentMypetBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(PetstargramViewModel::class.java)

        _binding = FragmentMypetBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        makeRecyclerView()


        // tvMypetRegi 버튼 클릭 시 새로운 프래그먼트로 이동
        binding.petInsert.setOnClickListener {
            findNavController().navigate(R.id.action_mypetFragment_to_petregiFragment)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    fun makeRecyclerView(){
        //파이어베이스db, pets 컬렉션 가져오기
        MyApplication.db.collection("pets")
            .get()
            .addOnSuccessListener {result ->
                val itemList = mutableListOf<ItemData>()
                for(document in result){
                    val item = document.toObject(ItemData::class.java)
                    item.docId = document.id

                    Log.d("aaaa", "${item.docId}")
                    itemList.add(item)
                }
                binding.mainRecyclerView.layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
                binding.mainRecyclerView.adapter = MyAdapter(requireContext(), itemList, this)
            }
            .addOnFailureListener {
                    exception -> Log.d("aaa", "서버 데이터 획득 실패", exception)
            }
    }

    override fun onItemClick(docId: String?) {
        // 리사이클러뷰 아이템 클릭 시 실행될 로직
        val bundle = bundleOf("docId" to docId)

        // 프래그먼트 전환
        findNavController().navigate(R.id.action_myFragment_to_detailFragment, bundle)
    }


}