package com.example.demo.fragment.list

import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.fragment.app.Fragment
import com.example.demo.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

abstract class SuperList : Fragment() {

    protected fun filtrar() {

        val contextazo = requireContext()
        val c = Calendar.getInstance()

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            contextazo,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDayOfMonth)
                }
                val dateFormat = SimpleDateFormat(
                    contextazo.getString(R.string.formato_dia),
                    Locale.getDefault()
                )
                val dateSelected = dateFormat.format(selectedDate.time)
                loadListWithDate(dateSelected)
            },
            year, month, day
        )
        dpd.show()
    }

    abstract fun loadListWithDate(date: String)

    protected fun getCurrentDate(): String {
        val formato = requireContext().resources.getString(R.string.formato_dia)
        return SimpleDateFormat(formato).format(Date())
    }
}