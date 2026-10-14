package com.spendingtracker.data;

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
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile DayEntryDao _dayEntryDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `day_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `monthKey` TEXT NOT NULL, `dayNumber` INTEGER NOT NULL, `food` REAL NOT NULL, `travelling` REAL NOT NULL, `others` REAL NOT NULL, `foodDetails` TEXT NOT NULL, `travellingDetails` TEXT NOT NULL, `othersDetails` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '4dd010932fa3a21d8767fd7412ed9ae0')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `day_entries`");
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
        final HashMap<String, TableInfo.Column> _columnsDayEntries = new HashMap<String, TableInfo.Column>(9);
        _columnsDayEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayEntries.put("monthKey", new TableInfo.Column("monthKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayEntries.put("dayNumber", new TableInfo.Column("dayNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayEntries.put("food", new TableInfo.Column("food", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayEntries.put("travelling", new TableInfo.Column("travelling", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayEntries.put("others", new TableInfo.Column("others", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayEntries.put("foodDetails", new TableInfo.Column("foodDetails", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayEntries.put("travellingDetails", new TableInfo.Column("travellingDetails", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayEntries.put("othersDetails", new TableInfo.Column("othersDetails", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDayEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDayEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDayEntries = new TableInfo("day_entries", _columnsDayEntries, _foreignKeysDayEntries, _indicesDayEntries);
        final TableInfo _existingDayEntries = TableInfo.read(db, "day_entries");
        if (!_infoDayEntries.equals(_existingDayEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "day_entries(com.spendingtracker.data.DayEntry).\n"
                  + " Expected:\n" + _infoDayEntries + "\n"
                  + " Found:\n" + _existingDayEntries);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "4dd010932fa3a21d8767fd7412ed9ae0", "a2ecde9d804ad6015111213ef8bfcedf");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "day_entries");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `day_entries`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
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
    _typeConvertersMap.put(DayEntryDao.class, DayEntryDao_Impl.getRequiredConverters());
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
  public DayEntryDao dayEntryDao() {
    if (_dayEntryDao != null) {
      return _dayEntryDao;
    } else {
      synchronized(this) {
        if(_dayEntryDao == null) {
          _dayEntryDao = new DayEntryDao_Impl(this);
        }
        return _dayEntryDao;
      }
    }
  }
}
