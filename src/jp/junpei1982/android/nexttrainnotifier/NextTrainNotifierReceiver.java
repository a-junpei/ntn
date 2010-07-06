/**
 * 
 */
package jp.junpei1982.android.nexttrainnotifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NextTrainNotifierReceiver extends BroadcastReceiver {

	public static final String ACTION_UPDATE = "NextTrainNotifierService_Update";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (ACTION_UPDATE.equals(intent.getAction())) {
			startNextTrainNotifierService(context, (NextTrainTable)intent.getExtras().get("table"));
		}
	}

	private void startNextTrainNotifierService(Context context, NextTrainTable table) {
		Intent intent = new Intent(context, NextTrainNotifierService.class);
		intent.setAction(NextTrainNotifierService.ACTION_START);
		intent.putExtra("table", table);
		context.startService(intent);
	}
}