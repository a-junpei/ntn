package jp.junpei1982.android.nexttrainnotifier;
/**
 * 
 */


public class NextTrainRecord {
	int hour;
	int min;
	String formatttedStr;
	
	public NextTrainRecord(int hour, int min, String[] notes) {
		this.hour = hour;
		this.min = min;
		formatttedStr = String.format("%02d:%02d", hour, min);
		for (String note : notes) {
			formatttedStr += " " + note;
		}
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
}