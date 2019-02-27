package com.feiya.me.notewidget.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.feiya.me.notewidget.NoteWidgetProvider;
import com.feiya.me.notewidget.R;
import com.feiya.me.notewidget.db.DatabaseManager;
import com.feiya.me.notewidget.model.NoteItem;


public class EditNoteActivity extends Activity {
    private final static String TAG=EditNoteActivity.class.getSimpleName();
    private EditText eTitle;
    private EditText eContent;
    private FloatingActionButton btn_save;
   // private LinearLayout linearLayout;
    private DatabaseManager databaseManager;

    private Context mContext;
    private Intent intent;

    private String title;
    private String content;
    private NoteItem noteItem;
    private int pageId;
    private int widgetId;

    private Boolean IsTitleChanged;
    private Boolean IsContentChanged;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.warp_edit_note);
        init();
    }
    private void init(){

        IsTitleChanged=false;
        IsContentChanged=false;

        mContext=this;
        intent=this.getIntent();
        pageId=intent.getIntExtra(NoteWidgetProvider.PAGE_ID,0);
        widgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
        Log.e(TAG,"pageId from intent "+pageId);
        Log.e(TAG,"widgetId from intent "+widgetId);

        eTitle=(EditText)findViewById(R.id.edit_note_title);
        eContent=(EditText)findViewById(R.id.edit_note_content);
       // linearLayout=(LinearLayout)findViewById(R.id.save_edit_content);
        btn_save=(FloatingActionButton) findViewById(R.id.save);

        databaseManager=new DatabaseManager(mContext);
        noteItem=databaseManager.getItem(databaseManager.queryItem(widgetId,pageId));

        title=noteItem.getTitle();
        Log.d(TAG,"init title "+title);
        content=noteItem.getContent();

        eTitle.setText(title);
        eContent.setText(content);

        eTitle.setHint("输入标题");

        eTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                IsTitleChanged=true;
            }
        });
        /*
        eTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        */

        eContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                IsContentChanged=true;
            }
        });

        /*
        eContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
`       */
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(IsTitleChanged||IsContentChanged){
                    saveNote();
                }
                finish();
                //overridePendingTransition(R.anim.zoomin,R.anim.zoomout);
            }
        });


    }

    /**
     * Called when the activity has detected the user's press of the back
     * key.  The default implementation simply finishes the current activity,
     * but you can override this to do whatever you want.
     */
    @Override
    public void onBackPressed() {
        if(IsTitleChanged||IsContentChanged){
            Dialog dialog=new AlertDialog.Builder(this).
                    setMessage("是否放弃更改？").
                    setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).
                    setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveNote();
                            finish();
                            //overridePendingTransition(R.anim.zoomin,R.anim.zoomout);

                        }
                    }).
                    setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            //overridePendingTransition(R.anim.zoomin,R.anim.zoomout);
                        }
                    }).create();
            dialog.show();
        }
        else {
            super.onBackPressed();
            //overridePendingTransition(R.anim.zoomin,R.anim.zoomout);
        }
    }
    private void saveNote(){
        if(IsContentChanged&&IsTitleChanged) {
            title = eTitle.getText().toString();
            content = eContent.getText().toString();
            databaseManager.updateTitle(widgetId,pageId,title);
            databaseManager.updateContent(widgetId,pageId,content);
            databaseManager.changedFlagToTrue(widgetId,pageId);
        }else if(IsContentChanged){
            content = eContent.getText().toString();
            databaseManager.updateContent(widgetId,pageId,content);
            databaseManager.changedFlagToTrue(widgetId,pageId);
        }
        else {
            title = eTitle.getText().toString();
            databaseManager.updateTitle(widgetId,pageId,title);
            databaseManager.changedFlagToTrue(widgetId,pageId);
        }
        updateWidget(mContext,pageId);
    }
    @Override
    protected void onDestroy() {
        databaseManager.closeDB();
        super.onDestroy();
    }

    private void updateWidget(Context context, int pageId){
        //databaseManager.getItem(databaseManager.queryItem(pageId));
        //noteItem.setTitle(eTitle.getText().toString());
        //noteItem.setContent(eContent.getText().toString());
        Intent intent=new Intent(NoteWidgetProvider.DATA_CHANGED_ACTION);
        intent.putExtra(NoteWidgetProvider.PAGE_ID,pageId);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetId);
        Log.e(TAG,"activity send data changed action");
        context.sendBroadcast(intent);
    }
}
