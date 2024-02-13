package org.bitc.petpalapp.ui.petstargram

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentDetailTestBinding
import org.bitc.petpalapp.databinding.FragmentMypetBinding
import org.bitc.petpalapp.databinding.FragmentPetstargramTestBinding
import org.bitc.petpalapp.ui.mypet.MypetFragment
import org.bitc.petpalapp.ui.mypet.MypetViewModel
import org.bitc.petpalapp.ui.mypet.PetData
import org.bitc.petpalapp.ui.mypet.utils.MyAdapter


class DetailTestFragment : Fragment() {
    lateinit var binding: FragmentDetailTestBinding

    companion object {
        fun newInstance() = MypetFragment()
    }

    private lateinit var viewModel: MypetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailTestBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


}