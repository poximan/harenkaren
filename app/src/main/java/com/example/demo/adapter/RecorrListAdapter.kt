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
        val recorr = recorridoList[position]
        holder.bind(recorr)
    }

    internal fun setRecorrido(recorridoList: List<Recorrido>) {
        this.recorridoList = recorridoList
        notifyDataSetChanged()
    }

    inner class RecorrViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val id: TextView = view.findViewById(R.id.num_recorr)
        private val observador: TextView = view.findViewById(R.id.text_observador)
        private val area: TextView = view.findViewById(R.id.area_recorrida)
        private val meteo: TextView = view.findViewById(R.id.text_meteo)
        private val marea: TextView = view.findViewById(R.id.text_marea)
        private val fecha: TextView = view.findViewById(R.id.textViewTimestamp)

        fun bind(recorrido: Recorrido) {
            itemView.setOnClickListener { itemClickListener.onItemClick(recorrido) }

            id.text = recorrido.contadorInstancias.toString()
            observador.text = "Observador: " + recorrido.observador
            area.text = "Area recorrida: " + recorrido.areaRecorrida
            meteo.text = "Condicion climatica: " + recorrido.meteo
            marea.text = "Condicion climatica: " + recorrido.marea
            fecha.text = "Fecha inicio: " + recorrido.fechaIni
        }
    }

    override fun getItemCount() = recorridoList.size

    interface OnRecorrClickListener {
        fun onItemClick(elem: Recorrido)
    }
}