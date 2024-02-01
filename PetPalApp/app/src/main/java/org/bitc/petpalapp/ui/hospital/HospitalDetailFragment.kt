package org.bitc.petpalapp.ui.hospital

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.bitc.petpalapp.R

class HospitalDetailFragment : Fragment() {

    companion object {
        fun newInstance() = HospitalDetailFragment()
    }

    private lateinit var viewModel: HospitalDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hospital_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HospitalDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}