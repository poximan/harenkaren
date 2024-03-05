package com.example.demo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.demo.dao.CircuitoDAO
import com.example.demo.dao.ReportDAO
import com.example.demo.model.Censo
import com.example.demo.model.Circuito
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Censo::class, Circuito::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class ReportRoomDatabase : RoomDatabase() {

    abstract fun reportDao(): ReportDAO
    abstract fun circuitoDao(): CircuitoDAO

    companion object {

        @Volatile
        private var INSTANCIA: ReportRoomDatabase? = null

        fun getDatabase(
            context: Context,
            viewModelScope: CoroutineScope
        ): ReportRoomDatabase {
            val instanciaTemporal = INSTANCIA
            if (instanciaTemporal != null) {
                return instanciaTemporal
            }
            synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    ReportRoomDatabase::class.java,
                    "haren_database"
                )
                    .addCallback(ReportDatabaseCallback(viewModelScope))
                    .build()
                INSTANCIA = instancia
                return instancia
            }
        }

        private class ReportDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {

        }
    }
}