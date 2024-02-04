package org.bitc.petpalapp.ui.hospital


import com.naver.maps.map.MapFragment
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentHospitalDetailBinding


class HospitalDetailFragment : Fragment(), OnMapReadyCallback {
    private var naverMap: NaverMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding = FragmentHospitalDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = NaverMapOptions()
            .camera(CameraPosition(LatLng(35.1798159, 129.0750222), 18.0))
            .mapType(NaverMap.MapType.Terrain)

        val mapFragment = childFragmentManager.findFragmentById(R.id.container_map2) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                childFragmentManager.beginTransaction().replace(R.id.container_map2, it).commit()
            }

        // getMapAsync 호출
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap


        val marker2 = Marker()

        marker2.position= LatLng(35.1629725, 129.1777527)
        marker2.map = naverMap


        val cameraUpdate = CameraUpdate.scrollTo(LatLng(35.16302, 129.177735))
            .animate(CameraAnimation.Fly, 1000)
        naverMap.moveCamera(cameraUpdate)


        naverMap.mapType = NaverMap.MapType.Basic
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRAFFIC, true)

        val coord = LatLng(35.16302, 129.177735)
        Log.d("aaaa","위도: ${coord.latitude}, 경도: ${coord.longitude}")
        // 초기 지도 위치 및 줌 설정
        val target = LatLng(37.5666103, 126.9783882) // 서울의 좌표

        naverMap.moveCamera(cameraUpdate)
    }

}