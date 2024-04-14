package com.example.demo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.demo.dao.DiaDAO
import com.example.demo.dao.RecorrDAO
import com.example.demo.dao.UnSocDAO
import com.example.demo.dao.UsuarioDAO
import com.example.demo.model.Dia
import com.example.demo.model.Recorrido
import com.example.demo.model.UnidSocial
import com.example.demo.model.Usuario
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Dia::class, Recorrido::class, UnidSocial::class, Usuario::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class HarenKarenRoomDatabase : RoomDatabase() {

    abstract fun diaDao(): DiaDAO
    abstract fun recorrDao(): RecorrDAO
    abstract fun unSocDao(): UnSocDAO
    abstract fun usuarioDao(): UsuarioDAO

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
                    .addCallback(DatabaseCallback(viewModelScope))
                    .build()
                INSTANCIA = instancia
                return instancia
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback()
    }
}