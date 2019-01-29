package com.zcool.inkstone.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.zcool.inkstone.manager.ProcessManager;
import com.zcool.inkstone.security.AES;
import com.zcool.inkstone.util.ContextUtil;
import com.zcool.inkstone.util.IOUtil;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import timber.log.Timber;

/**
 * 基于 SQLite 模拟 KV 数据库
 */
public class SimpleDB {

    private static final int DB_VERSION = 2;

    private static final String TABLE_NAME = "t_simple";
    private static final String COLUMN_KEY = "c_key";
    private static final String COLUMN_VALUE = "c_value";
    private static final String COLUMN_UPDATE = "c_update";
    private static final String SQL_CREATE_TABLE =
            "create table t_simple ("
                    + "c_key text not null primary key"
                    + ",c_value text"
                    + ",c_update integer"
                    + ")";
    private static final String SQL_CREATE_INDEX =
            "create index index_simple_update on t_simple(c_update)";

    private final SQLiteOpenHelper mOpenHelper;

    /**
     * 实现中会在数据库名前附加当前进程标识
     */
    public SimpleDB(@NonNull String databaseName) {
        Timber.v("init");
        String dbName = ProcessManager.getInstance().getProcessTag() + "_" + databaseName;
        mOpenHelper =
                new SQLiteOpenHelper(ContextUtil.getContext(), dbName, null, DB_VERSION) {
                    @Override
                    public void onCreate(SQLiteDatabase db) {
                        db.execSQL(SQL_CREATE_TABLE);
                        db.execSQL(SQL_CREATE_INDEX);
                    }

                    @Override
                    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                        Timber.v("upgrade from version %s to %s", oldVersion, newVersion);
                        // v1 -> v2
                        if (oldVersion == 1 && newVersion == 2) {
                            // v2 增加了加密模式
                            upgradeWithEncodeAllData(db);
                            return;
                        }

                        throw new IllegalAccessError("not support upgrade from version " + oldVersion + " to " + newVersion);
                    }
                };
    }

    /**
     * 所有数据转换为加密模式
     */
    private void upgradeWithEncodeAllData(SQLiteDatabase db) {
        Timber.v("upgrade with encode all data");
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME,
                    new String[]{COLUMN_KEY, COLUMN_VALUE, COLUMN_UPDATE},
                    null,
                    null,
                    null,
                    null,
                    COLUMN_UPDATE + " asc");
            int count = cursor.getCount();
            for (; cursor.moveToNext(); ) {
                count--;
                try {
                    String key = cursor.getString(0);
                    String value = cursor.getString(1);
                    long update = cursor.getLong(2);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(COLUMN_KEY, encodeKey(key));
                    contentValues.put(COLUMN_VALUE, encodeValue(value));
                    contentValues.put(COLUMN_UPDATE, update);

                    Timber.v("[%s]upgrade with encode data: [%s, %s, %s] -> [%s, %s, %s]",
                            count,
                            key, value, update,
                            contentValues.get(COLUMN_KEY), contentValues.get(COLUMN_VALUE), contentValues.get(COLUMN_UPDATE));
                    db.replace(TABLE_NAME, null, contentValues);
                    db.delete(TABLE_NAME, COLUMN_KEY + "=?", new String[]{key});
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(cursor);
        }
    }

    @Nullable
    public String get(@Nullable String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        Cursor cursor = null;
        try {
            key = encodeKey(key);

            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            cursor =
                    db.query(
                            TABLE_NAME,
                            new String[]{COLUMN_VALUE},
                            COLUMN_KEY + "=?",
                            new String[]{key},
                            null,
                            null,
                            null);
            if (cursor.moveToFirst()) {
                String value = cursor.getString(0);

                if (value != null) {
                    value = decodeValue(value);
                }

                return value;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(cursor);
        }
        return null;
    }

    @Nullable
    public Map<String, String> getAll() {
        Cursor cursor = null;
        try {
            Map<String, String> data = new HashMap<>();
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            cursor = db.query(TABLE_NAME,
                    new String[]{COLUMN_KEY, COLUMN_VALUE},
                    null,
                    null,
                    null,
                    null,
                    COLUMN_UPDATE + " desc");
            for (; cursor.moveToNext(); ) {
                String key = cursor.getString(0);
                String value = cursor.getString(1);

                key = decodeKey(key);
                value = decodeValue(value);

                data.put(key, value);
            }
            return data;
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(cursor);
        }
        return null;
    }

    public void set(@Nullable String key, @Nullable String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (TextUtils.isEmpty(value)) {
            remove(key);
            return;
        }

        try {
            key = encodeKey(key);
            value = encodeValue(value);

            SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_KEY, key);
            contentValues.put(COLUMN_VALUE, value);
            contentValues.put(COLUMN_UPDATE, System.currentTimeMillis());
            db.replace(TABLE_NAME, null, contentValues);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void touch(@Nullable String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }

        try {
            key = encodeKey(key);

            SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_UPDATE, System.currentTimeMillis());
            db.update(TABLE_NAME, contentValues, COLUMN_KEY + "=?", new String[]{key});
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除多余的旧数据(按时间倒序)，保留指定条数的数据. 返回删除的数据的条数，如果不满足删除条件返回 -1.
     */
    public int trim(int maxRows) {
        if (maxRows < 1) {
            return -1;
        }

        try {
            SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();

            Cursor cursor = null;
            long lastUpdate = 0L;
            try {
                cursor =
                        db.query(
                                TABLE_NAME,
                                new String[]{COLUMN_UPDATE},
                                null,
                                null,
                                null,
                                null,
                                COLUMN_UPDATE + " desc",
                                (maxRows - 1) + ",1");
                if (cursor.moveToFirst()) {
                    lastUpdate = cursor.getLong(0);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                IOUtil.closeQuietly(cursor);
            }

            if (lastUpdate <= 0) {
                return -1;
            }

            return db.delete(
                    TABLE_NAME, COLUMN_UPDATE + "<?", new String[]{String.valueOf(lastUpdate)});
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 清空数据，返回删除数据的条数, 如果出错，返回 -1.
     */
    public int clear() {
        try {
            SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
            return db.delete(TABLE_NAME, null, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void remove(@Nullable String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        try {
            key = encodeKey(key);

            SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
            db.delete(TABLE_NAME, COLUMN_KEY + "=?", new String[]{key});
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果失败，返回-1。
     */
    public int count() {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
            cursor = db.rawQuery("select count(*) from " + TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(cursor);
        }
        return -1;
    }

    /**
     * only for debug
     */
    public void printAllRows() {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
            String dbName = this.mOpenHelper.getDatabaseName();
            String dbPath = db.getPath();
            String tag = dbPath + "[" + dbName + "]";
            Timber.d("--" + tag + "--");
            cursor =
                    db.query(
                            TABLE_NAME,
                            new String[]{COLUMN_KEY, COLUMN_VALUE, COLUMN_UPDATE},
                            null,
                            null,
                            null,
                            null,
                            COLUMN_UPDATE + " desc");
            String key;
            String value;
            long update;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                key = cursor.getString(0);
                value = cursor.getString(1);
                update = cursor.getLong(2);

                key = decodeKey(key);
                value = decodeValue(value);

                Timber.d(dbName + " " + update + ", " + key + ", " + value);
            }
            Timber.d("--" + tag + "-- end");
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(cursor);
        }
    }

    @NonNull
    private static String encodeKey(@Nullable final String key) throws Exception {
        return AES.getDefault().encode(key, true);
    }

    @Nullable
    private static String decodeKey(@NonNull final String encodedKey) throws Exception {
        return AES.getDefault().decode(encodedKey);
    }

    @NonNull
    private static String encodeValue(@Nullable final String value) throws Exception {
        return AES.getDefault().encode(value);
    }

    @Nullable
    private static String decodeValue(@NonNull final String encodedValue) throws Exception {
        return AES.getDefault().decode(encodedValue);
    }

}
