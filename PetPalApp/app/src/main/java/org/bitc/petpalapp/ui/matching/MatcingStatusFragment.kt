package org.bitc.petpalapp.ui.matching

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentApplicantBinding
import org.bitc.petpalapp.databinding.FragmentMatcingStatusBinding

class MatcingStatusFragment : Fragment() {
    private var _binding: FragmentMatcingStatusBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMatcingStatusBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupViewPager()

        return root
    }


    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(GetPetsitterFragment(), "내가받은돌봄")
        adapter.addFragment(SetPetsitterFragment(), "내가한돌봄")
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}