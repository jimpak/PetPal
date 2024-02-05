package org.bitc.petpalapp.ui.matching

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentGetPetsitterBinding
import org.bitc.petpalapp.databinding.FragmentSetPetsitterBinding
import org.bitc.petpalapp.ui.mypet.util.ApplicationItem


class SetPetsitterFragment : Fragment()  {
    private var _binding: FragmentSetPetsitterBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSetPetsitterBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        makeRecyclerView()

    }


    fun makeRecyclerView() {
        //파이어베이스db, applications 컬렉션 가져오기
        MyApplication.db.collection("applications")
            .whereEqualTo("petsitterId", MyApplication.email)
            .get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<ApplicationItem>()
                for (document in result) {
                    val item = document.toObject(ApplicationItem::class.java)
                    item.docId = document.id

                    Log.d("aaaa", "${item.docId}")
                    itemList.add(item)

                }

                binding.setmactingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.setmactingRecyclerView.adapter =
                    setMatchingAdapter(requireContext(), itemList)
                binding.setmactingRecyclerView.addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        LinearLayoutManager.VERTICAL
                    )
                )
            }
            .addOnFailureListener { exception ->
                Log.d("aaa", "서버 데이터 획득 실패", exception)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun restartFragment() {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.detach(this).attach(this).commit()
    }


}
