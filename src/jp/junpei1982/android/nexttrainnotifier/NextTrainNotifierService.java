package jp.junpei1982.android.nexttrainnotifier;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

public class NextTrainNotifierService extends Service {

	public static final String ACTION_START = "NextTrainNotifierService_Start";
	public static final String ACTION_STOP  = "NextTrainNotifierService_Stop";
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		if (ACTION_START.equals(intent.getAction())) {
			NextTrainTable table = (NextTrainTable) intent.getExtras().get("table");
			notifyNextTrain(table);
			setAlarm(table);
		} else if (ACTION_STOP.equals(intent.getAction())) {
			cancelAlarm();
			stopNotifyNextTrain();
		}
	}

	private void cancelAlarm() {
		AlarmManager alarmManager = (AlarmManager) (getApplicationContext().getSystemService(ALARM_SERVICE));

		Intent intent = new Intent(getApplicationContext(),
				NextTrainNotifierReceiver.class);
		intent.setAction(NextTrainNotifierReceiver.ACTION_UPDATE);
		PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

		alarmManager.cancel(sender);
	}

	private void setAlarm(NextTrainTable table) {
		AlarmManager am = (AlarmManager) (getApplicationContext().getSystemService(ALARM_SERVICE));

		Intent intent = new Intent(getApplicationContext(),
				NextTrainNotifierReceiver.class);
		intent.setAction(NextTrainNotifierReceiver.ACTION_UPDATE);
		intent.putExtra("table", table);
		PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
		
		// 次の1分の00秒にセット
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 1);
		cal.set(Calendar.SECOND, 0);
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
	}

	private void notifyNextTrain(NextTrainTable table) {
		NextTrainRecord record1 = table.getNextRecord();
		NextTrainRecord record2 = table.getNextRecord(record1);
		NextTrainRecord record3 = table.getNextRecord(record2);

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		long now = System.currentTimeMillis();
		Notification notification = new Notification(R.drawable.icon, getResources().getText(R.string.app_name), now);
		Intent intent = new Intent(this, NextTrainNotifierActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		notification.contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.notification);
		contentView.setTextViewText(R.id.title, "[時刻表] " + table.getTitle());
		contentView.setTextViewText(R.id.body, record1.toString()
				+ record2.toString() + record3.toString());
		contentView.setTextViewText(R.id.when, new SimpleDateFormat("kk:mm")
				.format(new Date(now)));
		notification.contentView = contentView;
		
		notificationManager.notify(R.string.app_name, notification);
	}
	
	private void stopNotifyNextTrain() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(R.string.app_name);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null; // 実装しない
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cancelAlarm();
		stopNotifyNextTrain();
	}
}
