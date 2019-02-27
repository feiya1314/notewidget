package com.feiya.me.notewidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.feiya.me.notewidget.activity.EditNoteActivity;
import com.feiya.me.notewidget.db.DatabaseManager;
import com.feiya.me.notewidget.model.NoteItem;
import com.feiya.me.notewidget.service.AdapterViewFlipperService;
import com.feiya.me.notewidget.utils.DateToStringUtils;

import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class NoteWidgetProvider extends AppWidgetProvider {

    private static final String TAG=NoteWidgetProvider.class.getSimpleName();

    public static final String NEXT_ACTION="com.feiya.me.notewidget.NEXT_ACTION";
    public static final String PREVIOUS_ACTION="com.feiya.me.notewidget.PREVIOUS_ACTION";
    public static final String COLLECTION_VIEW_ACTION="com.feiya.me.notewidget.COLLECTION_VIEW_ACTION";
    public static final String COLLECTION_VIEW_EXTRA="com.feiya.me.notewidget.COLLECTION_VIEW_EXTRA";
    public static final String PAGE_ID="com.feiya.me.notewidget.PAGE_ID";
    public static final String DATA_CHANGED_ACTION="com.feiya.me.notewidget.DATA_CHANGED_ACTION";

    private int pagesCount=10;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.e(TAG,"Provider onUpdate");
        Log.e(TAG,"update appwidgetIds "+appWidgetIds.length);
        Log.e(TAG,"update appwidgetIds0 "+appWidgetIds[0]);


        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            initWidgetDatabase(context,appWidgetManager,appWidgetId);

            RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.adapterviewfilpper);
            Intent viewFlipperServiceIntent=new Intent(context, AdapterViewFlipperService.class);
            viewFlipperServiceIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            viewFlipperServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
            remoteViews.setRemoteAdapter(R.id.page_flipper,viewFlipperServiceIntent);

            Intent nextIntent=new Intent(context,NoteWidgetProvider.class);
            nextIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            nextIntent.setAction(NEXT_ACTION);

            nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent btnPendingIntent=PendingIntent.getBroadcast(context,appWidgetId,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.next,btnPendingIntent);

            Intent previousIntent=new Intent();
            previousIntent.setAction(PREVIOUS_ACTION);
            previousIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            previousIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            //如果将getBroadcast的参数request简单设置为0的话，会同时更新桌面上的所有widget，如果只更新特定的一个需要传入该widget的ID，appWidgetId
            PendingIntent prePendingIntent=PendingIntent.getBroadcast(context,appWidgetId,previousIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.previous,prePendingIntent);

            Intent flipperIntent=new Intent();
            flipperIntent.setAction(COLLECTION_VIEW_ACTION);
            flipperIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            flipperIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);

            PendingIntent filpperPendingIntent=PendingIntent.getBroadcast(context,appWidgetId,flipperIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setPendingIntentTemplate(R.id.page_flipper,filpperPendingIntent);
            //ComponentName componentName=new ComponentName(context,NoteWidgetProvider.class);
            // appWidgetManager.updateAppWidget(componentName,remoteViews);

            appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
    private void initListener(Context context,AppWidgetManager appWidgetManager){

        int[] appWidgetIds=appWidgetManager.getAppWidgetIds(new ComponentName(context,NoteWidgetProvider.class));
        Log.i(TAG,"initListener widgetIdCount "+appWidgetIds.length);
        // ArrayList<Integer> widgetIdList=new ArrayList<Integer>();
        // for (int i=0;i<appWidgetIds.length;i++) {
        //   widgetIdList.add(appWidgetIds[i]);
        //  }
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.adapterviewfilpper);

            Intent viewFlipperServiceIntent = new Intent(context, AdapterViewFlipperService.class);
            //viewFlipperServiceIntent.putIntegerArrayListExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,widgetIdList);
            //在onReceive中放置appWidgetIds，才可以在AdapterViewFlipperService中获取到，不知道为什么？
            viewFlipperServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
            viewFlipperServiceIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            remoteViews.setRemoteAdapter(R.id.page_flipper, viewFlipperServiceIntent);

            Intent nextIntent = new Intent(context,NoteWidgetProvider.class);
            nextIntent.setAction(NEXT_ACTION);
            nextIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent btnPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.next, btnPendingIntent);

            Intent previousIntent = new Intent();
            previousIntent.setAction(PREVIOUS_ACTION);
            previousIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            previousIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent prePendingIntent = PendingIntent.getBroadcast(context, appWidgetId, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.previous, prePendingIntent);

            Intent flipperIntent = new Intent();
            flipperIntent.setAction(COLLECTION_VIEW_ACTION);
            flipperIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            flipperIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            Log.d(TAG,"update collection putExtra widgetId"+appWidgetId);

            PendingIntent filpperPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, flipperIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setPendingIntentTemplate(R.id.page_flipper, filpperPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }


    }
    /**
     * Implements { BroadcastReceiver#onReceive} to dispatch calls to the various
     * other methods on AppWidgetProvider.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        int pageId;
        int widgetId;
        DatabaseManager databaseManager=new DatabaseManager(context);
        AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
        initListener(context,appWidgetManager);
        widgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
        Log.e(TAG,"provider onReceive "+action);
        if(action.equals(NEXT_ACTION)){
            int topPageItemId =getTopPageId(context,databaseManager,widgetId)+1;
            if(topPageItemId==10){
                topPageItemId=1;
            }else {
                topPageItemId++;
            }

            RemoteViews rv=new RemoteViews(context.getPackageName(),R.layout.adapterviewfilpper);
            rv.showNext(R.id.page_flipper);
            rv.setTextViewText(R.id.current_page, String.valueOf(topPageItemId));
            //下面这种写法，如果有多个widget实例时，会同时更新所有的widget，页码都会增加
            //ComponentName componentName=new ComponentName(context,NoteWidgetProvider.class);
            //appWidgetManager.updateAppWidget(componentName,rv);

            //下面这种写法会部分更新，即只更新id为AppWidgetManager.EXTRA_APPWIDGET_ID的widget实例
            appWidgetManager.partiallyUpdateAppWidget(
                    intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID),rv);
            databaseManager.topPageToTrue(widgetId,topPageItemId-1);
        }
        if(action.equals(PREVIOUS_ACTION)){
            int topPageItemId =getTopPageId(context,databaseManager,widgetId)+1;
            if(topPageItemId==1){
                topPageItemId=10;
            }else {
                topPageItemId--;
            }
            RemoteViews preRemoteViews=new RemoteViews(context.getPackageName(),R.layout.adapterviewfilpper);
            preRemoteViews.showPrevious(R.id.page_flipper);
            preRemoteViews.setTextViewText(R.id.current_page, String.valueOf(topPageItemId));
            appWidgetManager.partiallyUpdateAppWidget(
                    intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID),preRemoteViews);
            databaseManager.topPageToTrue(widgetId,topPageItemId-1);
        }
        if(action.equals(COLLECTION_VIEW_ACTION)){

            pageId=intent.getIntExtra(COLLECTION_VIEW_EXTRA,0);
            widgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.e(TAG,"receive collection action widgetId "+widgetId);

            Intent startActivity=new Intent(context, EditNoteActivity.class);
            startActivity.putExtra(PAGE_ID,pageId);
            startActivity.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetId);
            //由于不是在activity中启动另一个activity，而是由context启动，需要设置FLAG_ACTIVITY_NEW_TASK标志
            //表明启动一个activity
            startActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startActivity);
        }
        if(action.equals(DATA_CHANGED_ACTION)){
            Log.d(TAG,"Action _appwidget_update");
            pageId=intent.getIntExtra(NoteWidgetProvider.PAGE_ID,0);
            widgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
            //ComponentName componentName=new ComponentName(context,NoteWidgetProvider.class);




            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.adapterviewfilpper);

            Intent viewFlipperServiceIntent = new Intent(context, AdapterViewFlipperService.class);
            viewFlipperServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetId);
            viewFlipperServiceIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            remoteViews.setRemoteAdapter(R.id.page_flipper, viewFlipperServiceIntent);

            appWidgetManager.notifyAppWidgetViewDataChanged(widgetId,R.id.page_flipper);

            appWidgetManager.updateAppWidget(widgetId,remoteViews);
        }
        if(action.equals(Intent.ACTION_SHUTDOWN)){
            Log.d(TAG,"shutdown");
            databaseManager.topPageInit();
        }
        super.onReceive(context, intent);
    }
    private int getTopPageId(Context context,DatabaseManager databaseManager,int widgetId){
        NoteItem noteItem;
        noteItem=databaseManager.getItem(databaseManager.queryTopPageItem(widgetId));
        Log.e(TAG,"topPageId "+noteItem.getPageId());
        databaseManager.topPageToFalse(widgetId);
        return noteItem.getPageId();
    }
    private void initWidgetDatabase(Context context,AppWidgetManager appWidgetManager,int widgetId){
        DatabaseManager databaseManager=new DatabaseManager(context);
        if(databaseManager.queryItemByWidgetId(widgetId).getCount()!=0){
            Log.e(TAG,"该widget已经生成过啦");
            int topPage=databaseManager.getTopPageId(widgetId);
            RemoteViews showTopPage=new RemoteViews(context.getPackageName(),R.layout.adapterviewfilpper);
            showTopPage.setDisplayedChild(R.id.page_flipper,topPage);
            //databaseManager.closeDB();
        }else {
            for(int i=0;i<10;i++){
                NoteItem noteItem=new NoteItem("喂！我是标题");
                noteItem.setContent("小提示：可通过底部左右箭头翻页！");
                noteItem.setPageId(i);
                if (i==0){
                    noteItem.setFavorite(1);
                }
                noteItem.setWidgetId(widgetId);
                noteItem.setWritingDate(DateToStringUtils.dateToString(new Date(System.currentTimeMillis())));
                databaseManager.addItem(noteItem);
                //databaseManager.closeDB();
            }
            databaseManager.changedFlagToTrue(widgetId,0);
            databaseManager.closeDB();
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetId,R.id.page_flipper);
        }
    }
    // private void updateChangedItem(Context context,int widgetId){
    // DatabaseManager databaseManager=new DatabaseManager(context);
    // }
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.e(TAG,"ondeleted"+appWidgetIds.length);
        DatabaseManager databaseManager=new DatabaseManager(context);
        for(int appwidgetId : appWidgetIds){
            Log.e(TAG,"delete widgetId "+appwidgetId);
            databaseManager.deleteItemsByWidgetId(appwidgetId);
        }
        super.onDeleted(context, appWidgetIds);
    }

}

