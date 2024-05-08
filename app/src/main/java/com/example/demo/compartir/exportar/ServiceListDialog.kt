package com.example.demo.compartir.exportar

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.demo.R
import com.example.demo.compartir.NsdHelper

class ServiceListDialog(context: Context) : Dialog(context) {

    interface ServiceSelectedListener {
        fun onServiceSelected(serviceName: String)
    }

    private val adapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_list_item_1)
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
                listener?.onServiceSelected(item)
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