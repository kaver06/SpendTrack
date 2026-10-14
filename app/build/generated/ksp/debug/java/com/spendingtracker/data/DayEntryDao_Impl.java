package com.spendingtracker.data;

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
public final class DayEntryDao_Impl implements DayEntryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DayEntry> __insertionAdapterOfDayEntry;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<DayEntry> __deletionAdapterOfDayEntry;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMonth;

  public DayEntryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDayEntry = new EntityInsertionAdapter<DayEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `day_entries` (`id`,`monthKey`,`dayNumber`,`food`,`travelling`,`others`,`foodDetails`,`travellingDetails`,`othersDetails`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DayEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getMonthKey());
        statement.bindLong(3, entity.getDayNumber());
        statement.bindDouble(4, entity.getFood());
        statement.bindDouble(5, entity.getTravelling());
        statement.bindDouble(6, entity.getOthers());
        final String _tmp = __converters.fromExpenseDetailList(entity.getFoodDetails());
        statement.bindString(7, _tmp);
        final String _tmp_1 = __converters.fromExpenseDetailList(entity.getTravellingDetails());
        statement.bindString(8, _tmp_1);
        final String _tmp_2 = __converters.fromExpenseDetailList(entity.getOthersDetails());
        statement.bindString(9, _tmp_2);
      }
    };
    this.__deletionAdapterOfDayEntry = new EntityDeletionOrUpdateAdapter<DayEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `day_entries` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DayEntry entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteMonth = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM day_entries WHERE monthKey = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdate(final DayEntry entry, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDayEntry.insertAndReturnId(entry);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final DayEntry entry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDayEntry.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMonth(final String monthKey, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMonth.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, monthKey);
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
          __preparedStmtOfDeleteMonth.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DayEntry>> getEntriesForMonth(final String monthKey) {
    final String _sql = "SELECT * FROM day_entries WHERE monthKey = ? ORDER BY dayNumber ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, monthKey);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"day_entries"}, new Callable<List<DayEntry>>() {
      @Override
      @NonNull
      public List<DayEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMonthKey = CursorUtil.getColumnIndexOrThrow(_cursor, "monthKey");
          final int _cursorIndexOfDayNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "dayNumber");
          final int _cursorIndexOfFood = CursorUtil.getColumnIndexOrThrow(_cursor, "food");
          final int _cursorIndexOfTravelling = CursorUtil.getColumnIndexOrThrow(_cursor, "travelling");
          final int _cursorIndexOfOthers = CursorUtil.getColumnIndexOrThrow(_cursor, "others");
          final int _cursorIndexOfFoodDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "foodDetails");
          final int _cursorIndexOfTravellingDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "travellingDetails");
          final int _cursorIndexOfOthersDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "othersDetails");
          final List<DayEntry> _result = new ArrayList<DayEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DayEntry _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpMonthKey;
            _tmpMonthKey = _cursor.getString(_cursorIndexOfMonthKey);
            final int _tmpDayNumber;
            _tmpDayNumber = _cursor.getInt(_cursorIndexOfDayNumber);
            final double _tmpFood;
            _tmpFood = _cursor.getDouble(_cursorIndexOfFood);
            final double _tmpTravelling;
            _tmpTravelling = _cursor.getDouble(_cursorIndexOfTravelling);
            final double _tmpOthers;
            _tmpOthers = _cursor.getDouble(_cursorIndexOfOthers);
            final List<ExpenseDetail> _tmpFoodDetails;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfFoodDetails);
            _tmpFoodDetails = __converters.toExpenseDetailList(_tmp);
            final List<ExpenseDetail> _tmpTravellingDetails;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTravellingDetails);
            _tmpTravellingDetails = __converters.toExpenseDetailList(_tmp_1);
            final List<ExpenseDetail> _tmpOthersDetails;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfOthersDetails);
            _tmpOthersDetails = __converters.toExpenseDetailList(_tmp_2);
            _item = new DayEntry(_tmpId,_tmpMonthKey,_tmpDayNumber,_tmpFood,_tmpTravelling,_tmpOthers,_tmpFoodDetails,_tmpTravellingDetails,_tmpOthersDetails);
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
  public Object getEntry(final String monthKey, final int dayNumber,
      final Continuation<? super DayEntry> $completion) {
    final String _sql = "SELECT * FROM day_entries WHERE monthKey = ? AND dayNumber = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, monthKey);
    _argIndex = 2;
    _statement.bindLong(_argIndex, dayNumber);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DayEntry>() {
      @Override
      @Nullable
      public DayEntry call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMonthKey = CursorUtil.getColumnIndexOrThrow(_cursor, "monthKey");
          final int _cursorIndexOfDayNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "dayNumber");
          final int _cursorIndexOfFood = CursorUtil.getColumnIndexOrThrow(_cursor, "food");
          final int _cursorIndexOfTravelling = CursorUtil.getColumnIndexOrThrow(_cursor, "travelling");
          final int _cursorIndexOfOthers = CursorUtil.getColumnIndexOrThrow(_cursor, "others");
          final int _cursorIndexOfFoodDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "foodDetails");
          final int _cursorIndexOfTravellingDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "travellingDetails");
          final int _cursorIndexOfOthersDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "othersDetails");
          final DayEntry _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpMonthKey;
            _tmpMonthKey = _cursor.getString(_cursorIndexOfMonthKey);
            final int _tmpDayNumber;
            _tmpDayNumber = _cursor.getInt(_cursorIndexOfDayNumber);
            final double _tmpFood;
            _tmpFood = _cursor.getDouble(_cursorIndexOfFood);
            final double _tmpTravelling;
            _tmpTravelling = _cursor.getDouble(_cursorIndexOfTravelling);
            final double _tmpOthers;
            _tmpOthers = _cursor.getDouble(_cursorIndexOfOthers);
            final List<ExpenseDetail> _tmpFoodDetails;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfFoodDetails);
            _tmpFoodDetails = __converters.toExpenseDetailList(_tmp);
            final List<ExpenseDetail> _tmpTravellingDetails;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTravellingDetails);
            _tmpTravellingDetails = __converters.toExpenseDetailList(_tmp_1);
            final List<ExpenseDetail> _tmpOthersDetails;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfOthersDetails);
            _tmpOthersDetails = __converters.toExpenseDetailList(_tmp_2);
            _result = new DayEntry(_tmpId,_tmpMonthKey,_tmpDayNumber,_tmpFood,_tmpTravelling,_tmpOthers,_tmpFoodDetails,_tmpTravellingDetails,_tmpOthersDetails);
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
  public Flow<List<String>> getAllMonthKeys() {
    final String _sql = "SELECT DISTINCT monthKey FROM day_entries ORDER BY monthKey DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"day_entries"}, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
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
