package com.example.demo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.model.Circuito

class CircuitoListAdapter(
    private val itemClickListener: OnCircuitoClickListener
) : RecyclerView.Adapter<CircuitoListAdapter.CircuitoViewHolder>() {

    private var circuitos = emptyList<Circuito>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircuitoViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_censo, parent, false)
        return CircuitoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CircuitoViewHolder, position: Int) {
        val report = circuitos[position]
        holder.bind(report)
    }

    internal fun setCircuito(circuitos: List<Circuito>) {
        this.circuitos = circuitos
        notifyDataSetChanged()
    }

    inner class CircuitoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val reporte_resumen: TextView = view.findViewById(R.id.report_resumen)
        private val date: TextView = view.findViewById(R.id.report_timestamp)

        fun bind(circuito: Circuito) {
            itemView.setOnClickListener { itemClickListener.onItemClick(circuito) }
            reporte_resumen.text = "Observador.: " + circuito.observador + " - Meteo: " + circuito.meteo
            date.text = circuito.fecha
        }
    }

    override fun getItemCount() = circuitos.size

    interface OnCircuitoClickListener {
        fun onItemClick(circuitos: Circuito)
    }
}