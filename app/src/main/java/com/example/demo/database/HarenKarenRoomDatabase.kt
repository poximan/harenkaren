package com.example.demo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.demo.dao.RecorrDAO
import com.example.demo.dao.UnSocDAO
import com.example.demo.model.UnidSocial
import com.example.demo.model.Recorrido
import kotlinx.coroutines.CoroutineScope

@Database(entities = [UnidSocial::class, Recorrido::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class HarenKarenRoomDatabase : RoomDatabase() {

    abstract fun unSocDao(): UnSocDAO
    abstract fun recorrDao(): RecorrDAO

    companion object {

        @Volatile
        private var INSTANCIA: HarenKarenRoomDatabase? = null

        fun getDatabase(
            context: Context,
            viewModelScope: CoroutineScope
        ): HarenKarenRoomDatabase {
            val instanciaTemporal = INSTANCIA
            if (instanciaTemporal != null) {
                return instanciaTemporal
            }
            synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    HarenKarenRoomDatabase::class.java,
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
        ) : Callback()
    }
}