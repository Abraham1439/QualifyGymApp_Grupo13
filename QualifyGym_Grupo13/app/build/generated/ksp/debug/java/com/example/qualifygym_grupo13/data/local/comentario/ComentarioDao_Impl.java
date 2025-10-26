package com.example.qualifygym_grupo13.data.local.comentario;

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
public final class ComentarioDao_Impl implements ComentarioDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ComentarioEntity> __insertionAdapterOfComentarioEntity;

  private final EntityDeletionOrUpdateAdapter<ComentarioEntity> __updateAdapterOfComentarioEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public ComentarioDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfComentarioEntity = new EntityInsertionAdapter<ComentarioEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `comentarios` (`id_comentario`,`comentario`,`fecha_registro`,`oculto`,`fecha_baneo`,`motivo_baneo`,`Usuarios_id_usuario`,`Publicacion_id_publicacion`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ComentarioEntity entity) {
        statement.bindLong(1, entity.getId_comentario());
        statement.bindString(2, entity.getComentario());
        statement.bindLong(3, entity.getFecha_registro());
        final int _tmp = entity.getOculto() ? 1 : 0;
        statement.bindLong(4, _tmp);
        if (entity.getFecha_baneo() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getFecha_baneo());
        }
        if (entity.getMotivo_baneo() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getMotivo_baneo());
        }
        statement.bindLong(7, entity.getUsuarios_id_usuario());
        statement.bindLong(8, entity.getPublicacion_id_publicacion());
      }
    };
    this.__updateAdapterOfComentarioEntity = new EntityDeletionOrUpdateAdapter<ComentarioEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `comentarios` SET `id_comentario` = ?,`comentario` = ?,`fecha_registro` = ?,`oculto` = ?,`fecha_baneo` = ?,`motivo_baneo` = ?,`Usuarios_id_usuario` = ?,`Publicacion_id_publicacion` = ? WHERE `id_comentario` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ComentarioEntity entity) {
        statement.bindLong(1, entity.getId_comentario());
        statement.bindString(2, entity.getComentario());
        statement.bindLong(3, entity.getFecha_registro());
        final int _tmp = entity.getOculto() ? 1 : 0;
        statement.bindLong(4, _tmp);
        if (entity.getFecha_baneo() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getFecha_baneo());
        }
        if (entity.getMotivo_baneo() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getMotivo_baneo());
        }
        statement.bindLong(7, entity.getUsuarios_id_usuario());
        statement.bindLong(8, entity.getPublicacion_id_publicacion());
        statement.bindLong(9, entity.getId_comentario());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM comentarios WHERE id_comentario = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ComentarioEntity comentario,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfComentarioEntity.insertAndReturnId(comentario);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ComentarioEntity comentario,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfComentarioEntity.handle(comentario);
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
  public Flow<List<ComentarioEntity>> getByPublicacionId(final long publicacionId) {
    final String _sql = "SELECT * FROM comentarios WHERE Publicacion_id_publicacion = ? ORDER BY fecha_registro DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, publicacionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"comentarios"}, new Callable<List<ComentarioEntity>>() {
      @Override
      @NonNull
      public List<ComentarioEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdComentario = CursorUtil.getColumnIndexOrThrow(_cursor, "id_comentario");
          final int _cursorIndexOfComentario = CursorUtil.getColumnIndexOrThrow(_cursor, "comentario");
          final int _cursorIndexOfFechaRegistro = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_registro");
          final int _cursorIndexOfOculto = CursorUtil.getColumnIndexOrThrow(_cursor, "oculto");
          final int _cursorIndexOfFechaBaneo = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_baneo");
          final int _cursorIndexOfMotivoBaneo = CursorUtil.getColumnIndexOrThrow(_cursor, "motivo_baneo");
          final int _cursorIndexOfUsuariosIdUsuario = CursorUtil.getColumnIndexOrThrow(_cursor, "Usuarios_id_usuario");
          final int _cursorIndexOfPublicacionIdPublicacion = CursorUtil.getColumnIndexOrThrow(_cursor, "Publicacion_id_publicacion");
          final List<ComentarioEntity> _result = new ArrayList<ComentarioEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ComentarioEntity _item;
            final long _tmpId_comentario;
            _tmpId_comentario = _cursor.getLong(_cursorIndexOfIdComentario);
            final String _tmpComentario;
            _tmpComentario = _cursor.getString(_cursorIndexOfComentario);
            final long _tmpFecha_registro;
            _tmpFecha_registro = _cursor.getLong(_cursorIndexOfFechaRegistro);
            final boolean _tmpOculto;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfOculto);
            _tmpOculto = _tmp != 0;
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
            final long _tmpPublicacion_id_publicacion;
            _tmpPublicacion_id_publicacion = _cursor.getLong(_cursorIndexOfPublicacionIdPublicacion);
            _item = new ComentarioEntity(_tmpId_comentario,_tmpComentario,_tmpFecha_registro,_tmpOculto,_tmpFecha_baneo,_tmpMotivo_baneo,_tmpUsuarios_id_usuario,_tmpPublicacion_id_publicacion);
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
  public Flow<List<ComentarioEntity>> getByUserId(final long userId) {
    final String _sql = "SELECT * FROM comentarios WHERE Usuarios_id_usuario = ? ORDER BY fecha_registro DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"comentarios"}, new Callable<List<ComentarioEntity>>() {
      @Override
      @NonNull
      public List<ComentarioEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdComentario = CursorUtil.getColumnIndexOrThrow(_cursor, "id_comentario");
          final int _cursorIndexOfComentario = CursorUtil.getColumnIndexOrThrow(_cursor, "comentario");
          final int _cursorIndexOfFechaRegistro = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_registro");
          final int _cursorIndexOfOculto = CursorUtil.getColumnIndexOrThrow(_cursor, "oculto");
          final int _cursorIndexOfFechaBaneo = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_baneo");
          final int _cursorIndexOfMotivoBaneo = CursorUtil.getColumnIndexOrThrow(_cursor, "motivo_baneo");
          final int _cursorIndexOfUsuariosIdUsuario = CursorUtil.getColumnIndexOrThrow(_cursor, "Usuarios_id_usuario");
          final int _cursorIndexOfPublicacionIdPublicacion = CursorUtil.getColumnIndexOrThrow(_cursor, "Publicacion_id_publicacion");
          final List<ComentarioEntity> _result = new ArrayList<ComentarioEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ComentarioEntity _item;
            final long _tmpId_comentario;
            _tmpId_comentario = _cursor.getLong(_cursorIndexOfIdComentario);
            final String _tmpComentario;
            _tmpComentario = _cursor.getString(_cursorIndexOfComentario);
            final long _tmpFecha_registro;
            _tmpFecha_registro = _cursor.getLong(_cursorIndexOfFechaRegistro);
            final boolean _tmpOculto;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfOculto);
            _tmpOculto = _tmp != 0;
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
            final long _tmpPublicacion_id_publicacion;
            _tmpPublicacion_id_publicacion = _cursor.getLong(_cursorIndexOfPublicacionIdPublicacion);
            _item = new ComentarioEntity(_tmpId_comentario,_tmpComentario,_tmpFecha_registro,_tmpOculto,_tmpFecha_baneo,_tmpMotivo_baneo,_tmpUsuarios_id_usuario,_tmpPublicacion_id_publicacion);
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
  public Object getById(final long id, final Continuation<? super ComentarioEntity> $completion) {
    final String _sql = "SELECT * FROM comentarios WHERE id_comentario = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ComentarioEntity>() {
      @Override
      @Nullable
      public ComentarioEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdComentario = CursorUtil.getColumnIndexOrThrow(_cursor, "id_comentario");
          final int _cursorIndexOfComentario = CursorUtil.getColumnIndexOrThrow(_cursor, "comentario");
          final int _cursorIndexOfFechaRegistro = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_registro");
          final int _cursorIndexOfOculto = CursorUtil.getColumnIndexOrThrow(_cursor, "oculto");
          final int _cursorIndexOfFechaBaneo = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_baneo");
          final int _cursorIndexOfMotivoBaneo = CursorUtil.getColumnIndexOrThrow(_cursor, "motivo_baneo");
          final int _cursorIndexOfUsuariosIdUsuario = CursorUtil.getColumnIndexOrThrow(_cursor, "Usuarios_id_usuario");
          final int _cursorIndexOfPublicacionIdPublicacion = CursorUtil.getColumnIndexOrThrow(_cursor, "Publicacion_id_publicacion");
          final ComentarioEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId_comentario;
            _tmpId_comentario = _cursor.getLong(_cursorIndexOfIdComentario);
            final String _tmpComentario;
            _tmpComentario = _cursor.getString(_cursorIndexOfComentario);
            final long _tmpFecha_registro;
            _tmpFecha_registro = _cursor.getLong(_cursorIndexOfFechaRegistro);
            final boolean _tmpOculto;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfOculto);
            _tmpOculto = _tmp != 0;
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
            final long _tmpPublicacion_id_publicacion;
            _tmpPublicacion_id_publicacion = _cursor.getLong(_cursorIndexOfPublicacionIdPublicacion);
            _result = new ComentarioEntity(_tmpId_comentario,_tmpComentario,_tmpFecha_registro,_tmpOculto,_tmpFecha_baneo,_tmpMotivo_baneo,_tmpUsuarios_id_usuario,_tmpPublicacion_id_publicacion);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
