package org.bitc.petpalapp.ui.mypet

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentMachingBinding
import org.bitc.petpalapp.ui.myhome.PetsitterItem
import org.bitc.petpalapp.ui.mypet.util.MyAdapter
import org.bitc.petpalapp.ui.mypet.util.OnItemClickListener

class MachingFragment : Fragment() , OnItemClickListener {

    private var _binding: FragmentMachingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMachingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        makeRecyclerView()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    fun makeRecyclerView(){
        //파이어베이스db, pets 컬렉션 가져오기
        MyApplication.db.collection("petsitters")
            .get()
            .addOnSuccessListener {result ->
                val itemList = mutableListOf<PetsitterItem>()
                for(document in result){
                    val item = document.toObject(PetsitterItem::class.java)
                    item.docId = document.id

                    Log.d("aaaa", "${item.docId}")
                    itemList.add(item)
                }
                binding.matcingRecyclerView.layoutManager= LinearLayoutManager(requireContext())
                binding.matcingRecyclerView.adapter = MyAdapter(requireContext(), itemList, this)
                binding.matcingRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
                )
            }
            .addOnFailureListener {
                    exception -> Log.d("aaa", "서버 데이터 획득 실패", exception)
            }
    }

    override fun onItemClick(docId: String?) {
        // 리사이클러뷰 아이템 클릭 시 실행될 로직
        val bundle = bundleOf("docId" to docId)

        // 프래그먼트 전환
        findNavController().navigate(R.id.action_matchingFragment_to_petsitterDetailFragment, bundle)
    }
}