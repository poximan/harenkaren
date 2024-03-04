package com.example.demo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.demo.dao.CircuitoDAO
import com.example.demo.model.Circuito
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Circuito::class), version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class CircuitoRoomDatabase : RoomDatabase() {

    abstract fun cicuitoDao(): CircuitoDAO

    companion object {

        @Volatile
        private var INSTANCIA: CircuitoRoomDatabase? = null

        fun getDatabase(
            context: Context,
            viewModelScope: CoroutineScope
        ): CircuitoRoomDatabase {
            val instanciaTemporal = INSTANCIA
            if (instanciaTemporal != null) {
                return instanciaTemporal
            }
            synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    CircuitoRoomDatabase::class.java,
                    "circuito_database"
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

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCIA?.let { database ->
                    scope.launch {
//                        populateDatabase(database.reportDao())
                    }
                }
            }
            suspend fun populateDatabase(circuitoDAO: CircuitoDAO) {
                if (circuitoDAO.getCount() == 0) {
                    circuitoDAO.deleteAll()
                }
            }
        }
    }
}