package com.example.qualifygym_grupo13.data.local.estado;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
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
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class EstadoDao_Impl implements EstadoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EstadoEntity> __insertionAdapterOfEstadoEntity;

  public EstadoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEstadoEntity = new EntityInsertionAdapter<EstadoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `estados` (`id_estado`,`nombre`) VALUES (nullif(?, 0),?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EstadoEntity entity) {
        statement.bindLong(1, entity.getId_estado());
        statement.bindString(2, entity.getNombre());
      }
    };
  }

  @Override
  public Object insert(final EstadoEntity estado, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfEstadoEntity.insertAndReturnId(estado);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EstadoEntity>> getAll() {
    final String _sql = "SELECT * FROM estados";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"estados"}, new Callable<List<EstadoEntity>>() {
      @Override
      @NonNull
      public List<EstadoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "id_estado");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final List<EstadoEntity> _result = new ArrayList<EstadoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EstadoEntity _item;
            final long _tmpId_estado;
            _tmpId_estado = _cursor.getLong(_cursorIndexOfIdEstado);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            _item = new EstadoEntity(_tmpId_estado,_tmpNombre);
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
  public Object getById(final long id, final Continuation<? super EstadoEntity> $completion) {
    final String _sql = "SELECT * FROM estados WHERE id_estado = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EstadoEntity>() {
      @Override
      @Nullable
      public EstadoEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "id_estado");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final EstadoEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId_estado;
            _tmpId_estado = _cursor.getLong(_cursorIndexOfIdEstado);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            _result = new EstadoEntity(_tmpId_estado,_tmpNombre);
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
  public Object getByNombre(final String nombre,
      final Continuation<? super EstadoEntity> $completion) {
    final String _sql = "SELECT * FROM estados WHERE nombre = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, nombre);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EstadoEntity>() {
      @Override
      @Nullable
      public EstadoEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "id_estado");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final EstadoEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId_estado;
            _tmpId_estado = _cursor.getLong(_cursorIndexOfIdEstado);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            _result = new EstadoEntity(_tmpId_estado,_tmpNombre);
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
