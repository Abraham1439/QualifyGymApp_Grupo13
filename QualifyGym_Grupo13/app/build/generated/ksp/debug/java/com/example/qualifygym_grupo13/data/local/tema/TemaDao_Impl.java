package com.example.qualifygym_grupo13.data.local.tema;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
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
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TemaDao_Impl implements TemaDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TemaEntity> __insertionAdapterOfTemaEntity;

  private final EntityDeletionOrUpdateAdapter<TemaEntity> __updateAdapterOfTemaEntity;

  public TemaDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTemaEntity = new EntityInsertionAdapter<TemaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `temas` (`id_tema`,`nombre_tema`,`Estado_id_estado`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TemaEntity entity) {
        statement.bindLong(1, entity.getId_tema());
        statement.bindString(2, entity.getNombre_tema());
        statement.bindLong(3, entity.getEstado_id_estado());
      }
    };
    this.__updateAdapterOfTemaEntity = new EntityDeletionOrUpdateAdapter<TemaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `temas` SET `id_tema` = ?,`nombre_tema` = ?,`Estado_id_estado` = ? WHERE `id_tema` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TemaEntity entity) {
        statement.bindLong(1, entity.getId_tema());
        statement.bindString(2, entity.getNombre_tema());
        statement.bindLong(3, entity.getEstado_id_estado());
        statement.bindLong(4, entity.getId_tema());
      }
    };
  }

  @Override
  public Object insert(final TemaEntity tema, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTemaEntity.insertAndReturnId(tema);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final TemaEntity tema, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTemaEntity.handle(tema);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TemaEntity>> getAll() {
    final String _sql = "SELECT * FROM temas";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"temas"}, new Callable<List<TemaEntity>>() {
      @Override
      @NonNull
      public List<TemaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdTema = CursorUtil.getColumnIndexOrThrow(_cursor, "id_tema");
          final int _cursorIndexOfNombreTema = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre_tema");
          final int _cursorIndexOfEstadoIdEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "Estado_id_estado");
          final List<TemaEntity> _result = new ArrayList<TemaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TemaEntity _item;
            final long _tmpId_tema;
            _tmpId_tema = _cursor.getLong(_cursorIndexOfIdTema);
            final String _tmpNombre_tema;
            _tmpNombre_tema = _cursor.getString(_cursorIndexOfNombreTema);
            final long _tmpEstado_id_estado;
            _tmpEstado_id_estado = _cursor.getLong(_cursorIndexOfEstadoIdEstado);
            _item = new TemaEntity(_tmpId_tema,_tmpNombre_tema,_tmpEstado_id_estado);
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
  public Object getById(final long id, final Continuation<? super TemaEntity> $completion) {
    final String _sql = "SELECT * FROM temas WHERE id_tema = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TemaEntity>() {
      @Override
      @Nullable
      public TemaEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdTema = CursorUtil.getColumnIndexOrThrow(_cursor, "id_tema");
          final int _cursorIndexOfNombreTema = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre_tema");
          final int _cursorIndexOfEstadoIdEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "Estado_id_estado");
          final TemaEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId_tema;
            _tmpId_tema = _cursor.getLong(_cursorIndexOfIdTema);
            final String _tmpNombre_tema;
            _tmpNombre_tema = _cursor.getString(_cursorIndexOfNombreTema);
            final long _tmpEstado_id_estado;
            _tmpEstado_id_estado = _cursor.getLong(_cursorIndexOfEstadoIdEstado);
            _result = new TemaEntity(_tmpId_tema,_tmpNombre_tema,_tmpEstado_id_estado);
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
  public Flow<List<TemaEntity>> searchByNombre(final String query) {
    final String _sql = "SELECT * FROM temas WHERE nombre_tema LIKE '%' || ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"temas"}, new Callable<List<TemaEntity>>() {
      @Override
      @NonNull
      public List<TemaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdTema = CursorUtil.getColumnIndexOrThrow(_cursor, "id_tema");
          final int _cursorIndexOfNombreTema = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre_tema");
          final int _cursorIndexOfEstadoIdEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "Estado_id_estado");
          final List<TemaEntity> _result = new ArrayList<TemaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TemaEntity _item;
            final long _tmpId_tema;
            _tmpId_tema = _cursor.getLong(_cursorIndexOfIdTema);
            final String _tmpNombre_tema;
            _tmpNombre_tema = _cursor.getString(_cursorIndexOfNombreTema);
            final long _tmpEstado_id_estado;
            _tmpEstado_id_estado = _cursor.getLong(_cursorIndexOfEstadoIdEstado);
            _item = new TemaEntity(_tmpId_tema,_tmpNombre_tema,_tmpEstado_id_estado);
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
