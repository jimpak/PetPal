package org.bitc.petpalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIMEOUT: Long = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        object : CountDownTimer(SPLASH_TIMEOUT, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Do nothing on tick
            }

            override fun onFinish() {
                val intent = Intent(this@SplashActivity, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.start()
    }
}