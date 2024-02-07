package org.bitc.petpalapp.ui.hospital

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.databinding.FragmentHospitalBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HospitalFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentHospitalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHospitalBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()

        val networkService = (requireContext().applicationContext as MyApplication).networkService
        val hospitalListCall = networkService.getHospitalList()

        hospitalListCall.enqueue(object :Callback<HospitalListModel> {
            override fun onResponse(
                call: Call<HospitalListModel>,
                response: Response<HospitalListModel>
            ) {
                if (isAdded) { // 프래그먼트 연결 확인
                    if (response.isSuccessful) {
                        response.body()?.let { hospitalListModel ->
                            val hospitalList = hospitalListModel.hospitals
                            Log.d("hospitalList", "$hospitalList")
                            val adapter = HospitalAdapter(requireContext(), hospitalList)
                            binding.hospitalRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                            binding.hospitalRecyclerView.adapter = adapter
                            binding.hospitalRecyclerView.addItemDecoration(
                                DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<HospitalListModel>, t: Throwable) {
                call.cancel()
            }

        })
    }

    override fun onMapReady(p0: NaverMap) {
        TODO("Not yet implemented")
    }

}
