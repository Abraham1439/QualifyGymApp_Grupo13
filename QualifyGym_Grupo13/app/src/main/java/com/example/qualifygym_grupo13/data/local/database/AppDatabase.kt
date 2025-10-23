package com.example.qualifygym_grupo13.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.qualifygym_grupo13.data.local.user.UserDao
import com.example.qualifygym_grupo13.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true // Mantener true para inspección de esquema (útil en educación)
)
abstract class AppDatabase : RoomDatabase() {

    // Exponemos el DAO de usuarios
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: com.example.qualifygym_grupo13.data.local.database.AppDatabase? = null              // Instancia singleton
        private const val DB_NAME = "ui_navegacion.db"         // Nombre del archivo .db

        // Obtiene la instancia única de la base
        fun getInstance(context: Context): com.example.qualifygym_grupo13.data.local.database.AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Construimos la DB con callback de precarga
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    com.example.qualifygym_grupo13.data.local.database.AppDatabase::class.java,
                    DB_NAME
                )
                    // Callback para ejecutar cuando la DB se crea por primera vez
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Lanzamos una corrutina en IO para insertar datos iniciales
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).userDao()

                                // Precarga de usuarios (incluye teléfono)
                                // Reemplaza aquí por los mismitos datos que usas en Login/Register.
                                val seed = listOf(
                                    UserEntity(
                                        name = "Admin",
                                        email = "admin@duoc.cl",
                                        phone = "+56911111111",
                                        password = "Admin123!"
                                    ),
                                    UserEntity(
                                        name = "Víctor Rosendo",
                                        email = "victor@duoc.cl",
                                        phone = "+56922222222",
                                        password = "123456"
                                    )
                                )

                                // Inserta seed sólo si la tabla está vacía
                                if (dao.count() == 0) {
                                    seed.forEach { dao.insert(it) }
                                }
                            }
                        }
                    })
                    // En entorno educativo, si cambias versión sin migraciones, destruye y recrea.
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance                             // Guarda la instancia
                instance                                        // Devuelve la instancia
            }
        }
    }

}