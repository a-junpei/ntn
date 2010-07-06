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
		formatttedStr = String.format("%02d:%02d", hour, min);
		for (String note : notes) {
			formatttedStr += " " + note;
		}
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