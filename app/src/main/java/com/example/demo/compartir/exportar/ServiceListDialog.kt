package com.example.demo.compartir.exportar

import android.app.Dialog
import android.content.Context
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.view.get
import com.example.demo.R

class ServiceListDialog(context: Context) : Dialog(context) {

    private lateinit var listView: ListView
    private val adapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_list_item_1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_service_list)

        listView = findViewById(R.id.serviceListView)
        listView.adapter = adapter
        listView.setOnItemClickListener { adapter, _, position, _ ->
            // Aquí puedes manejar el clic en un servicio
            val selectedService = adapter[position]
            Log.i("detalle", selectedService.toString())
            dismiss()
        }
    }

    // Método para actualizar la lista de servicios
    fun updateServices(listNsdServiceInfo: List<NsdServiceInfo>) {
        adapter.clear()
        adapter.addAll(listNsdServiceInfo.map { it.serviceName })
        adapter.notifyDataSetChanged()
    }
}