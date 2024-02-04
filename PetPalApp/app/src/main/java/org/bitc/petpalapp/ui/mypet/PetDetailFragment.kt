package org.bitc.petpalapp.ui.mypet

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.bitc.petpalapp.R

class PetDetailFragment : Fragment() {

    companion object {
        fun newInstance() = PetDetailFragment()
    }

    private lateinit var viewModel: PetDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pet_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PetDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}