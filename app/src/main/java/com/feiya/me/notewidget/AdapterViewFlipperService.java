package com.feiya.me.notewidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

/**
 * Created by feiya on 2016/9/15.
 */
public class AdapterViewFlipperService extends RemoteViewsService {
    private Context mContext;
    private int mWidgetId;
    private static final String TAG=AdapterViewFlipperService.class.getSimpleName();
    private static final int pagesCount=10;
    private ArrayList<NoteItem> noteItems=new ArrayList<NoteItem>();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG,"onGetViewFactory");
        return new FlipperViewFactory(this,intent);
    }
    private class FlipperViewFactory implements RemoteViewsService.RemoteViewsFactory{

        public FlipperViewFactory(Context context,Intent intent){
            mContext=context;
            mWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.d(TAG,"Factory "+mWidgetId);

        }

        /**
         *
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return pagesCount;
        }

        /**
         *
         *
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
         *
         * <p>
         * Note: expensive tasks can be safely performed synchronously within this method, and a
         * loading view will be displayed in the interim. See {@link #getLoadingView()}.
         *
         * @param position The position of the item within the Factory's data set of the item whose
         *                 view we want.
         * @return A RemoteViews object corresponding to the data at the specified position.
         */
        @Override
        public RemoteViews getViewAt(int position) {
            Log.i(TAG,"getView "+position);
            RemoteViews remoteViews=new RemoteViews(getPackageName(),R.layout.notepage1);
            remoteViews.setTextViewText(R.id.note_title,noteItems.get(position).getTitle());

            Intent collectionIntent=new Intent();
            collectionIntent.putExtra(NoteWidgetProvider.COLLECTION_VIEW_EXTRA,position);

            remoteViews.setOnClickFillInIntent(R.id.note_title,collectionIntent);
            remoteViews.setOnClickFillInIntent(R.id.note_content,collectionIntent);

            return remoteViews;
        }

        /**
         *
         *
         * @return The number of types of Views that will be returned by this factory.
         */
        @Override
        public int getViewTypeCount() {
            return 1;
        }

        /**
         *
         *
         * @return True if the same id always refers to the same object.
         */
        @Override
        public boolean hasStableIds() {
            return true;
        }

        /**
         * Called when your factory is first constructed. The same factory may be shared across
         * multiple RemoteViewAdapters depending on the intent passed.
         */
        @Override
        public void onCreate() {
            Log.i(TAG,"onCreate");
            for(int i=0;i<pagesCount;i++){
                noteItems.add(new NoteItem("page "+i));
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
            Log.i(TAG,"onDataSetChanged");
        }

        /**
         * Called when the last RemoteViewsAdapter that is associated with this factory is
         * unbound.
         */
        @Override
        public void onDestroy() {
            Log.i(TAG,"onDestroy");
        }
    }
}
