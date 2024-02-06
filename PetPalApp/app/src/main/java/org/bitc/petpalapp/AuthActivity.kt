package org.bitc.petpalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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

        // 이메일 회원가입 폼으로 전환
        binding.btnRegister.setOnClickListener {
            changeVisibility("sign")
        }

        // 이메일 회원가입
        binding.btnEmailSign.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            MyApplication.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.editTextEmail.text.clear()
                    binding.editTextPassword.text.clear()

                    if (task.isSuccessful) {
                        MyApplication.auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener { sendTask ->
                                if (sendTask.isSuccessful) {
                                    Toast.makeText(
                                        baseContext,
                                        "회원가입 메일 유효성 검사 성공",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    changeVisibility("logout")
                                } else {
                                    Toast.makeText(
                                        baseContext,
                                        "회원가입 메일 유효성 검사 실패",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    changeVisibility("logout")
                                }

                            }
                    } else {
                        Toast.makeText(baseContext, "회원가입 실패", Toast.LENGTH_SHORT).show()
                        changeVisibility("logout")
                    }
                }
        }

        // 구글 회원가입
        binding.btnGoogleLogin.setOnClickListener {
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val signInIntent = GoogleSignIn.getClient(this, gso).signInIntent
            requestLauncher.launch(signInIntent)
        }

        // 이메일 로그인
        binding.btnLogin.setOnClickListener {
            //이메일, 비밀번호 로그인.......................
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            MyApplication.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.editTextEmail.text.clear()
                    binding.editTextPassword.text.clear()

                    if (task.isSuccessful) {
                        if (MyApplication.checkAuth()) {
                            MyApplication.email = email
                            changeVisibility("login")
                        } else {
                            Toast.makeText(
                                baseContext,
                                "전송된 이메일로 인증처리가 되지 않았습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(baseContext, "로그인 실패.", Toast.LENGTH_SHORT).show()
                    }
                }
        }


        binding.btnFindPassword.setOnClickListener {
            changeVisibility("find_pass")
        }

        // 비밀번호 찾기
        binding.btnNewPassword.setOnClickListener {
            val email = binding.editTextEmail.text.toString()

            if (email.isNotEmpty()) {
                // 비밀번호 재설정 이메일을 보내기를 시도합니다.
                MyApplication.auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // 비밀번호 재설정 이메일이 성공적으로 보내졌으므로, 이메일은 가입되어 있습니다.
                            Toast.makeText(
                                baseContext,
                                "$email 메일을 확인해 주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.mainTextView.text = "$email 메일을 확인해 주세요."

                        } else {
                            // 이메일이 가입되어 있지 않거나 다른 이슈가 발생한 경우
                            Toast.makeText(
                                baseContext,
                                "가입되지 않은 이메일 주소입니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

            } else {
                // 이메일이 비어있는 경우
                Toast.makeText(
                    baseContext,
                    "이메일 주소를 입력해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding.btnMoveLogin.setOnClickListener {
            changeVisibility("logout")
        }

        // 시작하기
        binding.btnStart.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_auth, menu)

        val logoutMenuItem = menu?.findItem(R.id.logoutMenuItem)

        if (MyApplication.checkAuth()) {
            logoutMenuItem?.isVisible = true
        } else {
            logoutMenuItem?.isVisible = false
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        MyApplication.auth.signOut()
        MyApplication.email = null
        changeVisibility("logout")
        return super.onOptionsItemSelected(item)
    }


    fun changeVisibility(mode: String) {
        if (mode === "login") { // 로그인상태
            binding.run {
                LoginTextView.visibility = View.GONE
                JoinTextView.visibility = View.GONE
                FindPassTextView.visibility = View.GONE
                mainTextView.text = "${MyApplication.email} 님 반갑습니다"
                editTextEmail.visibility = View.GONE
                editTextPassword.visibility = View.GONE
                btnLogin.visibility = View.GONE
                btnEmailSign.visibility = View.GONE
                btnNewPassword.visibility = View.GONE
                btnFindPassword.visibility = View.GONE
                or2.visibility = View.GONE
                btnRegister.visibility = View.GONE
                btnGoogleLogin.visibility = View.GONE
                btnMoveLogin.visibility = View.GONE
                btnStart.visibility = View.VISIBLE
            }

        } else if (mode === "logout") { // 로그아웃상태
            binding.run {
                LoginTextView.visibility = View.VISIBLE
                JoinTextView.visibility = View.GONE
                FindPassTextView.visibility = View.GONE
                mainTextView.text = "로그인 또는 회원가입이 필요합니다."
                editTextEmail.visibility = View.VISIBLE
                editTextPassword.visibility = View.VISIBLE
                btnLogin.visibility = View.VISIBLE
                btnEmailSign.visibility = View.GONE
                btnNewPassword.visibility = View.GONE
                btnFindPassword.visibility = View.VISIBLE
                or2.visibility = View.VISIBLE
                btnRegister.visibility = View.VISIBLE
                btnGoogleLogin.visibility = View.VISIBLE
                btnMoveLogin.visibility = View.GONE
                btnStart.visibility = View.GONE
            }

        } else if (mode === "sign") { // 회원가입
            binding.run {
                LoginTextView.visibility = View.GONE
                JoinTextView.visibility = View.VISIBLE
                FindPassTextView.visibility = View.GONE
                mainTextView.text = ""
                editTextEmail.visibility = View.VISIBLE
                editTextPassword.visibility = View.VISIBLE
                btnLogin.visibility = View.GONE
                btnEmailSign.visibility = View.VISIBLE
                btnNewPassword.visibility = View.GONE
                btnFindPassword.visibility = View.GONE
                or2.visibility = View.GONE
                btnRegister.visibility = View.GONE
                btnGoogleLogin.visibility = View.VISIBLE
                btnMoveLogin.visibility = View.VISIBLE
                btnStart.visibility = View.GONE
            }

        } else if (mode === "find_pass") { // 비밀번호 찾기
            binding.run {
                LoginTextView.visibility = View.GONE
                JoinTextView.visibility = View.GONE
                FindPassTextView.visibility = View.VISIBLE
                mainTextView.text = "이메일 주소를 입력하세요."
                editTextEmail.visibility = View.VISIBLE
                editTextPassword.visibility = View.GONE
                btnLogin.visibility = View.GONE
                btnEmailSign.visibility = View.GONE
                btnNewPassword.visibility = View.VISIBLE
                btnFindPassword.visibility = View.GONE
                or2.visibility = View.GONE
                btnRegister.visibility = View.GONE
                btnGoogleLogin.visibility = View.GONE
                btnMoveLogin.visibility = View.VISIBLE
                btnStart.visibility = View.GONE
            }
        }
    }
}