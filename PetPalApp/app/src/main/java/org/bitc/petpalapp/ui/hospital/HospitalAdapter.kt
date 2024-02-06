package org.bitc.petpalapp.ui.hospital

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import org.bitc.petpalapp.databinding.HospitalItemBinding


class HospitalViewHolder(val binding: HospitalItemBinding) : RecyclerView.ViewHolder(binding.root)

class HospitalAdapter(val context: Context, val datas: List<HospitalModel>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val fragmentManager = (context as AppCompatActivity).supportFragmentManager
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HospitalViewHolder(
            HospitalItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return datas?.size ?:0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as HospitalViewHolder).binding
        val hospital = datas?.get(position)

        hospital?.let {
            binding.animalHospital.text = it.animal_hospital
            binding.gugun.text = it.gugun
            binding.tel.text = it.tel
        }
    }

}
