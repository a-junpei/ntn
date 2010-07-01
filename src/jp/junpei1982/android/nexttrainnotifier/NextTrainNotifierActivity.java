package jp.junpei1982.android.nexttrainnotifier;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.Toast;

public class NextTrainNotifierActivity extends Activity {
	private final int REPEAT_INTERVAL = 60 * 1000; // 1分

	private Handler handler = new Handler();
	private Runnable runnable;

	private List<NextTrainTable> nextTrainTableList;

	private RadioGroup radioGroup;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Toast.makeText(this, "起動しました。", Toast.LENGTH_SHORT).show();

		// TODO 再読込をアプリ起動中にできたほうが便利かも
		nextTrainTableList = loadTBLFilesFromSD();

		setContentView(R.layout.main);
		// TODO スタート/ストップボタンはトグルボタンにする
		Button startButton = (Button) findViewById(R.id.start);
		startButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				runnable = new Runnable() {
					@Override
					public void run() {
						notifyNextTrain();
						handler.postDelayed(this, REPEAT_INTERVAL);
					}
				};
				handler.postDelayed(runnable, 0);
			}
		});
		Button stopButton = (Button) findViewById(R.id.stop);
		stopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stopNotifyNextTrain();
			}
		});
		
		radioGroup = (RadioGroup) findViewById(R.id.RadioGroup01);
		for (int i = 0; i < nextTrainTableList.size(); i++) {
			RadioButton radioButton = new RadioButton(this);
			radioButton.setId(i);
			radioButton.setText(nextTrainTableList.get(i).getTitle());
			radioButton.setTextColor(Color.WHITE);
			radioGroup.addView(radioButton);
		}
		if (radioGroup.getChildCount() > 0) { // 1つ以上あれば一番上をチェック
			radioGroup.check(0);
		}
	}

	private List<NextTrainTable> loadTBLFilesFromSD() {
		List<NextTrainTable> result = new ArrayList<NextTrainTable>();
		
		File[] files = new File("/sdcard/NextTrainNotifier").listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".tbl") || filename.endsWith(".TBL");
			}
		});
		if (files != null) {
			for (File file : files) { 
				result.addAll(NextTrainReader.loadTBLFile(file.getAbsolutePath())); // 読み込んだ結果を連結
			}
		}
		return result;
	}

	private void notifyNextTrain() {
//		Log.d("###", "notifyNextTrain() called.");

		NextTrainTable table = nextTrainTableList.get(radioGroup.getCheckedRadioButtonId());
		NextTrainRecord record1 = table.getNextRecord();
		NextTrainRecord record2 = table.getNextRecord(record1);
		NextTrainRecord record3 = table.getNextRecord(record2);

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(R.string.app_name);

		long now = System.currentTimeMillis();
		Notification notification = new Notification(R.drawable.icon, getResources().getText(R.string.app_name), now);
		Intent intent = new Intent(this, NextTrainNotifierActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		notification.contentIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		
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

	@Override
	protected void onDestroy() {
		stopNotifyNextTrain();

//		Toast.makeText(this, "終了しました。", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	private void stopNotifyNextTrain() {
		handler.removeCallbacks(runnable);

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(R.string.app_name);
	}
}