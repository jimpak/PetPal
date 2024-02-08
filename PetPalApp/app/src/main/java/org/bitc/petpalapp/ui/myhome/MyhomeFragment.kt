package org.bitc.petpalapp.ui.myhome

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentMyhomeBinding
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.model.UserInfo

class MyhomeFragment : Fragment() {

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

        binding.btnRegiPetsitter.setOnClickListener {
            findNavController().navigate(R.id.action_myhomeFragment_to_careRegisterFragment)
        }

        binding.btnpetsitterlist.setOnClickListener {
            findNavController().navigate(R.id.action_myhomeFragment_to_petsitterListRegisterFragment)
        }

        binding.btnRe.setOnClickListener {
            findNavController().navigate(R.id.action_myhomeFragment_to_Myprofile)
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

                    binding.txNickname.setText(item.nickname)
                    binding.txName.setText(item.username)
                    binding.txAddress.setText(item.address)
                    binding.txEmail.setText(item.email)

                    val imgRef =
                        MyApplication.storage.reference.child("images/${item.docId}.jpg")

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
            .addOnFailureListener { Exception ->
                Log.d("bbb", "오류", Exception)
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}