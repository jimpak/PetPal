package org.bitc.petpalapp.ui.hospital

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.bitc.petpalapp.databinding.HospitalItemBinding


class HospitalViewHolder(val binding: HospitalItemBinding) : RecyclerView.ViewHolder(binding.root)

class HospitalAdapter(val context: Context, private val hospitals: List<HospitalModel>?) :
    RecyclerView.Adapter<HospitalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val binding = HospitalItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HospitalViewHolder(binding)
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
    }
}