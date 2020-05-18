package com.example.app_ointmentt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_ointmentt.R
import com.example.app_ointmentt.models.Doctor
import kotlinx.android.synthetic.main.cardview_notification.view.*

class AllDoctorAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<Doctor> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return AllDoctorAdapter.DoctorViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.cardview_doctor_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder){
            is AllDoctorAdapter.DoctorViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    fun submitList(titleList: List<Doctor>){
        items = titleList

    }

    class DoctorViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView){
        val doctorName: TextView = itemView.txt
        fun bind(doctor: Doctor){
            doctorName.setText(doctor.name)
        }
    }
}