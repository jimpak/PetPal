package org.bitc.petpalapp.ui.hospital

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentHospitalBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HospitalFragment : Fragment(), OnMapReadyCallback, NaverMap.OnCameraIdleListener {

        private val locationPermissionRequestCode = 1000
        private val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    private var _binding: FragmentHospitalBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationSource: FusedLocationSource

    private var hospitalList: List<HospitalModel>? = null
    private var markerList: MutableList<Marker> = mutableListOf()
    private lateinit var naverMap: NaverMap

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 권한이 모두 수락되었을 때
                initMapView()
            } else {
                // 권한이 거부되었을 때
                showToast()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHospitalBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (!hasPermission()) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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
        // 위치 권한 확인
        if (hasPermission()) {
            // 지도 초기화
            naverMap.locationSource = locationSource
            naverMap.uiSettings.isLocationButtonEnabled = true
            naverMap.locationTrackingMode = LocationTrackingMode.Follow

            naverMap.addOnCameraIdleListener(this)

            hospitalList?.let { showHospitalsInVisibleRegion(it) }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissions,
                locationPermissionRequestCode
            )
        }
    }

    override fun onCameraIdle() {
        hospitalList?.let { showHospitalsInVisibleRegion(it) }
    }

    // 지도 관련 init
    private fun initMapView() {
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.frame_map1) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.frame_map1, it).commit()
            }

        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, locationPermissionRequestCode)
    }

    // 위치 권한
    private fun hasPermission(): Boolean {
        for (permission in permissions) {
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
        Log.d("hospital", "Trying to add markers. Hospitals: $hospitals")

        // 기존 마커 제거
        markerList.forEach { it.map = null }
        markerList.clear()

        hospitals?.forEach { hospital ->
            Log.d("hospital", "Adding marker for hospital: ${hospital.animal_hospital}")
            val marker = Marker()
            marker.position = LatLng(hospital.lat, hospital.lon)
            marker.map = naverMap
            marker.captionText = hospital.animal_hospital
            marker.icon = OverlayImage.fromResource(com.naver.maps.map.R.drawable.navermap_default_marker_icon_blue)

            markerList.add(marker)
            Log.d("hospital", "Added marker for hospital: ${hospital.animal_hospital}")
        }
    }


    private fun showHospitalsInVisibleRegion(hospitals: List<HospitalModel>) {
        val visibleRegionBounds = naverMap.contentBounds
        Log.d("hospital", "Bounds: $visibleRegionBounds")

        val visibleHospitals = hospitals.filter { hospital ->
            val hospitalLatLng = LatLng(hospital.lat, hospital.lon)
            val isContained = visibleRegionBounds.contains(hospitalLatLng)
            Log.d("hospital", "Hospital: $hospital, isContained: $isContained")
            isContained
        }

        Log.d("hospital", "Visible Hospitals: $visibleHospitals")
        addHospitalMarkers(visibleHospitals)

        // RecyclerView 업데이트
        val adapter = HospitalAdapter(requireContext(), visibleHospitals, findNavController())
        binding.hospitalRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.hospitalRecyclerView.adapter = adapter
        binding.hospitalRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )
    }

    private fun showToast() {
        Toast.makeText(requireContext(), getString(R.string.permission_denied_message), Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
