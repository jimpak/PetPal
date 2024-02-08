package org.bitc.petpalapp.ui.petstargram

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentPetstarWriteBinding
import org.bitc.petpalapp.databinding.FragmentPetstargramTestBinding
import org.bitc.petpalapp.recyclerviewAdapter.GridAdapter

class PetstarWriteFragment : Fragment() {
    private var _binding: FragmentPetstarWriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPetstarWriteBinding.inflate(inflater, container, false)
        val root: View = binding.root







//        binding.setOnClickListener {
//
//            findNavController().navigate(R.id.action_petstarmain_to_insert)
//        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





}
