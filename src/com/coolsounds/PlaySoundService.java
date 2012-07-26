package com.coolsounds;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

public class PlaySoundService extends Service {

	private SoundController sp;
	public void onStart(Intent intent, int startId)
    {

        // Build the widget update
        RemoteViews updateViews = buildUpdate(this);

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(this, CoolSoundsWidget.class);

        // AppWidgetManager updates AppWidget state; gets information about 
        // installed AppWidget providers and other AppWidget related state 
        AppWidgetManager manager = AppWidgetManager.getInstance(this);

        // Updates the views based on the RemoteView returned from the
        // buildUpdate method (stored in updateViews)
        manager.updateAppWidget(thisWidget, updateViews);
        sp = SoundController.getSoundController(this);
        sp.initSound(R.raw.drama);
    }

	 public RemoteViews buildUpdate(Context context)
     {
         RemoteViews updateViews = 
             new RemoteViews(context.getPackageName(), R.layout.widget);
         return updateViews;
     }

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
