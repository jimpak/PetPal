package org.bitc.petpalapp.ui.petstargram

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentMachingBinding
import org.bitc.petpalapp.databinding.FragmentPetstargramTestBinding
import org.bitc.petpalapp.model.ApplicationItem
import org.bitc.petpalapp.recyclerviewAdapter.GridAdapter
import org.bitc.petpalapp.recyclerviewAdapter.setMatchingAdapter

class PetstargramTestFragment : Fragment() {
    private var _binding: FragmentPetstargramTestBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPetstargramTestBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //이미지 로드, 리사이클러뷰 그리드 형식으로 출력
        makeRecyclerView()


        binding.addPetsatrfab.setOnClickListener {

            findNavController().navigate(R.id.action_petstarmain_to_insert)
        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.petstarRecyclerView.layoutManager =GridLayoutManager(requireContext(), 3)
        binding.petstarRecyclerView.adapter =
            GridAdapter(requireContext(), testList)
        binding.petstarRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
    }

}
