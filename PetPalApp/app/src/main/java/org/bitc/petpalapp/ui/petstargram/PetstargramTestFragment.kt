package org.bitc.petpalapp.ui.petstargram

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentMachingBinding
import org.bitc.petpalapp.databinding.FragmentPetstargramTestBinding
import org.bitc.petpalapp.model.ApplicationItem
import org.bitc.petpalapp.model.PetstargamItem
import org.bitc.petpalapp.recyclerviewAdapter.GridAdapter
import org.bitc.petpalapp.recyclerviewAdapter.setMatchingAdapter
import org.bitc.petpalapp.ui.mypet.PetData
import org.bitc.petpalapp.ui.mypet.util.OnItemClickListener
import org.bitc.petpalapp.ui.mypet.utils.MyAdapter

class PetstargramTestFragment : Fragment(), OnItemClickListener {
    private var _binding: FragmentPetstargramTestBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPetstargramTestBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //이미지 로드, 리사이클러뷰 그리드 형식으로 출력
        makeRecyclerView()

        binding.addPetsatrfab.setOnClickListener {

            findNavController().navigate(R.id.action_petstarmain_to_insert)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    fun makeRecyclerView() {
        //테스트 더미 데이터 넣기
        val testList = mutableListOf<String>()
        for (i in 1..30) {
            testList.add(i.toString())
        }

            MyApplication.db.collection("petstars")
                .get()
                .addOnSuccessListener { result ->
                    val itemList = mutableListOf<PetstargamItem>()
                    for (document in result) {
                        val item = document.toObject(PetstargamItem::class.java)
                        item.docId = document.id
                        Log.d("여기는 들어왔나","${item.email},${item.content}")
                        itemList.add(item)
                    }
                    binding.petstarRecyclerView.layoutManager =GridLayoutManager(requireContext(), 3)
                    binding.petstarRecyclerView.adapter =
                        GridAdapter(requireContext(), itemList, this)
                    binding.petstarRecyclerView.addItemDecoration(
                        DividerItemDecoration(
                            requireContext(),
                            LinearLayoutManager.VERTICAL
                        )
                    )
                }
                .addOnFailureListener { exception ->
                    Log.d("kkang", "error..getting document..", exception)
                }

    }

    override fun onItemClick(docId: String?) {

        val bundle = bundleOf("docId" to docId)

        findNavController().navigate(R.id.action_petstarImg_detail, bundle)
    }

}
