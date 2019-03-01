package com.feiya.me.notewidget.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.feiya.me.notewidget.model.NoteItem;

import java.util.ArrayList;

/**
 * Created by feiya on 2016/9/27.
 */

public class DatabaseManager {
    private static final String TAG = DatabaseManager.class.getSimpleName();
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * @param noteItem which note you want insert to database
     * @function add a note to sqlite database
     */
    public void addItem(NoteItem noteItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", noteItem.getTitle());
        Log.d(TAG, "title " + noteItem.getTitle());

        contentValues.put("content", noteItem.getContent());
        Log.d(TAG, "content " + noteItem.getContent());

        contentValues.put("widgetId", noteItem.getWidgetId());

        contentValues.put("changedFlag", noteItem.getChangedFlag());

        contentValues.put("pageId", noteItem.getPageId());
        Log.d(TAG, "pageId " + noteItem.getPageId());

        contentValues.put("writingDate", noteItem.getWritingDate());
        Log.d(TAG, "writingDate " + noteItem.getWritingDate());

        contentValues.put("favorite", noteItem.getFavorite());
        Log.d(TAG, "favorite " + noteItem.getFavorite());

        db.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
    }

    /**
     *
     */
    public void updateItem(NoteItem noteItem, int widgetId, int pageId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", noteItem.getTitle());
        contentValues.put("content", noteItem.getContent());
        contentValues.put("writingDate", noteItem.getWritingDate());
        Log.d(TAG, "updateItem" + pageId);
        db.update(DatabaseHelper.TABLE_NAME, contentValues, "pageId=? ", new String[]{String.valueOf(pageId)});
    }

    public void updateTitle(int widgetId, int pageId, String title) {
        String sql = "UPDATE " + DatabaseHelper.TABLE_NAME + " SET title='" + title +
                "' WHERE pageId=" + pageId + " AND widgetId=" + widgetId;
        db.execSQL(sql);
    }

    public void updateContent(int widgetId, int pageId, String content) {
        String sql = "UPDATE " + DatabaseHelper.TABLE_NAME + " SET content='" + content +
                "' WHERE pageId=" + pageId + " AND widgetId=" + widgetId;
        db.execSQL(sql);
    }

    public void changedFlagToFalse() {
        String sql = "UPDATE " + DatabaseHelper.TABLE_NAME + " SET changedFlag=0 WHERE changedFlag=1";
        db.execSQL(sql);
    }

    public void changedFlagToTrue(int widgetId, int pageId) {
        String sql = "UPDATE " + DatabaseHelper.TABLE_NAME + " SET changedFlag=1 WHERE widgetId=" + widgetId + " AND pageId=" + pageId;
        db.execSQL(sql);
    }

    public Cursor queryTopPageItem(int widgetId) {
        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE widgetId=" + widgetId + " AND favorite=1";
        Log.d(TAG, "queryTopPageItem ");
        return db.rawQuery(sql, null);
    }

    public void topPageToTrue(int widgetId, int pageId) {
        String sql = "UPDATE " + DatabaseHelper.TABLE_NAME + " SET favorite=1 WHERE widgetId=" + widgetId + " AND pageId=" + pageId;
        db.execSQL(sql);
    }

    public void topPageToFalse(int widgetId) {
        String sql = "UPDATE " + DatabaseHelper.TABLE_NAME + " SET favorite=0 WHERE widgetId=" + widgetId + " AND favorite=1";
        db.execSQL(sql);
    }

    public void topPageInit() {
        String sql_1 = "UPDATE " + DatabaseHelper.TABLE_NAME + " SET favorite=0 WHERE favorite=1";
        String sql_2 = "UPDATE " + DatabaseHelper.TABLE_NAME + " SET favorite=1 WHERE pageId=0";
        db.execSQL(sql_1);
        db.execSQL(sql_2);

    }

    public ArrayList<NoteItem> getItems(Cursor cursor) {
        ArrayList<NoteItem> noteItems = new ArrayList<NoteItem>();
        if (cursor.getCount() == 0) {
            Log.e(TAG, "getItems :Cursor's count is 0");
        } else {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                NoteItem noteItem = new NoteItem();
                noteItem.setPageId(cursor.getInt(1));

                noteItem.setTitle(cursor.getString(2));

                Log.d(TAG, "setTitle " + cursor.getString(2));
                noteItem.setWidgetId(cursor.getInt(3));

                noteItem.setContent(cursor.getString(4));
                noteItem.setFavorite(cursor.getInt(5));
                noteItem.setChangedFlag(cursor.getInt(6));
                noteItem.setWritingDate(cursor.getString(7));
                noteItems.add(noteItem);
            }
        }
        cursor.close();
        return noteItems;
    }

    public NoteItem getItem(Cursor cursor) {
        cursor.moveToFirst();
        Log.d(TAG, "cursor count " + cursor.getCount());
        if (cursor.getCount() == 0 || cursor.getCount() > 1) {
            Log.e(TAG, "cursor count is 0 or >1");
            cursor.close();
            return null;
        } else {
            Log.d(TAG, " getItem");
            Log.d(TAG, "getdate " + cursor.getString(5));
            NoteItem noteItem = new NoteItem();
            noteItem.setPageId(cursor.getInt(1));

            noteItem.setTitle(cursor.getString(2));

            Log.d(TAG, "setTitle " + cursor.getString(2));
            noteItem.setWidgetId(cursor.getInt(3));

            noteItem.setContent(cursor.getString(4));
            noteItem.setFavorite(cursor.getInt(5));
            noteItem.setChangedFlag(cursor.getInt(6));
            noteItem.setWritingDate(cursor.getString(7));
            cursor.close();
            return noteItem;
        }
    }

    public Cursor queryChangedItem() {
        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE changedFlag=1";
        Log.d("datamagager", sql);
        Log.d(TAG, "queryItem  " + sql);

        return db.rawQuery(sql, null);
    }

    public void deleteItemsByWidgetId(int widgetId) {
        String sql = "DELETE FROM " + DatabaseHelper.TABLE_NAME + " WHERE widgetId=" + widgetId;
        db.execSQL(sql);
    }

    public Cursor queryItem(int widgetId, int pageId) {
        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE pageId=" + pageId + " AND widgetId=" + widgetId;
        Log.d("datamagager", sql);
        Log.d(TAG, "queryItem  " + sql);

        return db.rawQuery(sql, null);
    }

    public Cursor queryItemByWidgetId(int widgetId) {
        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_NAME
                + " WHERE widgetId="
                + widgetId;
        return db.rawQuery(sql, null);
    }


    public int getTopPageId(int widgetId) {
        return getItem(queryTopPageItem(widgetId)).getPageId();
    }

    public void closeDB() {
        Log.i(TAG, "closeDB");
        db.close();
    }
}
