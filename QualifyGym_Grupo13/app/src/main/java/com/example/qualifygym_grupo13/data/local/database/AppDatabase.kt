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
import com.example.qualifygym_grupo13.data.local.imagen.ImagenDao
import com.example.qualifygym_grupo13.data.local.imagen.ImagenEntity
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
        ComentarioEntity::class,
        ImagenEntity::class
    ],
    version = 5,
    exportSchema = true // Mantener true para inspección de esquema (útil en educación)
)
abstract class AppDatabase : RoomDatabase() {

    // Exponemos el DAO de usuarios
    abstract fun userDao(): UserDao
    abstract fun estadoDao(): EstadoDao
    abstract fun temaDao(): TemaDao
    abstract fun publicacionDao(): PublicacionDao
    abstract fun comentarioDao(): ComentarioDao
    abstract fun imagenDao(): ImagenDao

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

            // Precarga de usuarios (incluye teléfono e isAdmin)
            val userSeed = listOf(
                UserEntity(
                    name = "Admin",
                    email = "admin@duoc.cl",
                    phone = "+56911111111",
                    password = "Admin123!",
                    isAdmin = true  // Usuario administrador
                ),
                UserEntity(
                    name = "Víctor Rosendo",
                    email = "victor@duoc.cl",
                    phone = "+56922222222",
                    password = "123456",
                    isAdmin = false  // Usuario normal
                )
            )

            // Inserta usuarios sólo si la tabla está vacía
            if (userDao.count() == 0) {
                userSeed.forEach { userDao.insert(it) }
            }

            // Verificar si hay estados, si no, crearlos
            val estadoActivo = estadoDao.getByNombre("Activo")
            val estadoActivoId = if (estadoActivo != null) {
                estadoActivo.id_estado
            } else {
                val nuevoEstadoActivo = EstadoEntity(nombre = "Activo")
                val nuevoEstadoInactivo = EstadoEntity(nombre = "Inactivo")
                val activoId = estadoDao.insert(nuevoEstadoActivo)
                estadoDao.insert(nuevoEstadoInactivo)
                activoId
            }

            // Verificar si hay temas existentes
            val temasExistentes = temaDao.getById(1L)  // Verificar si existe al menos un tema
            
            if (temasExistentes == null) {
                // Precarga de temas solo si no existen
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
                
                temasSeed.forEach { temaDao.insert(it) }
            }
        }
    }

}