package edu.centenary.cogmality;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class TradeAnswerReceiver extends BroadcastReceiver {
	private static final String TAG = "TradeOfferReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String action = intent.getAction();
			String channel = intent.getExtras().getString("com.parse.Channel");
			JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
			String type = json.getString("mailtype");
			Log.d(TAG, "received action " + action + " on channel " + channel + " with extras:");
			Iterator itr = json.keys();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				Log.d(TAG, "..." + key + " => " + json.getString(key));
			}

			CogmalitySQLHelper steamdb = new CogmalitySQLHelper(context);
			steamdb.mailReceived(context, intent.getExtras().getString("com.parse.Data"), type);
			
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
			int icon = R.drawable.ic_launcher;
			CharSequence tickerText = "Trade Answer";
			long when = System.currentTimeMillis();

			Notification notification = new Notification(icon, tickerText, when);
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			CharSequence contentTitle = json.getString("userfrom") + " answered your trade offer.";
			CharSequence contentText = "Trade Answer";
			Intent notificationIntent = new Intent(context, CogmalityActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("tabState", "Mail");
			notificationIntent.putExtras(bundle);

			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			
			mNotificationManager.notify(CogmalitySQLHelper.ANSWER_NOTIFICATION, notification);
			

		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}
	}
}