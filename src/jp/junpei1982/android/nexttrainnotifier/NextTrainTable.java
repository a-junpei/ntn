package jp.junpei1982.android.nexttrainnotifier;
import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;




public class NextTrainTable implements Parcelable {
	private String title;
	private NextTrainRecord[][] records;

	public static final Parcelable.Creator<NextTrainTable> CREATOR = new Parcelable.Creator<NextTrainTable>() {
		public NextTrainTable createFromParcel(Parcel in) {
			return new NextTrainTable(in);
		}

		public NextTrainTable[] newArray(int size) {
			return new NextTrainTable[size];
		}
	};

	NextTrainTable(Parcel in) {
		this.title = in.readString();
		this.records = new NextTrainRecord[24][];
		for (int i = 0; i < 24; i++) {
			int len = in.readInt();
			if (len != 0) {
				this.records[i] = new NextTrainRecord[len];
				for (int j = 0; j < len; j++) {
					this.records[i][j] = in.readParcelable(NextTrainRecord.class.getClassLoader());
				}
			}
		}
	} 

	NextTrainTable(String title, NextTrainRecord[][] records) {
		this.title = title;
		this.records = records;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.title);
		for (int i = 0; i < 24; i++) {
			if (this.records[i] != null) {
				int len = this.records[i].length;
				out.writeInt(len);
				for (int j = 0; j < len; j++) {
					out.writeParcelable(this.records[i][j], 0);
				}
			} else {
				out.writeInt(0);
			}
		}
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
	
	// FIXME 空データの場合など、丸一日さがしてもない場合を考慮する
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
			if (records[i] != null) { // 電車が来ない時間の考慮
				for (int j = 0; j < records[i].length; j++) {
					result.append(records[i][j].toString());
				}
			}
		}
		return result.toString();
	}
}
