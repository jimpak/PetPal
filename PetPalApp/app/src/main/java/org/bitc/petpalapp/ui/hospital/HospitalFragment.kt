package org.bitc.petpalapp.ui.hospital

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentHospitalBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HospitalFragment : Fragment(), OnMapReadyCallback {

        private val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    private var _binding: FragmentHospitalBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    private var hospitalList: List<HospitalModel>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHospitalBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (!hasPermission()) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            initMapView()
        }

        return root
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
                            hospitalList = hospitalListModel.hospitals
                            Log.d("hospitalList", "$hospitalList")
                            val adapter = HospitalAdapter(requireContext(), hospitalList, findNavController())

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

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        // 현재 위치
        naverMap.locationSource = locationSource
        // 현재 위치 버튼 기능
        naverMap.uiSettings.isLocationButtonEnabled = true
        // 위치를 추적하면서 카메라도 따라 움직인다.
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        hospitalList?.let { addHospitalMarkers(it) }
    }

    // 지도 관련 init
    private fun initMapView() {
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.frame_map1) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.frame_map1, it).commit()
            }

        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    // 위치 권한
    private fun hasPermission(): Boolean {
        for (permission in PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    // 마커
    private fun addHospitalMarkers(hospitals: List<HospitalModel>?) {
        hospitals?.forEach { hospital ->
            val marker = Marker()
            marker.position = LatLng(hospital.lat, hospital.lon)
            marker.map = naverMap
            marker.captionText = hospital.animal_hospital
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
