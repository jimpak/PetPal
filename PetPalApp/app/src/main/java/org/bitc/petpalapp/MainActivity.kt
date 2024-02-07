package org.bitc.petpalapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.content.ContextCompat
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.bitc.petpalapp.MyApplication.Companion.checkAuth
import org.bitc.petpalapp.databinding.ActivityMainBinding
import org.bitc.petpalapp.ui.myhome.myCheckPermission

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val navController: NavController by lazy {
        findNavController(R.id.nav_host_fragment_activity_main)
    }

    private val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(
            setOf(
                R.id.navigation_mypet, R.id.navigation_petstargram,
                R.id.navigation_machingstatus, R.id.navigation_hospital, R.id.navigation_myhome

            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myCheckPermission(this)


        myCheckPermission(this)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // 뒤로가기 버튼 클릭 시 처리
                goBackToPreviousFragment()
                return true
            }
            // 다른 메뉴 아이템에 대한 처리 추가 가능
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goBackToPreviousFragment() {
        // 이전 프래그먼트로 돌아가는 코드
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStack()
    }

}