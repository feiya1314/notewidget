package com.feiya.me.notewidget.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.feiya.me.notewidget.Constant;
import com.feiya.me.notewidget.R;
import com.feiya.me.notewidget.db.DatabaseManager;
import com.feiya.me.notewidget.model.NoteItem;
import com.feiya.me.notewidget.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by feiya on 2016/9/15.
 */
public class AdapterViewFlipperService extends RemoteViewsService {
    private Context mContext;
    private int mWidgetId;
    private static final String TAG = AdapterViewFlipperService.class.getSimpleName();
    private static final int pagesCount = 10;
    private ArrayList<NoteItem> noteItems = new ArrayList<NoteItem>();
    private DatabaseManager databaseManager;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.e(TAG, "onGetViewFactory");
        return new FlipperViewFactory(this, intent);
    }

    private class FlipperViewFactory implements RemoteViewsFactory {
        private boolean isReboot = false;

        public FlipperViewFactory(Context context, Intent intent) {
            mContext = context;
            mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.e("service widgetid ", String.valueOf(intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 521)));

            databaseManager = DatabaseManager.getInstance(context);
        }

        /**
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return pagesCount;
        }

        /**
         * @param position The position of the item within the data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * This allows for the use of a custom loading view which appears between the time that
         * {@link #getViewAt(int)} is called and returns. If null is returned, a default loading
         * view will be used.
         *
         * @return The RemoteViews representing the desired loading view.
         */
        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        /**
         * Note: expensive tasks can be safely performed synchronously within this method, and a
         * loading view will be displayed in the interim. See {@link #getLoadingView()}.
         *
         * @param position The position of the item within the Factory's data set of the item whose
         *                 view we want.
         * @return A RemoteViews object corresponding to the data at the specified position.
         */
        @Override
        public RemoteViews getViewAt(int position) {
            Log.e(TAG, "getView " + position);

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notepage1);

            if (isReboot) {
                //witchWidgetUpdate=mWidgetIds[updateWidgetPostion];
                //Log.e("witchWidgetUpdate ",String.valueOf(witchWidgetUpdate));
                // Log.e("witchWidgetUpdate ",String.valueOf(witchWidgetUpdate));
                noteItems = databaseManager.getItems(databaseManager.queryItemByWidgetId(mWidgetId));
                isReboot = false;
            }
            remoteViews.setTextViewText(R.id.note_title, noteItems.get(position).getTitle());
            remoteViews.setTextViewText(R.id.note_content, noteItems.get(position).getContent());

            Intent collectionIntent = new Intent();
            collectionIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            collectionIntent.putExtra(Constant.COLLECTION_VIEW_EXTRA, position);

            remoteViews.setOnClickFillInIntent(R.id.note_title, collectionIntent);
            remoteViews.setOnClickFillInIntent(R.id.note_content, collectionIntent);

            return remoteViews;
        }

        /**
         * @return The number of types of Views that will be returned by this factory.
         */
        @Override
        public int getViewTypeCount() {
            return 1;
        }

        /**
         * @return True if the same id always refers to the same object.
         */
        @Override
        public boolean hasStableIds() {
            return false;
        }

        /**
         * Called when your factory is first constructed. The same factory may be shared across
         * multiple RemoteViewAdapters depending on the intent passed.
         */
        @Override
        public void onCreate() {
            Log.e(TAG, "onCreatedebug");
            Log.e(TAG, Utils.dateToString(new Date(System.currentTimeMillis())));
            for (int i = 0; i < pagesCount; i++) {
                NoteItem noteItem = new NoteItem("喂！我是标题");
                noteItem.setContent("小提示：可通过底部左右箭头翻页！");
                noteItem.setPageId(i);
                noteItem.setWidgetId(0);
                noteItem.setWritingDate(Utils.dateToString(new Date(System.currentTimeMillis())));
                noteItems.add(noteItem);
                Log.d(TAG, noteItems.get(i).getContent());
            }
        }

        /**
         * Called when notifyDataSetChanged() is triggered on the remote adapter. This allows a
         * RemoteViewsFactory to respond to data changes by updating any internal references.
         * <p>
         * Note: expensive tasks can be safely performed synchronously within this method. In the
         * interim, the old data will be displayed within the widget.
         *
         * @see AppWidgetManager#notifyAppWidgetViewDataChanged(int[], int)
         */
        @Override
        public void onDataSetChanged() {
            Log.e(TAG, "onDataSetChanged");
            //当某一个widget的某一页有改变时，该页的changedFlag状态会改变，通过查询该Flag就可找出要更新的widget
            NoteItem noteItem = databaseManager.getItem(databaseManager.queryChangedItem());
            if (noteItem != null) {
                noteItems = databaseManager.getItems(databaseManager.queryItemByWidgetId(noteItem.getWidgetId()));
                Log.d(TAG, "noteItems length" + noteItems.size());
                databaseManager.changedFlagToFalse();
            } else {
                Log.e(TAG, "onDataSetChanged " + String.valueOf(mWidgetId));
                isReboot = true;

            }
            //for(int i=0;i<pagesCount;i++){
            //  noteItems.set(i,databaseManager.getItem(databaseManager.queryItem(i)));
            //   Log.d(TAG,noteItems.get(i).getContent());
            // }
        }

        /**
         * Called when the last RemoteViewsAdapter that is associated with this factory is
         * unbound.
         */
        @Override
        public void onDestroy() {
            Log.i(TAG, "onDestroy");
            databaseManager.close();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_REDELIVER_INTENT;
    }
}
