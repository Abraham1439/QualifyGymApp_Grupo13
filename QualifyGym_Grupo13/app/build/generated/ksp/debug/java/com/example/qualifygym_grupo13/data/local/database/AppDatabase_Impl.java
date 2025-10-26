package com.example.qualifygym_grupo13.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.example.qualifygym_grupo13.data.local.comentario.ComentarioDao;
import com.example.qualifygym_grupo13.data.local.comentario.ComentarioDao_Impl;
import com.example.qualifygym_grupo13.data.local.estado.EstadoDao;
import com.example.qualifygym_grupo13.data.local.estado.EstadoDao_Impl;
import com.example.qualifygym_grupo13.data.local.imagen.ImagenDao;
import com.example.qualifygym_grupo13.data.local.imagen.ImagenDao_Impl;
import com.example.qualifygym_grupo13.data.local.publicacion.PublicacionDao;
import com.example.qualifygym_grupo13.data.local.publicacion.PublicacionDao_Impl;
import com.example.qualifygym_grupo13.data.local.tema.TemaDao;
import com.example.qualifygym_grupo13.data.local.tema.TemaDao_Impl;
import com.example.qualifygym_grupo13.data.local.user.UserDao;
import com.example.qualifygym_grupo13.data.local.user.UserDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile UserDao _userDao;

  private volatile EstadoDao _estadoDao;

  private volatile TemaDao _temaDao;

  private volatile PublicacionDao _publicacionDao;

  private volatile ComentarioDao _comentarioDao;

  private volatile ImagenDao _imagenDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `email` TEXT NOT NULL, `phone` TEXT NOT NULL, `password` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `estados` (`id_estado` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombre` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `temas` (`id_tema` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombre_tema` TEXT NOT NULL, `Estado_id_estado` INTEGER NOT NULL, FOREIGN KEY(`Estado_id_estado`) REFERENCES `estados`(`id_estado`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_temas_Estado_id_estado` ON `temas` (`Estado_id_estado`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `publicaciones` (`id_publicacion` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `titulo` TEXT NOT NULL, `fecha` INTEGER NOT NULL, `descripcion` TEXT NOT NULL, `fecha_baneo` INTEGER, `motivo_baneo` TEXT, `Usuarios_id_usuario` INTEGER NOT NULL, `Tema_id_tema` INTEGER NOT NULL, FOREIGN KEY(`Usuarios_id_usuario`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`Tema_id_tema`) REFERENCES `temas`(`id_tema`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_publicaciones_Usuarios_id_usuario` ON `publicaciones` (`Usuarios_id_usuario`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_publicaciones_Tema_id_tema` ON `publicaciones` (`Tema_id_tema`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `comentarios` (`id_comentario` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `comentario` TEXT NOT NULL, `fecha_registro` INTEGER NOT NULL, `fecha_baneo` INTEGER, `motivo_baneo` TEXT, `Usuarios_id_usuario` INTEGER NOT NULL, `Publicacion_id_publicacion` INTEGER NOT NULL, FOREIGN KEY(`Usuarios_id_usuario`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`Publicacion_id_publicacion`) REFERENCES `publicaciones`(`id_publicacion`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_comentarios_Usuarios_id_usuario` ON `comentarios` (`Usuarios_id_usuario`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_comentarios_Publicacion_id_publicacion` ON `comentarios` (`Publicacion_id_publicacion`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `imagenes` (`id_imagen` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombre_imagen` TEXT NOT NULL, `imagen` BLOB NOT NULL, `Publicacion_id_publicacion` INTEGER NOT NULL, `Estado_id_estado` INTEGER NOT NULL, FOREIGN KEY(`Publicacion_id_publicacion`) REFERENCES `publicaciones`(`id_publicacion`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`Estado_id_estado`) REFERENCES `estados`(`id_estado`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_imagenes_Publicacion_id_publicacion` ON `imagenes` (`Publicacion_id_publicacion`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_imagenes_Estado_id_estado` ON `imagenes` (`Estado_id_estado`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'fb95a0d8346b38a59a4a5af926f1f874')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `users`");
        db.execSQL("DROP TABLE IF EXISTS `estados`");
        db.execSQL("DROP TABLE IF EXISTS `temas`");
        db.execSQL("DROP TABLE IF EXISTS `publicaciones`");
        db.execSQL("DROP TABLE IF EXISTS `comentarios`");
        db.execSQL("DROP TABLE IF EXISTS `imagenes`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUsers = new HashMap<String, TableInfo.Column>(5);
        _columnsUsers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("email", new TableInfo.Column("email", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("phone", new TableInfo.Column("phone", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("password", new TableInfo.Column("password", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsers = new TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers);
        final TableInfo _existingUsers = TableInfo.read(db, "users");
        if (!_infoUsers.equals(_existingUsers)) {
          return new RoomOpenHelper.ValidationResult(false, "users(com.example.qualifygym_grupo13.data.local.user.UserEntity).\n"
                  + " Expected:\n" + _infoUsers + "\n"
                  + " Found:\n" + _existingUsers);
        }
        final HashMap<String, TableInfo.Column> _columnsEstados = new HashMap<String, TableInfo.Column>(2);
        _columnsEstados.put("id_estado", new TableInfo.Column("id_estado", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEstados.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEstados = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEstados = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEstados = new TableInfo("estados", _columnsEstados, _foreignKeysEstados, _indicesEstados);
        final TableInfo _existingEstados = TableInfo.read(db, "estados");
        if (!_infoEstados.equals(_existingEstados)) {
          return new RoomOpenHelper.ValidationResult(false, "estados(com.example.qualifygym_grupo13.data.local.estado.EstadoEntity).\n"
                  + " Expected:\n" + _infoEstados + "\n"
                  + " Found:\n" + _existingEstados);
        }
        final HashMap<String, TableInfo.Column> _columnsTemas = new HashMap<String, TableInfo.Column>(3);
        _columnsTemas.put("id_tema", new TableInfo.Column("id_tema", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTemas.put("nombre_tema", new TableInfo.Column("nombre_tema", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTemas.put("Estado_id_estado", new TableInfo.Column("Estado_id_estado", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTemas = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysTemas.add(new TableInfo.ForeignKey("estados", "CASCADE", "NO ACTION", Arrays.asList("Estado_id_estado"), Arrays.asList("id_estado")));
        final HashSet<TableInfo.Index> _indicesTemas = new HashSet<TableInfo.Index>(1);
        _indicesTemas.add(new TableInfo.Index("index_temas_Estado_id_estado", false, Arrays.asList("Estado_id_estado"), Arrays.asList("ASC")));
        final TableInfo _infoTemas = new TableInfo("temas", _columnsTemas, _foreignKeysTemas, _indicesTemas);
        final TableInfo _existingTemas = TableInfo.read(db, "temas");
        if (!_infoTemas.equals(_existingTemas)) {
          return new RoomOpenHelper.ValidationResult(false, "temas(com.example.qualifygym_grupo13.data.local.tema.TemaEntity).\n"
                  + " Expected:\n" + _infoTemas + "\n"
                  + " Found:\n" + _existingTemas);
        }
        final HashMap<String, TableInfo.Column> _columnsPublicaciones = new HashMap<String, TableInfo.Column>(8);
        _columnsPublicaciones.put("id_publicacion", new TableInfo.Column("id_publicacion", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPublicaciones.put("titulo", new TableInfo.Column("titulo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPublicaciones.put("fecha", new TableInfo.Column("fecha", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPublicaciones.put("descripcion", new TableInfo.Column("descripcion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPublicaciones.put("fecha_baneo", new TableInfo.Column("fecha_baneo", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPublicaciones.put("motivo_baneo", new TableInfo.Column("motivo_baneo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPublicaciones.put("Usuarios_id_usuario", new TableInfo.Column("Usuarios_id_usuario", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPublicaciones.put("Tema_id_tema", new TableInfo.Column("Tema_id_tema", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPublicaciones = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysPublicaciones.add(new TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", Arrays.asList("Usuarios_id_usuario"), Arrays.asList("id")));
        _foreignKeysPublicaciones.add(new TableInfo.ForeignKey("temas", "CASCADE", "NO ACTION", Arrays.asList("Tema_id_tema"), Arrays.asList("id_tema")));
        final HashSet<TableInfo.Index> _indicesPublicaciones = new HashSet<TableInfo.Index>(2);
        _indicesPublicaciones.add(new TableInfo.Index("index_publicaciones_Usuarios_id_usuario", false, Arrays.asList("Usuarios_id_usuario"), Arrays.asList("ASC")));
        _indicesPublicaciones.add(new TableInfo.Index("index_publicaciones_Tema_id_tema", false, Arrays.asList("Tema_id_tema"), Arrays.asList("ASC")));
        final TableInfo _infoPublicaciones = new TableInfo("publicaciones", _columnsPublicaciones, _foreignKeysPublicaciones, _indicesPublicaciones);
        final TableInfo _existingPublicaciones = TableInfo.read(db, "publicaciones");
        if (!_infoPublicaciones.equals(_existingPublicaciones)) {
          return new RoomOpenHelper.ValidationResult(false, "publicaciones(com.example.qualifygym_grupo13.data.local.publicacion.PublicacionEntity).\n"
                  + " Expected:\n" + _infoPublicaciones + "\n"
                  + " Found:\n" + _existingPublicaciones);
        }
        final HashMap<String, TableInfo.Column> _columnsComentarios = new HashMap<String, TableInfo.Column>(7);
        _columnsComentarios.put("id_comentario", new TableInfo.Column("id_comentario", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComentarios.put("comentario", new TableInfo.Column("comentario", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComentarios.put("fecha_registro", new TableInfo.Column("fecha_registro", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComentarios.put("fecha_baneo", new TableInfo.Column("fecha_baneo", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComentarios.put("motivo_baneo", new TableInfo.Column("motivo_baneo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComentarios.put("Usuarios_id_usuario", new TableInfo.Column("Usuarios_id_usuario", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComentarios.put("Publicacion_id_publicacion", new TableInfo.Column("Publicacion_id_publicacion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysComentarios = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysComentarios.add(new TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", Arrays.asList("Usuarios_id_usuario"), Arrays.asList("id")));
        _foreignKeysComentarios.add(new TableInfo.ForeignKey("publicaciones", "CASCADE", "NO ACTION", Arrays.asList("Publicacion_id_publicacion"), Arrays.asList("id_publicacion")));
        final HashSet<TableInfo.Index> _indicesComentarios = new HashSet<TableInfo.Index>(2);
        _indicesComentarios.add(new TableInfo.Index("index_comentarios_Usuarios_id_usuario", false, Arrays.asList("Usuarios_id_usuario"), Arrays.asList("ASC")));
        _indicesComentarios.add(new TableInfo.Index("index_comentarios_Publicacion_id_publicacion", false, Arrays.asList("Publicacion_id_publicacion"), Arrays.asList("ASC")));
        final TableInfo _infoComentarios = new TableInfo("comentarios", _columnsComentarios, _foreignKeysComentarios, _indicesComentarios);
        final TableInfo _existingComentarios = TableInfo.read(db, "comentarios");
        if (!_infoComentarios.equals(_existingComentarios)) {
          return new RoomOpenHelper.ValidationResult(false, "comentarios(com.example.qualifygym_grupo13.data.local.comentario.ComentarioEntity).\n"
                  + " Expected:\n" + _infoComentarios + "\n"
                  + " Found:\n" + _existingComentarios);
        }
        final HashMap<String, TableInfo.Column> _columnsImagenes = new HashMap<String, TableInfo.Column>(5);
        _columnsImagenes.put("id_imagen", new TableInfo.Column("id_imagen", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsImagenes.put("nombre_imagen", new TableInfo.Column("nombre_imagen", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsImagenes.put("imagen", new TableInfo.Column("imagen", "BLOB", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsImagenes.put("Publicacion_id_publicacion", new TableInfo.Column("Publicacion_id_publicacion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsImagenes.put("Estado_id_estado", new TableInfo.Column("Estado_id_estado", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysImagenes = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysImagenes.add(new TableInfo.ForeignKey("publicaciones", "CASCADE", "NO ACTION", Arrays.asList("Publicacion_id_publicacion"), Arrays.asList("id_publicacion")));
        _foreignKeysImagenes.add(new TableInfo.ForeignKey("estados", "CASCADE", "NO ACTION", Arrays.asList("Estado_id_estado"), Arrays.asList("id_estado")));
        final HashSet<TableInfo.Index> _indicesImagenes = new HashSet<TableInfo.Index>(2);
        _indicesImagenes.add(new TableInfo.Index("index_imagenes_Publicacion_id_publicacion", false, Arrays.asList("Publicacion_id_publicacion"), Arrays.asList("ASC")));
        _indicesImagenes.add(new TableInfo.Index("index_imagenes_Estado_id_estado", false, Arrays.asList("Estado_id_estado"), Arrays.asList("ASC")));
        final TableInfo _infoImagenes = new TableInfo("imagenes", _columnsImagenes, _foreignKeysImagenes, _indicesImagenes);
        final TableInfo _existingImagenes = TableInfo.read(db, "imagenes");
        if (!_infoImagenes.equals(_existingImagenes)) {
          return new RoomOpenHelper.ValidationResult(false, "imagenes(com.example.qualifygym_grupo13.data.local.imagen.ImagenEntity).\n"
                  + " Expected:\n" + _infoImagenes + "\n"
                  + " Found:\n" + _existingImagenes);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "fb95a0d8346b38a59a4a5af926f1f874", "599a546ead6501336b0518682f59cce4");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "users","estados","temas","publicaciones","comentarios","imagenes");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `users`");
      _db.execSQL("DELETE FROM `estados`");
      _db.execSQL("DELETE FROM `temas`");
      _db.execSQL("DELETE FROM `publicaciones`");
      _db.execSQL("DELETE FROM `comentarios`");
      _db.execSQL("DELETE FROM `imagenes`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EstadoDao.class, EstadoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TemaDao.class, TemaDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PublicacionDao.class, PublicacionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ComentarioDao.class, ComentarioDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ImagenDao.class, ImagenDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public EstadoDao estadoDao() {
    if (_estadoDao != null) {
      return _estadoDao;
    } else {
      synchronized(this) {
        if(_estadoDao == null) {
          _estadoDao = new EstadoDao_Impl(this);
        }
        return _estadoDao;
      }
    }
  }

  @Override
  public TemaDao temaDao() {
    if (_temaDao != null) {
      return _temaDao;
    } else {
      synchronized(this) {
        if(_temaDao == null) {
          _temaDao = new TemaDao_Impl(this);
        }
        return _temaDao;
      }
    }
  }

  @Override
  public PublicacionDao publicacionDao() {
    if (_publicacionDao != null) {
      return _publicacionDao;
    } else {
      synchronized(this) {
        if(_publicacionDao == null) {
          _publicacionDao = new PublicacionDao_Impl(this);
        }
        return _publicacionDao;
      }
    }
  }

  @Override
  public ComentarioDao comentarioDao() {
    if (_comentarioDao != null) {
      return _comentarioDao;
    } else {
      synchronized(this) {
        if(_comentarioDao == null) {
          _comentarioDao = new ComentarioDao_Impl(this);
        }
        return _comentarioDao;
      }
    }
  }

  @Override
  public ImagenDao imagenDao() {
    if (_imagenDao != null) {
      return _imagenDao;
    } else {
      synchronized(this) {
        if(_imagenDao == null) {
          _imagenDao = new ImagenDao_Impl(this);
        }
        return _imagenDao;
      }
    }
  }
}
