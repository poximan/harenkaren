package phocidae.mirounga.leonina.database

import android.content.Context
import android.database.Cursor
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import phocidae.mirounga.leonina.dao.DiaDAO
import phocidae.mirounga.leonina.dao.RecorrDAO
import phocidae.mirounga.leonina.dao.UnSocDAO
import phocidae.mirounga.leonina.dao.UsuarioDAO
import phocidae.mirounga.leonina.model.Dia
import phocidae.mirounga.leonina.model.Recorrido
import phocidae.mirounga.leonina.model.UnidSocial
import phocidae.mirounga.leonina.model.Usuario

@Database(
    entities = [Dia::class, Recorrido::class, UnidSocial::class, Usuario::class],
    version = 1,
    exportSchema = false
)
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

        fun deleteDatabase(context: Context, dbName: String) {
            context.deleteDatabase(dbName)
            INSTANCIA = null
        }

        fun isDatabaseSchemaCreated(context: Context): Boolean {
            val db = Room.databaseBuilder(
                context.applicationContext,
                HarenKarenRoomDatabase::class.java,
                "haren_database"
            ).build()

            return try {
                val cursor: Cursor = db.query(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name IN ('dia', 'recorrido', 'unidsocial', 'usuario')",
                    null
                )
                val tableNames = mutableListOf<String>()
                if (cursor.moveToFirst()) {
                    do {
                        tableNames.add(cursor.getString(0))
                    } while (cursor.moveToNext())
                }
                cursor.close()
                tableNames.containsAll(listOf("dia", "recorrido", "unidsocial", "usuario"))
            } catch (e: Exception) {
                false
            } finally {
                db.close()
            }
        }
    }
}