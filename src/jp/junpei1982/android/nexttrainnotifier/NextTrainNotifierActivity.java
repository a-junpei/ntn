package jp.junpei1982.android.nexttrainnotifier;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class NextTrainNotifierActivity extends Activity {

	private static final String TBL_LIST_PATH = Environment.getExternalStorageDirectory() + "/NextTrainNotifier";
	
	private List<NextTrainTable> nextTrainTableList;
	private RadioGroup radioGroup;
	private Button startButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);

		radioGroup = (RadioGroup) findViewById(R.id.RadioGroup01);
		loadTBLListFromSD();

		startButton = (Button) findViewById(R.id.StartButton);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NextTrainNotifierActivity.this,
						NextTrainNotifierService.class);
				intent.setAction(NextTrainNotifierService.ACTION_START);
				intent.putExtra("table",
						nextTrainTableList.get(radioGroup
								.getCheckedRadioButtonId()));
				startService(intent);
			}
		});
		Button stopButton = (Button) findViewById(R.id.StopButton);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NextTrainNotifierActivity.this,
						NextTrainNotifierService.class);
				intent.setAction(NextTrainNotifierService.ACTION_STOP);
				startService(intent);
			}
		});
		Button reloadButton = (Button) findViewById(R.id.ReloadButton);
		reloadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadTBLListFromSD();
			}
		});
	}

	private void loadTBLListFromSD() {
		// ファイルから読み込みを非同期実行
		new AsyncTask<Void, String, Void>() {
			private ProgressDialog dialog;
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				this.dialog = new ProgressDialog(NextTrainNotifierActivity.this);
				this.dialog.setTitle("時刻表データを読み込み中です");
				this.dialog.setMessage("---");
				this.dialog.setIndeterminate(false);
				this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				this.dialog.setCancelable(false);
				this.dialog.show();
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				nextTrainTableList = new ArrayList<NextTrainTable>();
				
				File[] files = new File(TBL_LIST_PATH).listFiles(new FilenameFilter(){
					@Override
					public boolean accept(File dir, String filename) {
						return filename.endsWith(".tbl") || filename.endsWith(".TBL");
					}
				});
				if (files != null) {
					for (File file : files) { 
						publishProgress(file.getName());
						nextTrainTableList.addAll(NextTrainReader.loadTBLFile(file.getAbsolutePath())); // 読み込んだ結果を連結
					}
				}
				return null;
			}
			@Override  
			protected void onProgressUpdate(String... progress) {
				this.dialog.setMessage(progress[0]);
			}
			
			@Override
			protected void onPostExecute(Void result) {
				this.dialog.dismiss();
				reflectView(NextTrainNotifierActivity.this);
			}
		}.execute();
	}

	/**
	 * 読み込んだ結果を画面に反映
	 * 
	 * @param context
	 */
	private void reflectView(Context context) {
		radioGroup.clearCheck();
		radioGroup.removeAllViews();

		if (nextTrainTableList.size() == 0) {
			startButton.setEnabled(false); // データなし時は通知開始ボタンは無効
		} else {
			for (int i = 0; i < nextTrainTableList.size(); i++) {
				RadioButton radioButton = new RadioButton(this);
				radioButton.setId(i); // idはindex値をそのまま使う
				radioButton.setText(nextTrainTableList.get(i).getTitle());
				radioButton.setTextColor(Color.WHITE);
				radioGroup.addView(radioButton);
			}
			
			startButton.setEnabled(true);
			radioGroup.check(0);// 1つ以上あれば一番上をチェック
		}
	}
}