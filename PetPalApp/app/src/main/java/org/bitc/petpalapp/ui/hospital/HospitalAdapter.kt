package org.bitc.petpalapp.ui.hospital

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import org.bitc.petpalapp.R
import org.bitc.petpalapp.databinding.HospitalItemBinding

class HospitalViewHolder(val binding: HospitalItemBinding, val navController: NavController) : RecyclerView.ViewHolder(binding.root)

class HospitalAdapter(val context: Context, val hospitals: List<HospitalModel>?, val navController: NavController) :
    RecyclerView.Adapter<HospitalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val binding =
            HospitalItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HospitalViewHolder(binding, navController)
    }

    override fun getItemCount(): Int {
        return hospitals?.size ?: 0
    }

    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {
        val binding = holder.binding
        val hospital = hospitals?.get(position)

        hospital?.let {
            binding.animalHospital.text = it.animal_hospital
            binding.gugun.text = it.gugun
            binding.tel.text = it.tel
        }

        binding.btnMoveDetail.setOnClickListener {
            val hospital = hospitals?.get(position)
            val hno = hospital?.hno ?: 0

            val bundle = Bundle()
            bundle.putLong("hno", hno)

            val navController = holder.navController
            navController.navigate(R.id.action_hospital_to_detail, bundle)
        }
    }
}
