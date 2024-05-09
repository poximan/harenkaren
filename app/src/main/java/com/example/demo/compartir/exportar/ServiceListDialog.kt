package com.example.demo.compartir.exportar

import android.app.Dialog
import android.content.Context
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.demo.R

class ServiceListDialog(context: Context, private val mapNSD: MutableMap<Int, NsdServiceInfo>) :
    Dialog(context) {

    interface ServiceSelectedListener {
        fun onServiceSelected(serviceInfo: NsdServiceInfo)
    }

    private val adapter: ArrayAdapter<String> =
        ArrayAdapter(context, android.R.layout.simple_list_item_1)
    private lateinit var listView: ListView

    private var listener: ServiceSelectedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_service_list)

        listView = findViewById(R.id.serviceListView)
        listView.adapter = adapter
        listView.setOnItemClickListener { parent, _, position, _ ->
            val adapter = parent.adapter

            if (adapter is ArrayAdapter<*>) {
                val item = adapter.getItem(position) as String
                val entrada = mapNSD[item.substringBefore("-").toInt()]!!
                listener?.onServiceSelected(entrada)
                dismiss()
            }
        }
    }

    fun updateServices(listNsdServiceInfo: List<String>) {
        adapter.clear()
        adapter.addAll(listNsdServiceInfo)
        adapter.notifyDataSetChanged()
    }

    fun setServiceSelectedListener(listener: ServiceSelectedListener) {
        this.listener = listener
    }
}