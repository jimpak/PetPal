package org.bitc.petpalapp

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import org.bitc.petpalapp.databinding.ActivityMainBinding
import org.bitc.petpalapp.ui.mypet.util.myCheckPermission

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val navController: NavController by lazy {
        findNavController(R.id.nav_host_fragment_activity_main)
    }

    private val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(
            setOf(
                R.id.navigation_mypet, R.id.navigation_petstargram,
                R.id.navigation_maching, R.id.navigation_hospital, R.id.navigation_myhome
            )
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myCheckPermission(this) //퍼미션 체크

    }

    override fun onStart() {
        super.onStart()
        val navView: BottomNavigationView = binding.navView


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

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