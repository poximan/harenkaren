package com.example.demo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.model.Dia

class DiaListAdapter(
    private val itemListener: OnDiaClickListener
) : RecyclerView.Adapter<DiaListAdapter.DiaViewHolder>() {

    private var diaList: List<Dia> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_dia, parent, false)
        return DiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaViewHolder, position: Int) {
        val dia = diaList[position]
        holder.numDia.text = (position + 1).toString()
        holder.timestamp.text = dia.fecha
        holder.deleteButton.setOnClickListener {
            itemListener.onDeleteClick(dia)
        }
    }

    internal fun setDia(diaList: List<Dia>) {
        this.diaList = diaList
        notifyDataSetChanged()
    }

    inner class DiaViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val numDia: TextView = view.findViewById(R.id.num_dia)
        val timestamp: TextView = view.findViewById(R.id.textViewTimestamp)
        private val icono: ImageView = view.findViewById(R.id.grafDia)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteDia)

        init {
            view.setOnClickListener {
                itemListener.onItemClick(diaList[adapterPosition])
            }
            icono.setOnClickListener {
                itemListener.onIconClick(diaList[adapterPosition])
            }
        }
    }

    override fun getItemCount() = diaList.size

    interface OnDiaClickListener {
        fun onItemClick(elem: Dia)
        fun onIconClick(dia: Dia)
        fun onDeleteClick(dia: Dia)
    }
}