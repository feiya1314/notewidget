package com.feiya.me.notewidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class NoteWidgetProvider extends AppWidgetProvider {

    private static final String TAG=NoteWidgetProvider.class.getSimpleName();

    public static final String NEXT_ACTION="com.feiya.me.notewidget.NEXT_ACTION";
    public static final String PREVIOUS_ACTION="com.feiya.me.notewidget.PREVIOUS_ACTION";
    public static final String COLLECTION_VIEW_ACTION="com.feiya.me.notewidget.COLLECTION_VIEW_ACTION";
    public static final String COLLECTION_VIEW_EXTRA="com.feiya.me.notewidget.COLLECTION_VIEW_EXTRA";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG,"Provider onUpdate");

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
           RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.adapterviewfilpper);

            Intent viewFlipperServiceIntent=new Intent(context,AdapterViewFlipperService.class);
            remoteViews.setRemoteAdapter(R.id.page_flipper,viewFlipperServiceIntent);

            Intent nextIntent=new Intent();
            nextIntent.setAction(NEXT_ACTION);
            nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent btnPendingIntent=PendingIntent.getBroadcast(context,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.next,btnPendingIntent);

            Intent previousIntent=new Intent();
            previousIntent.setAction(PREVIOUS_ACTION);
            previousIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent prePendingIntent=PendingIntent.getBroadcast(context,0,previousIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.previous,prePendingIntent);

            Intent flipperIntent=new Intent();
            flipperIntent.setAction(COLLECTION_VIEW_ACTION);
            flipperIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);

            PendingIntent filpperPendingIntent=PendingIntent.getBroadcast(context,0,flipperIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setPendingIntentTemplate(R.id.page_flipper,filpperPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
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
        AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
        Log.d(TAG,"provider onReceivr"+action);
        if(action.equals(NEXT_ACTION)){
            RemoteViews rv=new RemoteViews(context.getPackageName(),R.layout.adapterviewfilpper);
            rv.showNext(R.id.page_flipper);
            appWidgetManager.partiallyUpdateAppWidget(
                    intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID),rv);
            Toast.makeText(context,"touched next button ",Toast.LENGTH_SHORT).show();
        }
        if(action.equals(PREVIOUS_ACTION)){
            RemoteViews preRemoteViews=new RemoteViews(context.getPackageName(),R.layout.adapterviewfilpper);
            preRemoteViews.showPrevious(R.id.page_flipper);
            appWidgetManager.partiallyUpdateAppWidget(
                    intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID),preRemoteViews);
            Toast.makeText(context,"touched next button ",Toast.LENGTH_SHORT).show();
        }
        if(action.equals(COLLECTION_VIEW_ACTION)){
            int pageId=intent.getIntExtra(COLLECTION_VIEW_EXTRA,0);
            Intent startActivity=new Intent(context,EditNoteActivity.class);
            startActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startActivity);

            Toast.makeText(context,"touched View "+pageId,Toast.LENGTH_SHORT).show();
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

