package com.example.demo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.model.Recorrido

class RecorrListAdapter(
    private val itemClickListener: OnRecorrClickListener
) : RecyclerView.Adapter<RecorrListAdapter.RecorrViewHolder>() {

    private var recorridoList = emptyList<Recorrido>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecorrViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_recorrido, parent, false)
        return RecorrViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecorrViewHolder, position: Int) {
        val report = recorridoList[position]
        holder.bind(report)
    }

    internal fun setRecorrido(recorridoList: List<Recorrido>) {
        this.recorridoList = recorridoList
        notifyDataSetChanged()
    }

    inner class RecorrViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titulo: TextView = view.findViewById(R.id.textViewCirArea)
        private val resumen: TextView = view.findViewById(R.id.textViewCirObsMeteo)
        private val fecha: TextView = view.findViewById(R.id.textViewTimestamp)

        fun bind(recorrido: Recorrido) {
            itemView.setOnClickListener { itemClickListener.onItemClick(recorrido) }

            titulo.text = "Area: " + recorrido.areaRecorrida
            resumen.text = "Observador: " + recorrido.observador + "\n" + "Meteo: " + recorrido.meteo
            fecha.text = recorrido.fecha
        }
    }

    override fun getItemCount() = recorridoList.size

    interface OnRecorrClickListener {
        fun onItemClick(circuitos: Recorrido)
    }
}