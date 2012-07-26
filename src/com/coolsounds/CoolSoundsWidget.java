package com.coolsounds;
/**
 * this is not fully implemented.... sorry... 
 * */
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class CoolSoundsWidget extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            
            // Create an Intent to launch ExampleActivity
			Intent intent = new Intent(context, PlaySoundService.class);
			//PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				//	intent, 0);
			PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
			


			// Get the layout for the App Widget and attach an on-click listener
			// to the button
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.widget);
			views.setOnClickPendingIntent(R.id.btn_widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current App Widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
