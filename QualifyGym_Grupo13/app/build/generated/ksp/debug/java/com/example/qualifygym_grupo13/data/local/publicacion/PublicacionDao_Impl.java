package com.example.qualifygym_grupo13.data.local.publicacion;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PublicacionDao_Impl implements PublicacionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PublicacionEntity> __insertionAdapterOfPublicacionEntity;

  private final EntityDeletionOrUpdateAdapter<PublicacionEntity> __updateAdapterOfPublicacionEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public PublicacionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPublicacionEntity = new EntityInsertionAdapter<PublicacionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `publicaciones` (`id_publicacion`,`titulo`,`fecha`,`descripcion`,`oculta`,`fecha_baneo`,`motivo_baneo`,`Usuarios_id_usuario`,`Tema_id_tema`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PublicacionEntity entity) {
        statement.bindLong(1, entity.getId_publicacion());
        statement.bindString(2, entity.getTitulo());
        statement.bindLong(3, entity.getFecha());
        statement.bindString(4, entity.getDescripcion());
        final int _tmp = entity.getOculta() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getFecha_baneo() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getFecha_baneo());
        }
        if (entity.getMotivo_baneo() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getMotivo_baneo());
        }
        statement.bindLong(8, entity.getUsuarios_id_usuario());
        statement.bindLong(9, entity.getTema_id_tema());
      }
    };
    this.__updateAdapterOfPublicacionEntity = new EntityDeletionOrUpdateAdapter<PublicacionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `publicaciones` SET `id_publicacion` = ?,`titulo` = ?,`fecha` = ?,`descripcion` = ?,`oculta` = ?,`fecha_baneo` = ?,`motivo_baneo` = ?,`Usuarios_id_usuario` = ?,`Tema_id_tema` = ? WHERE `id_publicacion` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PublicacionEntity entity) {
        statement.bindLong(1, entity.getId_publicacion());
        statement.bindString(2, entity.getTitulo());
        statement.bindLong(3, entity.getFecha());
        statement.bindString(4, entity.getDescripcion());
        final int _tmp = entity.getOculta() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getFecha_baneo() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getFecha_baneo());
        }
        if (entity.getMotivo_baneo() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getMotivo_baneo());
        }
        statement.bindLong(8, entity.getUsuarios_id_usuario());
        statement.bindLong(9, entity.getTema_id_tema());
        statement.bindLong(10, entity.getId_publicacion());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM publicaciones WHERE id_publicacion = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final PublicacionEntity publicacion,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPublicacionEntity.insertAndReturnId(publicacion);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final PublicacionEntity publicacion,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPublicacionEntity.handle(publicacion);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PublicacionEntity>> getAll() {
    final String _sql = "SELECT * FROM publicaciones ORDER BY fecha DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"publicaciones"}, new Callable<List<PublicacionEntity>>() {
      @Override
      @NonNull
      public List<PublicacionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdPublicacion = CursorUtil.getColumnIndexOrThrow(_cursor, "id_publicacion");
          final int _cursorIndexOfTitulo = CursorUtil.getColumnIndexOrThrow(_cursor, "titulo");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final int _cursorIndexOfOculta = CursorUtil.getColumnIndexOrThrow(_cursor, "oculta");
          final int _cursorIndexOfFechaBaneo = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_baneo");
          final int _cursorIndexOfMotivoBaneo = CursorUtil.getColumnIndexOrThrow(_cursor, "motivo_baneo");
          final int _cursorIndexOfUsuariosIdUsuario = CursorUtil.getColumnIndexOrThrow(_cursor, "Usuarios_id_usuario");
          final int _cursorIndexOfTemaIdTema = CursorUtil.getColumnIndexOrThrow(_cursor, "Tema_id_tema");
          final List<PublicacionEntity> _result = new ArrayList<PublicacionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PublicacionEntity _item;
            final long _tmpId_publicacion;
            _tmpId_publicacion = _cursor.getLong(_cursorIndexOfIdPublicacion);
            final String _tmpTitulo;
            _tmpTitulo = _cursor.getString(_cursorIndexOfTitulo);
            final long _tmpFecha;
            _tmpFecha = _cursor.getLong(_cursorIndexOfFecha);
            final String _tmpDescripcion;
            _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            final boolean _tmpOculta;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfOculta);
            _tmpOculta = _tmp != 0;
            final Long _tmpFecha_baneo;
            if (_cursor.isNull(_cursorIndexOfFechaBaneo)) {
              _tmpFecha_baneo = null;
            } else {
              _tmpFecha_baneo = _cursor.getLong(_cursorIndexOfFechaBaneo);
            }
            final String _tmpMotivo_baneo;
            if (_cursor.isNull(_cursorIndexOfMotivoBaneo)) {
              _tmpMotivo_baneo = null;
            } else {
              _tmpMotivo_baneo = _cursor.getString(_cursorIndexOfMotivoBaneo);
            }
            final long _tmpUsuarios_id_usuario;
            _tmpUsuarios_id_usuario = _cursor.getLong(_cursorIndexOfUsuariosIdUsuario);
            final long _tmpTema_id_tema;
            _tmpTema_id_tema = _cursor.getLong(_cursorIndexOfTemaIdTema);
            _item = new PublicacionEntity(_tmpId_publicacion,_tmpTitulo,_tmpFecha,_tmpDescripcion,_tmpOculta,_tmpFecha_baneo,_tmpMotivo_baneo,_tmpUsuarios_id_usuario,_tmpTema_id_tema);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final long id, final Continuation<? super PublicacionEntity> $completion) {
    final String _sql = "SELECT * FROM publicaciones WHERE id_publicacion = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PublicacionEntity>() {
      @Override
      @Nullable
      public PublicacionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdPublicacion = CursorUtil.getColumnIndexOrThrow(_cursor, "id_publicacion");
          final int _cursorIndexOfTitulo = CursorUtil.getColumnIndexOrThrow(_cursor, "titulo");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final int _cursorIndexOfOculta = CursorUtil.getColumnIndexOrThrow(_cursor, "oculta");
          final int _cursorIndexOfFechaBaneo = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_baneo");
          final int _cursorIndexOfMotivoBaneo = CursorUtil.getColumnIndexOrThrow(_cursor, "motivo_baneo");
          final int _cursorIndexOfUsuariosIdUsuario = CursorUtil.getColumnIndexOrThrow(_cursor, "Usuarios_id_usuario");
          final int _cursorIndexOfTemaIdTema = CursorUtil.getColumnIndexOrThrow(_cursor, "Tema_id_tema");
          final PublicacionEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId_publicacion;
            _tmpId_publicacion = _cursor.getLong(_cursorIndexOfIdPublicacion);
            final String _tmpTitulo;
            _tmpTitulo = _cursor.getString(_cursorIndexOfTitulo);
            final long _tmpFecha;
            _tmpFecha = _cursor.getLong(_cursorIndexOfFecha);
            final String _tmpDescripcion;
            _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            final boolean _tmpOculta;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfOculta);
            _tmpOculta = _tmp != 0;
            final Long _tmpFecha_baneo;
            if (_cursor.isNull(_cursorIndexOfFechaBaneo)) {
              _tmpFecha_baneo = null;
            } else {
              _tmpFecha_baneo = _cursor.getLong(_cursorIndexOfFechaBaneo);
            }
            final String _tmpMotivo_baneo;
            if (_cursor.isNull(_cursorIndexOfMotivoBaneo)) {
              _tmpMotivo_baneo = null;
            } else {
              _tmpMotivo_baneo = _cursor.getString(_cursorIndexOfMotivoBaneo);
            }
            final long _tmpUsuarios_id_usuario;
            _tmpUsuarios_id_usuario = _cursor.getLong(_cursorIndexOfUsuariosIdUsuario);
            final long _tmpTema_id_tema;
            _tmpTema_id_tema = _cursor.getLong(_cursorIndexOfTemaIdTema);
            _result = new PublicacionEntity(_tmpId_publicacion,_tmpTitulo,_tmpFecha,_tmpDescripcion,_tmpOculta,_tmpFecha_baneo,_tmpMotivo_baneo,_tmpUsuarios_id_usuario,_tmpTema_id_tema);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PublicacionEntity>> getByUserId(final long userId) {
    final String _sql = "SELECT * FROM publicaciones WHERE Usuarios_id_usuario = ? ORDER BY fecha DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"publicaciones"}, new Callable<List<PublicacionEntity>>() {
      @Override
      @NonNull
      public List<PublicacionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdPublicacion = CursorUtil.getColumnIndexOrThrow(_cursor, "id_publicacion");
          final int _cursorIndexOfTitulo = CursorUtil.getColumnIndexOrThrow(_cursor, "titulo");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final int _cursorIndexOfOculta = CursorUtil.getColumnIndexOrThrow(_cursor, "oculta");
          final int _cursorIndexOfFechaBaneo = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_baneo");
          final int _cursorIndexOfMotivoBaneo = CursorUtil.getColumnIndexOrThrow(_cursor, "motivo_baneo");
          final int _cursorIndexOfUsuariosIdUsuario = CursorUtil.getColumnIndexOrThrow(_cursor, "Usuarios_id_usuario");
          final int _cursorIndexOfTemaIdTema = CursorUtil.getColumnIndexOrThrow(_cursor, "Tema_id_tema");
          final List<PublicacionEntity> _result = new ArrayList<PublicacionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PublicacionEntity _item;
            final long _tmpId_publicacion;
            _tmpId_publicacion = _cursor.getLong(_cursorIndexOfIdPublicacion);
            final String _tmpTitulo;
            _tmpTitulo = _cursor.getString(_cursorIndexOfTitulo);
            final long _tmpFecha;
            _tmpFecha = _cursor.getLong(_cursorIndexOfFecha);
            final String _tmpDescripcion;
            _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            final boolean _tmpOculta;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfOculta);
            _tmpOculta = _tmp != 0;
            final Long _tmpFecha_baneo;
            if (_cursor.isNull(_cursorIndexOfFechaBaneo)) {
              _tmpFecha_baneo = null;
            } else {
              _tmpFecha_baneo = _cursor.getLong(_cursorIndexOfFechaBaneo);
            }
            final String _tmpMotivo_baneo;
            if (_cursor.isNull(_cursorIndexOfMotivoBaneo)) {
              _tmpMotivo_baneo = null;
            } else {
              _tmpMotivo_baneo = _cursor.getString(_cursorIndexOfMotivoBaneo);
            }
            final long _tmpUsuarios_id_usuario;
            _tmpUsuarios_id_usuario = _cursor.getLong(_cursorIndexOfUsuariosIdUsuario);
            final long _tmpTema_id_tema;
            _tmpTema_id_tema = _cursor.getLong(_cursorIndexOfTemaIdTema);
            _item = new PublicacionEntity(_tmpId_publicacion,_tmpTitulo,_tmpFecha,_tmpDescripcion,_tmpOculta,_tmpFecha_baneo,_tmpMotivo_baneo,_tmpUsuarios_id_usuario,_tmpTema_id_tema);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<PublicacionEntity>> getByTemaId(final long temaId) {
    final String _sql = "SELECT * FROM publicaciones WHERE Tema_id_tema = ? ORDER BY fecha DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, temaId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"publicaciones"}, new Callable<List<PublicacionEntity>>() {
      @Override
      @NonNull
      public List<PublicacionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdPublicacion = CursorUtil.getColumnIndexOrThrow(_cursor, "id_publicacion");
          final int _cursorIndexOfTitulo = CursorUtil.getColumnIndexOrThrow(_cursor, "titulo");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final int _cursorIndexOfOculta = CursorUtil.getColumnIndexOrThrow(_cursor, "oculta");
          final int _cursorIndexOfFechaBaneo = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_baneo");
          final int _cursorIndexOfMotivoBaneo = CursorUtil.getColumnIndexOrThrow(_cursor, "motivo_baneo");
          final int _cursorIndexOfUsuariosIdUsuario = CursorUtil.getColumnIndexOrThrow(_cursor, "Usuarios_id_usuario");
          final int _cursorIndexOfTemaIdTema = CursorUtil.getColumnIndexOrThrow(_cursor, "Tema_id_tema");
          final List<PublicacionEntity> _result = new ArrayList<PublicacionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PublicacionEntity _item;
            final long _tmpId_publicacion;
            _tmpId_publicacion = _cursor.getLong(_cursorIndexOfIdPublicacion);
            final String _tmpTitulo;
            _tmpTitulo = _cursor.getString(_cursorIndexOfTitulo);
            final long _tmpFecha;
            _tmpFecha = _cursor.getLong(_cursorIndexOfFecha);
            final String _tmpDescripcion;
            _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            final boolean _tmpOculta;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfOculta);
            _tmpOculta = _tmp != 0;
            final Long _tmpFecha_baneo;
            if (_cursor.isNull(_cursorIndexOfFechaBaneo)) {
              _tmpFecha_baneo = null;
            } else {
              _tmpFecha_baneo = _cursor.getLong(_cursorIndexOfFechaBaneo);
            }
            final String _tmpMotivo_baneo;
            if (_cursor.isNull(_cursorIndexOfMotivoBaneo)) {
              _tmpMotivo_baneo = null;
            } else {
              _tmpMotivo_baneo = _cursor.getString(_cursorIndexOfMotivoBaneo);
            }
            final long _tmpUsuarios_id_usuario;
            _tmpUsuarios_id_usuario = _cursor.getLong(_cursorIndexOfUsuariosIdUsuario);
            final long _tmpTema_id_tema;
            _tmpTema_id_tema = _cursor.getLong(_cursorIndexOfTemaIdTema);
            _item = new PublicacionEntity(_tmpId_publicacion,_tmpTitulo,_tmpFecha,_tmpDescripcion,_tmpOculta,_tmpFecha_baneo,_tmpMotivo_baneo,_tmpUsuarios_id_usuario,_tmpTema_id_tema);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<PublicacionEntity>> search(final String query) {
    final String _sql = "SELECT * FROM publicaciones WHERE titulo LIKE '%' || ? || '%' OR descripcion LIKE '%' || ? || '%' ORDER BY fecha DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"publicaciones"}, new Callable<List<PublicacionEntity>>() {
      @Override
      @NonNull
      public List<PublicacionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdPublicacion = CursorUtil.getColumnIndexOrThrow(_cursor, "id_publicacion");
          final int _cursorIndexOfTitulo = CursorUtil.getColumnIndexOrThrow(_cursor, "titulo");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final int _cursorIndexOfOculta = CursorUtil.getColumnIndexOrThrow(_cursor, "oculta");
          final int _cursorIndexOfFechaBaneo = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_baneo");
          final int _cursorIndexOfMotivoBaneo = CursorUtil.getColumnIndexOrThrow(_cursor, "motivo_baneo");
          final int _cursorIndexOfUsuariosIdUsuario = CursorUtil.getColumnIndexOrThrow(_cursor, "Usuarios_id_usuario");
          final int _cursorIndexOfTemaIdTema = CursorUtil.getColumnIndexOrThrow(_cursor, "Tema_id_tema");
          final List<PublicacionEntity> _result = new ArrayList<PublicacionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PublicacionEntity _item;
            final long _tmpId_publicacion;
            _tmpId_publicacion = _cursor.getLong(_cursorIndexOfIdPublicacion);
            final String _tmpTitulo;
            _tmpTitulo = _cursor.getString(_cursorIndexOfTitulo);
            final long _tmpFecha;
            _tmpFecha = _cursor.getLong(_cursorIndexOfFecha);
            final String _tmpDescripcion;
            _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            final boolean _tmpOculta;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfOculta);
            _tmpOculta = _tmp != 0;
            final Long _tmpFecha_baneo;
            if (_cursor.isNull(_cursorIndexOfFechaBaneo)) {
              _tmpFecha_baneo = null;
            } else {
              _tmpFecha_baneo = _cursor.getLong(_cursorIndexOfFechaBaneo);
            }
            final String _tmpMotivo_baneo;
            if (_cursor.isNull(_cursorIndexOfMotivoBaneo)) {
              _tmpMotivo_baneo = null;
            } else {
              _tmpMotivo_baneo = _cursor.getString(_cursorIndexOfMotivoBaneo);
            }
            final long _tmpUsuarios_id_usuario;
            _tmpUsuarios_id_usuario = _cursor.getLong(_cursorIndexOfUsuariosIdUsuario);
            final long _tmpTema_id_tema;
            _tmpTema_id_tema = _cursor.getLong(_cursorIndexOfTemaIdTema);
            _item = new PublicacionEntity(_tmpId_publicacion,_tmpTitulo,_tmpFecha,_tmpDescripcion,_tmpOculta,_tmpFecha_baneo,_tmpMotivo_baneo,_tmpUsuarios_id_usuario,_tmpTema_id_tema);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
