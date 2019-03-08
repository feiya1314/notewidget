package com.feiya.me.notewidget;

public interface Constant {
    int DATABASE_VERSION = 1;
    String DATABASE_NAME = "NoteWidget.db";
    String TABLE_NAME = "note";
    String CREATE_NOTE_TABLE = "create table note ("
            + "id integer primary key autoincrement,"
            + "pageId integer,"
            + "title text,"
            + "widgetId integer,"
            + "content text,"
            + "favorite integer,"
            + "changedFlag integer,"
            + "writingDate text)";
}
