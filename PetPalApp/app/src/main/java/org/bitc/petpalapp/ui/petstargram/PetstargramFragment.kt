
package org.bitc.petpalapp.ui.petstargram

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import org.bitc.petpalapp.AuthActivity
import org.bitc.petpalapp.MyApplication
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentPetstargramBinding
import org.bitc.petpalapp.ui.mypet.utils.MyAdapter

//import org.bitc.petpalapp.ui.petstargram.util.MyAdapter

class PetstargramFragment : Fragment() {

    lateinit var binding: FragmentPetstargramBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
//    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val dashboardViewModel =
//            ViewModelProvider(this).get(PetstargramViewModel::class.java)

        binding = FragmentPetstargramBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        binding.petstarAdd.setOnClickListener {
//            findNavController().navigate(R.id.action_petstargram_add)
//        }

//        binding.petImg1.setOnClickListener {
//            findNavController().navigate(R.id.action_petstargram_add)
//        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        stagstore()
    }

    private fun stagstore() {
//        MyApplication.db.collection("news")
//            .get()
//            .addOnSuccessListener { result->
//                val itemList= mutableListOf<itemPetstar>()
//                for (document in result){
//                    val item=document.toObject(itemPetstar::class.java)
//                    item.docId=document.id
//                    itemList.add(item)
//                }
//                binding.petRecyclerview.layoutManager=
//                    GridLayoutManager(requireContext(),3)
//                binding.petRecyclerview.adapter=
//                    MyAdapter(requireContext(),itemList)
//            }
//            .addOnFailureListener {
//                Log.d("error","error..getting document")
//                Toast.makeText(requireContext(),"서버 획득 실패",Toast.LENGTH_SHORT).show()
//            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        startActivity(Intent(requireContext(),AuthActivity::class.java))
        return super.onCreate(savedInstanceState)
    }








}
