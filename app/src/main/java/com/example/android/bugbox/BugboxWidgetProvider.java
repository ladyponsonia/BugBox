package com.example.android.bugbox;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.example.android.bugbox.adapters.BugAdapter;
import com.google.gson.Gson;

/**
 * Implementation of App Widget functionality.
 */
public class BugboxWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        //get number of bugs to display in widget
        int bugsCollected = getBugsNumber(context);
        CharSequence widgetText = String.valueOf(bugsCollected);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bugbox_widget_provider);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        //on click open app
        //from https://stackoverflow.com/questions/3589741/how-to-open-a-application-from-widget-in-android
        Intent intent = new Intent(context, BugsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        //attach an on-click listener
        views.setOnClickPendingIntent(R.id.widget_iv, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static int getBugsNumber(Context context) {
        int numberOfBugs;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(prefs.contains(MyBugsFragment.BUG_NUMBER_KEY)) {
            numberOfBugs = prefs.getInt(MyBugsFragment.BUG_NUMBER_KEY, 0);
        }else{
            numberOfBugs = 0;
        }
        return numberOfBugs;
    }
}

