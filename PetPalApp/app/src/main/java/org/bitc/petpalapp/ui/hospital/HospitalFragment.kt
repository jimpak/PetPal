package org.bitc.petpalapp.ui.hospital

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.bitc.petpalapp.databinding.FragmentHospitalBinding
import org.bitc.petpalapp.RetrofitApplication

class HospitalFragment : Fragment() {

    private lateinit var binding: FragmentHospitalBinding
    private lateinit var hospitalAdapter: HospitalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHospitalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val networkService = (requireContext().applicationContext as RetrofitApplication).networkService
        val hospitalListCall = networkService.getHospitalList()

//        hospitalListCall.enqueue(object: Callback<HospitalListModel> {
//            override fun onResponse(
//                call: Call<HospitalListModel>,
//                response: Response<HospitalListModel>
//            ) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onFailure(call: Call<HospitalListModel>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//        })
    }
}