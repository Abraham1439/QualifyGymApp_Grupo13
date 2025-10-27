package com.example.qualifygym_grupo13.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.qualifygym_grupo13.data.local.comentario.ComentarioDao
import com.example.qualifygym_grupo13.data.local.comentario.ComentarioEntity
import com.example.qualifygym_grupo13.data.local.estado.EstadoDao
import com.example.qualifygym_grupo13.data.local.estado.EstadoEntity
import com.example.qualifygym_grupo13.data.local.publicacion.PublicacionDao
import com.example.qualifygym_grupo13.data.local.publicacion.PublicacionEntity
import com.example.qualifygym_grupo13.data.local.tema.TemaDao
import com.example.qualifygym_grupo13.data.local.tema.TemaEntity
import com.example.qualifygym_grupo13.data.local.user.UserDao
import com.example.qualifygym_grupo13.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        EstadoEntity::class,
        TemaEntity::class,
        PublicacionEntity::class,
        ComentarioEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun estadoDao(): EstadoDao
    abstract fun temaDao(): TemaDao
    abstract fun publicacionDao(): PublicacionDao
    abstract fun comentarioDao(): ComentarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "ui_navegacion.db"

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
                                initializeDatabase(context)
                            }
                        }
                        
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            // Solo verificar la integridad de la base de datos, no reinsertar datos
                            // La inicialización solo debe ocurrir en onCreate
                        }
                    })
                    // En entorno educativo, si cambias versión sin migraciones, destruye y recrea.
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance                             // Guarda la instancia
                instance                                        // Devuelve la instancia
            }
        }
        
        // Función privada para inicializar datos
        private suspend fun initializeDatabase(context: Context) {
            val database = getInstance(context)
            val userDao = database.userDao()
            val estadoDao = database.estadoDao()
            val temaDao = database.temaDao()

            val userSeed = listOf(
                UserEntity(
                    name = "Admin",
                    email = "admin@duoc.cl",
                    phone = "+56911111111",
                    password = "Admin123!",
                    isAdmin = true
                ),
                UserEntity(
                    name = "Víctor Rosendo",
                    email = "victor@duoc.cl",
                    phone = "+56922222222",
                    password = "123456",
                    isAdmin = false
                )
            )

            try {
                if (userDao.count() == 0) {
                    userSeed.forEach { user ->
                        try {
                            userDao.insert(user)
                        } catch (e: Exception) {
                            // Ignorar errores de inserción individual
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignorar errores de conteo
            }

            val estadoActivoId = try {
                val estadoActivo = estadoDao.getByNombre("Activo")
                if (estadoActivo != null) {
                    estadoActivo.id_estado
                } else {
                    val nuevoEstadoActivo = EstadoEntity(nombre = "Activo")
                    val nuevoEstadoInactivo = EstadoEntity(nombre = "Inactivo")
                    val activoId = estadoDao.insert(nuevoEstadoActivo)
                    estadoDao.insert(nuevoEstadoInactivo)
                    activoId
                }
            } catch (e: Exception) {
                1L // Valor por defecto si hay error
            }

            try {
                val temasExistentes = temaDao.getById(1L)
                if (temasExistentes == null) {
                    val temasSeed = listOf(
                        TemaEntity(
                            nombre_tema = "Rutinas de Fuerza",
                            Estado_id_estado = estadoActivoId
                        ),
                        TemaEntity(
                            nombre_tema = "Nutrición y Suplementos",
                            Estado_id_estado = estadoActivoId
                        ),
                        TemaEntity(
                            nombre_tema = "Cardio y Resistencia",
                            Estado_id_estado = estadoActivoId
                        ),
                        TemaEntity(
                            nombre_tema = "Pérdida de Peso",
                            Estado_id_estado = estadoActivoId
                        ),
                        TemaEntity(
                            nombre_tema = "Ganancia Muscular",
                            Estado_id_estado = estadoActivoId
                        )
                    )
                    
                    temasSeed.forEach { tema ->
                        try {
                            temaDao.insert(tema)
                        } catch (e: Exception) {
                            // Ignorar errores de inserción individual
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignorar errores de inicialización de temas
            }
        }
    }

}