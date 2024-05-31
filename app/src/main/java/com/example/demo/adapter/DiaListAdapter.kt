package com.example.demo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.model.Dia

class DiaListAdapter(
    private val itemClickListener: OnDiaClickListener
) : RecyclerView.Adapter<DiaListAdapter.DiaViewHolder>() {

    private var diaList = emptyList<Dia>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_dia, parent, false)
        return DiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaViewHolder, position: Int) {
        val dia = diaList[position]
        holder.bind(dia)
    }

    internal fun setDia(diaList: List<Dia>) {
        this.diaList = diaList
        notifyDataSetChanged()
    }

    inner class DiaViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val numDia: TextView = view.findViewById(R.id.num_dia)
        private val fecha: TextView = view.findViewById(R.id.textViewTimestamp)
        private var posicion = 1

        fun bind(dia: Dia) {
            itemView.setOnClickListener { itemClickListener.onItemClick(dia) }
            numDia.text = (diaList.indexOf(dia) + 1).toString()
            fecha.text = dia.fecha
        }
    }

    override fun getItemCount() = diaList.size

    interface OnDiaClickListener {
        fun onItemClick(elem: Dia)
    }
}