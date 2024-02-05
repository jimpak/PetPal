package org.bitc.petpalapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.FragmentMyprofileBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyprofileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyprofileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding=FragmentMyprofileBinding.inflate(inflater,container, false)

        binding.btnSave.setOnClickListener{
            val nickname=binding.editName.text.toString()
            val username=binding.editNickname.text.toString()
            val pass=binding.editPassword.text.toString()
            val email=binding.editEmail.text.toString()
            val phone=binding.editPhone.text.toString()
            val address=binding.editAddress.text.toString()
            val usecount=binding.editUsecount.text.toString()
            val petcount=binding.editPetcount.text.toString()
            val boardcount=binding.editBoardcount.text.toString()



        }
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyprofileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyprofileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}