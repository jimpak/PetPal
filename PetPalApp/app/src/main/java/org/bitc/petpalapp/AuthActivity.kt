package org.bitc.petpalapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import org.bitc.petpalapp.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (MyApplication.checkAuth()) {
            changeVisibility("login")
        } else {
            changeVisibility("logout")
        }

        val requestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                MyApplication.auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            MyApplication.email = account.email // 구글이메일 인증서 저장
                            changeVisibility("login")
                        } else {
                            changeVisibility("logout")
                        }
                    }
            } catch (e: ApiException) {
                changeVisibility("logout")
            }
        }
    }


    fun changeVisibility(mode: String) {
        if (mode === "login") {
            binding.run {
//                authMainTextView.text = "${MyApplication.email} 님 반갑습니다"
//                logoutBtn.visibility = View.VISIBLE
//                goSignInBtn.visibility = View.GONE
//                googleLoginBtn.visibility = View.GONE
//                authEmailEditView.visibility = View.GONE
//                authPasswordEditView.visibility = View.GONE
//                signBtn.visibility = View.GONE
//                loginBtn.visibility = View.GONE
            }
        } else if (mode === "logout") {
            binding.run {
//                authMainTextView.text = "로그인 하거나 회원가입 해주세요."
//                logoutBtn.visibility = View.GONE
//                goSignInBtn.visibility = View.VISIBLE
//                googleLoginBtn.visibility = View.VISIBLE
//                authEmailEditView.visibility = View.VISIBLE
//                authPasswordEditView.visibility = View.VISIBLE
//                signBtn.visibility = View.GONE
//                loginBtn.visibility = View.VISIBLE
            }
        } else if (mode === "signin") {
            binding.run {
//                logoutBtn.visibility = View.GONE
//                goSignInBtn.visibility = View.GONE
//                googleLoginBtn.visibility = View.GONE
//                authEmailEditView.visibility = View.VISIBLE
//                authPasswordEditView.visibility = View.VISIBLE
//                signBtn.visibility = View.VISIBLE
//                loginBtn.visibility = View.GONE
            }
        }
    }
}