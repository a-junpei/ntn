package jp.junpei1982.android.nexttrainnotifier;
import java.util.Calendar;




public class NextTrainTable {
	private String title;
	private NextTrainRecord[][] records;

	NextTrainTable(String title, NextTrainRecord[][] records) {
		this.title = title;
		this.records = records;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * 現在時刻から次のレコードをさがす
	 * 
	 * @return
	 */
	public NextTrainRecord getNextRecord() {
		return getNextRecord(Calendar.getInstance());
	}

	/**
	 * 指定時刻から次のレコードをさがす
	 * 
	 * @param calendar
	 * @return
	 */
	public NextTrainRecord getNextRecord(Calendar calendar) {
		return getNextRecord(
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE));
	}
	
	/**
	 * 指定したレコードの次をさがす
	 * 
	 * @param record
	 * @return
	 */
	public NextTrainRecord getNextRecord(NextTrainRecord record) {
		return getNextRecord(
				record.getHour(),
				record.getMin());
	}

	private NextTrainRecord getNextRecord(int h, int m) {
		if (records[h] != null) {
			for (NextTrainRecord record : records[h]) {
				if (m < record.getMin()) {
					return record;
				}
			}
		}
		
		// 同じ時間内に電車なしor該当するのがない場合、次の1時間の最初の電車
		return getNextHourRecord(h);
	}
	
	private NextTrainRecord getNextHourRecord(int h) {
		if (h == 23) { // 23時の次は0時
			h = 0;
		} else {
			h++;
		}
		
		if (records[h] != null) {
			return records[h][0];
		} else { // この1時間には電車がないのでさらに次をさがす
			return getNextHourRecord(h);
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[" + title + "]\n");
		for (int i = 0; i < records.length; i++) {
			if (records[i] != null) { // FIXME 電車が来ない時間の考慮
				for (int j = 0; j < records[i].length; j++) {
					result.append(records[i][j].toString());
				}
			}
		}
		return result.toString();
	}
}
