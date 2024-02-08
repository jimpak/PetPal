package org.bitc.petpalapp.ui.hospital

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentHospitalDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HospitalDetailFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentHospitalDetailBinding? = null
    private val binding get() = _binding!!
    lateinit var mapFragment: MapFragment

    private var hospital: HospitalModel? = null

    override fun onStart() {
        super.onStart()

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.frame_map2) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.frame_map2, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHospitalDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hno = arguments?.getLong("hno", 0) ?: 0
        val networkService = (requireContext().applicationContext as MyApplication).networkService
        val hospitalModelCall = networkService.getHospital(hno)

        hospitalModelCall.enqueue(object : Callback<HospitalModel> {
            override fun onResponse(call: Call<HospitalModel>, response: Response<HospitalModel>) {
                hospital = response.body()
                binding.hospitalName.text = hospital?.animal_hospital.orEmpty()
                binding.hospitalAddress.text = hospital?.road_address.orEmpty()
                binding.hospitalPhone.text = hospital?.tel.orEmpty()
            }

            override fun onFailure(call: Call<HospitalModel>, t: Throwable) {
                call.cancel()
            }
        })
    }

    override fun onMapReady(naverMap: NaverMap) {
        if (isAdded) {
            hospital?.let {
                val lat = it.lat
                val lon = it.lon

                val marker = Marker()
                marker.position = LatLng(lat, lon)
                marker.map = naverMap

                val cameraUpdate = CameraUpdate.scrollTo(LatLng(lat, lon))
                    .animate(CameraAnimation.Fly, 2000)
                naverMap.moveCamera(cameraUpdate)
            }
        }
    }
}
