package ru.vadim_manaenko.ethwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vadim on 17.03.18.
 */

public class EthWidgetProvider extends AppWidgetProvider {
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000*3,
                        60000, pendingIntent);
    }

    @Override
    public void onDisabled(Context context) {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);
        super.onDisabled(context);
    }

    static void updateAppWidget(final Context context,final AppWidgetManager appWidgetManager,final int appWidgetId) {
        RequestSingleton.getInstance(context).fetchData(new VolleyCallback() {
            @Override
            public void onSuccessRequest(JSONArray result) {
                Log.i("Response", result.toString());
                String price = "";
                try {
                    JSONObject etherObject = result.getJSONObject(0);
                    price = etherObject.getString("price_usd");
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                }
                Log.i("Price", price);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ethwidget);
                views.setTextViewText(R.id.price_text, "$" + price);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        });
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int appId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appId);
        }
    }
}
