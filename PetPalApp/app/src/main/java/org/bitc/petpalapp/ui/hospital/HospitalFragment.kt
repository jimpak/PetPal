package org.bitc.petpalapp.ui.hospital

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import org.bitc.petpalapp.R

import androidx.navigation.fragment.findNavController
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import org.bitc.petpalapp.databinding.FragmentHospitalBinding

class HospitalFragment : Fragment(), OnMapReadyCallback {
    private var naverMap: NaverMap? = null

    private var _binding: org.bitc.petpalapp.databinding.FragmentHospitalBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHospitalBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val drawerLayout: DrawerLayout = binding.drawerLayout

        binding.navView.setNavigationItemSelectedListener {
                menuItem -> when (menuItem.itemId) {
            R.id.nav_hospital1 -> {
                // Item 1을 클릭했을 때의 동작
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.addToBackStack(null)
                findNavController().navigate(R.id.action_hospital_to_detail)
                transaction.commit()
            }
            else -> false
        }
            // 항목을 클릭하면 드로어를 닫도록 설정
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = NaverMapOptions()
            .camera(CameraPosition(LatLng(35.1798159, 129.0750222), 18.0))
            .mapType(NaverMap.MapType.Terrain)

        val mapFragment = childFragmentManager.findFragmentById(R.id.container_map) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                childFragmentManager.beginTransaction().replace(R.id.container_map, it).commit()
            }

        // getMapAsync 호출
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        val marker = Marker()
        val marker2 = Marker()

        marker2.position= LatLng(35.1629725, 129.1777527)
        marker2.map = naverMap
        marker.position= LatLng(35.16302, 129.177735)
        marker.map = naverMap

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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}