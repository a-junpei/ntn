package jp.junpei1982.android.nexttrainnotifier;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 */


public class NextTrainRecord implements Parcelable{
	int hour;
	int min;
	String formatttedStr;

	public static final Parcelable.Creator<NextTrainRecord> CREATOR = new Parcelable.Creator<NextTrainRecord>() {
		public NextTrainRecord createFromParcel(Parcel in) {
			return new NextTrainRecord(in);
		}

		public NextTrainRecord[] newArray(int size) {
			return new NextTrainRecord[size];
		}
	};

	NextTrainRecord(int hour, int min, String[] notes) {
		this.hour = hour;
		this.min = min;
		
		buildFormatttedStr(hour, min, notes);
	}

	/**
	 * 次の形式の文字列をつくる
	 * hh:mm [半角スペース区切りの備考]
	 * 
	 * @param hour
	 * @param min
	 * @param notes
	 */
	private void buildFormatttedStr(int hour, int min, String[] notes) {
		//String.format()は重いので手動で0パディング
		formatttedStr = Utils.padding(String.valueOf(hour), 2, '0') + ":" + Utils.padding(String.valueOf(min), 2, '0');
		for (String note : notes) {
			formatttedStr += " " + note;
		}
		formatttedStr = Utils.truncate(formatttedStr, 25);
	}

	NextTrainRecord(Parcel in) {
		this.hour = in.readInt();
		this.min = in.readInt();
		this.formatttedStr = in.readString();
	}

	public int getHour() {
		return hour;
	}
	
	public int getMin() {
		return min;
	}
	
	public String toString() {
		return formatttedStr + "\n";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(this.hour);
		out.writeInt(this.min);
		out.writeString(this.formatttedStr);
	}

}