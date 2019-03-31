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
            + "deleted integer,"
            + "writingDate text)";

    String NEXT_ACTION = "com.feiya.me.notewidget.NEXT_ACTION";
    String PREVIOUS_ACTION = "com.feiya.me.notewidget.PREVIOUS_ACTION";
    String COLLECTION_VIEW_ACTION = "com.feiya.me.notewidget.COLLECTION_VIEW_ACTION";
    String COLLECTION_VIEW_EXTRA = "com.feiya.me.notewidget.COLLECTION_VIEW_EXTRA";
    String PAGE_ID = "com.feiya.me.notewidget.PAGE_ID";
    String DATA_CHANGED_ACTION = "com.feiya.me.notewidget.DATA_CHANGED_ACTION";
    String INIT_NOTE_TITLE = "在此输入标题";
    String INIT_NOTE_CONTENT = "在此输入记事. 小提示：可通过底部左右箭头翻页！";
}
