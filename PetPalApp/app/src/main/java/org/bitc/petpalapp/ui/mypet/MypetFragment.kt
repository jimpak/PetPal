package org.bitc.petpalapp.ui.mypet

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentMypetBinding
import org.bitc.petpalapp.ui.mypet.util.OnItemClickListener
import org.bitc.petpalapp.ui.mypet.utils.MyAdapter

class MypetFragment : Fragment(), OnItemClickListener {
    lateinit var binding: FragmentMypetBinding

    companion object {
        fun newInstance() = MypetFragment()
    }

    private lateinit var viewModel: MypetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("requestKey") { _, result ->
            if (result.getBoolean("petImageUpdated", false)) {
                makeRecyclerView()
            }
        }

        setFragmentResultListener("requestKey") { _, result ->
            if (result.getBoolean("petImageRegister", false)) {
                makeRecyclerView()
            }
        }

        makeRecyclerView()

        binding.petsitterRig.setOnClickListener {
            findNavController().navigate(R.id.action_mypetFragment_to_petsitterregFragment)
        }

        binding.petsitterList.setOnClickListener {
            findNavController().navigate(R.id.action_mypetFragment_to_petsitterListFragment)
        }

        binding.starInsert.setOnClickListener {
            findNavController().navigate(R.id.action_myFragment_to_petstarInsert)
        }

        binding.mypetAdd.setOnClickListener{
            findNavController().navigate(R.id.mypet_reigster)
        }

    }

    fun makeRecyclerView() {
        MyApplication.db.collection("pets")
            .whereEqualTo("email",MyApplication.email)
            .get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<PetData>()
                for (document in result) {
                    val item = document.toObject(PetData::class.java)
                    item.petId = document.id
                    itemList.add(item)
                }
                if (itemList.isNullOrEmpty()){
                    binding.mypetBackgroud.setBackgroundResource(R.drawable.mypet)
                }
                val imagePath = arguments?.getString("imagePath").toString()

                binding.petRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                binding.petRecyclerView.adapter =
                    MyAdapter(requireContext(), itemList, this)
            }
            .addOnFailureListener { exception ->
                Log.d("kkang", "error..getting document..", exception)
            }
    }

    override fun onItemClick(petId: String?) {

        val bundle = bundleOf("petId" to petId)

        findNavController().navigate(R.id.action_myFragment_to_detailFragment, bundle)
    }

}