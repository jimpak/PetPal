package org.bitc.petpalapp.ui.myhome

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.bitc.petpalapp.R

class CareRegisterFragment : Fragment() {

    companion object {
        fun newInstance() = CareRegisterFragment()
    }

    private lateinit var viewModel: CareRegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_care_register, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CareRegisterViewModel::class.java)
        // TODO: Use the ViewModel
    }

}