package com.example.qualifygym_grupo13.data.local.imagen;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
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
public final class ImagenDao_Impl implements ImagenDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ImagenEntity> __insertionAdapterOfImagenEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public ImagenDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfImagenEntity = new EntityInsertionAdapter<ImagenEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `imagenes` (`id_imagen`,`nombre_imagen`,`imagen`,`Publicacion_id_publicacion`,`Estado_id_estado`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ImagenEntity entity) {
        statement.bindLong(1, entity.getId_imagen());
        statement.bindString(2, entity.getNombre_imagen());
        statement.bindBlob(3, entity.getImagen());
        statement.bindLong(4, entity.getPublicacion_id_publicacion());
        statement.bindLong(5, entity.getEstado_id_estado());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM imagenes WHERE id_imagen = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ImagenEntity imagen, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfImagenEntity.insertAndReturnId(imagen);
          __db.setTransactionSuccessful();
          return _result;
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
  public Flow<List<ImagenEntity>> getByPublicacionId(final long publicacionId) {
    final String _sql = "SELECT * FROM imagenes WHERE Publicacion_id_publicacion = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, publicacionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"imagenes"}, new Callable<List<ImagenEntity>>() {
      @Override
      @NonNull
      public List<ImagenEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdImagen = CursorUtil.getColumnIndexOrThrow(_cursor, "id_imagen");
          final int _cursorIndexOfNombreImagen = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre_imagen");
          final int _cursorIndexOfImagen = CursorUtil.getColumnIndexOrThrow(_cursor, "imagen");
          final int _cursorIndexOfPublicacionIdPublicacion = CursorUtil.getColumnIndexOrThrow(_cursor, "Publicacion_id_publicacion");
          final int _cursorIndexOfEstadoIdEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "Estado_id_estado");
          final List<ImagenEntity> _result = new ArrayList<ImagenEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ImagenEntity _item;
            final long _tmpId_imagen;
            _tmpId_imagen = _cursor.getLong(_cursorIndexOfIdImagen);
            final String _tmpNombre_imagen;
            _tmpNombre_imagen = _cursor.getString(_cursorIndexOfNombreImagen);
            final byte[] _tmpImagen;
            _tmpImagen = _cursor.getBlob(_cursorIndexOfImagen);
            final long _tmpPublicacion_id_publicacion;
            _tmpPublicacion_id_publicacion = _cursor.getLong(_cursorIndexOfPublicacionIdPublicacion);
            final long _tmpEstado_id_estado;
            _tmpEstado_id_estado = _cursor.getLong(_cursorIndexOfEstadoIdEstado);
            _item = new ImagenEntity(_tmpId_imagen,_tmpNombre_imagen,_tmpImagen,_tmpPublicacion_id_publicacion,_tmpEstado_id_estado);
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
  public Object getById(final long id, final Continuation<? super ImagenEntity> $completion) {
    final String _sql = "SELECT * FROM imagenes WHERE id_imagen = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ImagenEntity>() {
      @Override
      @Nullable
      public ImagenEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdImagen = CursorUtil.getColumnIndexOrThrow(_cursor, "id_imagen");
          final int _cursorIndexOfNombreImagen = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre_imagen");
          final int _cursorIndexOfImagen = CursorUtil.getColumnIndexOrThrow(_cursor, "imagen");
          final int _cursorIndexOfPublicacionIdPublicacion = CursorUtil.getColumnIndexOrThrow(_cursor, "Publicacion_id_publicacion");
          final int _cursorIndexOfEstadoIdEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "Estado_id_estado");
          final ImagenEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId_imagen;
            _tmpId_imagen = _cursor.getLong(_cursorIndexOfIdImagen);
            final String _tmpNombre_imagen;
            _tmpNombre_imagen = _cursor.getString(_cursorIndexOfNombreImagen);
            final byte[] _tmpImagen;
            _tmpImagen = _cursor.getBlob(_cursorIndexOfImagen);
            final long _tmpPublicacion_id_publicacion;
            _tmpPublicacion_id_publicacion = _cursor.getLong(_cursorIndexOfPublicacionIdPublicacion);
            final long _tmpEstado_id_estado;
            _tmpEstado_id_estado = _cursor.getLong(_cursorIndexOfEstadoIdEstado);
            _result = new ImagenEntity(_tmpId_imagen,_tmpNombre_imagen,_tmpImagen,_tmpPublicacion_id_publicacion,_tmpEstado_id_estado);
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
