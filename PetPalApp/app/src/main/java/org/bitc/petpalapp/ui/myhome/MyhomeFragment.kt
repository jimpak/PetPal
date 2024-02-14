package org.bitc.petpalapp.ui.myhome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentMyhomeBinding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import org.bitc.petpalapp.AuthActivity
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.model.ApplicationItem
import org.bitc.petpalapp.model.Event
import org.bitc.petpalapp.model.UserInfo
import org.bitc.petpalapp.recyclerviewAdapter.EventsAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyhomeFragment : Fragment() {
    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var eventsAdapter: EventsAdapter
    private var _binding: FragmentMyhomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(MyhomeViewModel::class.java)

        _binding = FragmentMyhomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        calendarView = binding.calendarView
        recyclerView = binding.recyclerView
//        binding.btnRegiPetsitter.setOnClickListener {
//            findNavController().navigate(R.id.action_myhomeFragment_to_careRegisterFragment)
//        }

//        binding.btnpetsitterlist.setOnClickListener {
//            findNavController().navigate(R.id.action_myhomeFragment_to_petsitterListRegisterFragment)
//        }

        binding.btnRe.setOnClickListener {
            findNavController().navigate(R.id.action_myhomeFragment_to_Myprofile)
        }

        // RecyclerView 초기화
        eventsAdapter = EventsAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = eventsAdapter

        // CalendarView 리스너 설정
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = formatDate(year, month, dayOfMonth)
            fetchData(selectedDate)
        }


        val docRef = MyApplication.db.collection("users")

        docRef
            .whereEqualTo("email", MyApplication.email)
            .get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<UserInfo>()
                for (document in result) {
                    val item = document.toObject(UserInfo::class.java)
                    item.docId = document.id
                    itemList.add(item)

                    binding.txName.setText(item.username)
                    binding.txNickname.setText(item.nickname)
                    binding.txAddress.setText(item.address)
                    binding.txEmail.setText(item.email)
                    binding.resultPetsittercount.text = (item.petsittercount ?: 0).toString()
                    binding.resultPetstarcount.text = (item.petstarcount ?: 0).toString()
                    binding.resultPoint.text =  (item.point ?: 0).toString()

                    // 전달된 데이터 확인
                    val imagePath = arguments?.getString("imagePath")

                    // imagePath를 사용하여 UI 업데이트
                    if (!imagePath.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(imagePath)
                            .into(binding.profileImage)
                    }else{
                        val imgRef =
                            MyApplication.storage.reference.child("userimages/${item.docId}.jpg")

                        imgRef.downloadUrl.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Glide
                                    .with(requireContext())
                                    .load(task.result)
                                    .into(binding.profileImage)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { Exception ->
                Log.d("bbb", "오류", Exception)
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun fetchData(selectedDate: String) {
        // Firebase Firestore에서 데이터 가져오기
        MyApplication.db.collection("applications")
            .whereEqualTo("petsitterId", MyApplication.email)
            .whereEqualTo("date", selectedDate)
            .get()
            .addOnSuccessListener { result ->
                val eventsList = ArrayList<Event>()

                for (document in result) {
                    val eventName = document.getString("applierNickname") ?: ""
                    val eventMemo = document.getString("status") ?: ""
                    eventsList.add(Event(eventName, eventMemo))
                }

                // RecyclerView에 데이터 업데이트
                eventsAdapter.updateData(eventsList)
            }
            .addOnFailureListener { exception ->
                // 데이터 가져오기 실패
                exception.printStackTrace()
            }
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_auth, menu)

        val logoutMenuItem = menu.findItem(R.id.logoutMenuItem)

        if (MyApplication.checkAuth()) {
            logoutMenuItem.isVisible = true
        } else {
            logoutMenuItem.isVisible = false
        }

        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logoutMenuItem -> {
                MyApplication.auth.signOut()
                MyApplication.email = null
                startActivity(Intent(requireContext(), AuthActivity::class.java))
                requireActivity().finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}

