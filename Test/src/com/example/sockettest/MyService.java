package com.example.sockettest;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

public class MyService extends Service {

	private int serviceId;
	// private int notificationId = 2;
	public final IBinder mBinder = new LocalBinder();
	private NotificationManager mNM;
	Intent intent;
	private Messenger serviceMessenger;

	// private int NOTIFICATION = 5;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		// Toast.makeText(getApplicationContext(), "Service is started",
		// Toast.LENGTH_SHORT).show();

		// sendMessage("The service is created");
		// mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Display a notification about us starting. We put an icon in the
		// status bar.
		// showNotification();
	}

	@Override
	public void onDestroy() {
		sendMessage("Service Destroyed");
		// Cancel the persistent notification.
		// mNM.cancel(serviceId);
		// Tell the user we stopped.
		// Toast.makeText(this, "Service is stopped",
		// Toast.LENGTH_SHORT).show();
	}

	public void sendMessage(String string) {

		try {
			Bundle bundle = new Bundle();
			bundle.putString("message", string);

			Message message = new Message();
			message.setData(bundle);

			serviceMessenger.send(message);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle extras = intent.getExtras();
		serviceMessenger = (Messenger) extras.get("ActMessenger");
		sendMessage("Service Created");
		serviceId = startId;
		return Service.START_NOT_STICKY;
	}

	/**
	 * Show a notification while this service is running.
	 */
	// private void showNotification() {
	// NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
	// this).setSmallIcon(R.drawable.ic_launcher)
	// .setContentTitle("MyService")
	// .setContentText("Service Has Started");
	// // Creates an explicit intent for an Activity in your app
	// Intent resultIntent = new Intent(this, MainActivity.class);
	//
	// // The stack builder object will contain an artificial back stack for
	// // the
	// // started Activity.
	// // This ensures that navigating backward from the Activity leads out of
	// // your application to the Home screen.
	// TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
	// // Adds the back stack for the Intent (but not the Intent itself)
	// stackBuilder.addParentStack(MainActivity.class);
	// // Adds the Intent that starts the Activity to the top of the stack
	// stackBuilder.addNextIntent(resultIntent);
	// PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
	// PendingIntent.FLAG_UPDATE_CURRENT);
	// mBuilder.setContentIntent(resultPendingIntent);
	// NotificationManager mNotificationManager = (NotificationManager)
	// getSystemService(Context.NOTIFICATION_SERVICE);
	// // mId allows you to update the notification later on.
	//
	// mNotificationManager.notify(serviceId, mBuilder.build());
	// }

	public class LocalBinder extends Binder {
		MyService getService() {
			return MyService.this;
		}
	}
}
