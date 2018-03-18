package ru.vadim_manaenko.ethwidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vadim on 18.03.18.
 */

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EthWidget");
        wl.acquire(10*60*1000); // 10 minutes

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
                ComponentName thisWidget = new ComponentName(context, EthWidgetProvider.class);
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                manager.updateAppWidget(thisWidget, views);
            }
        });

        wl.release();
    }
}
