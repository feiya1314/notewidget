package com.feiya.me.notewidget;

import java.util.Date;

/**
 * Created by feiya on 2016/9/17.
 */
public class NoteItem {

    private String content;
    private Date writingDate;
    private String title;
    private int pageId;
    private int favorite=0;

    public NoteItem(){

    }
    public NoteItem(String title){
        this.title=title;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getWritingDate() {
        return writingDate;
    }

    public void setWritingDate(Date writingDate) {
        this.writingDate = writingDate;
    }
    public int getId(){
        return pageId;
    }

    public void setId(int id) {
        this.pageId = id;
    }

    public void setFavorite(int favorite){
        this.favorite=favorite;
    }

    public int getFavorite() {
        return favorite;
    }
}
