package jp.junpei1982.android.nexttrainnotifier;

public class Utils {
	/**
	 * 文字列strの長さがrepeatより小さい時、repeatと同じ長さになるまでpadCharを左側に詰める
	 * repeat以上の場合、そのまま返す
	 * 
	 * @param str
	 * @param repeat
	 * @param padChar
	 * @return
	 */
	public static String padding(String str, int repeat, char padChar) {
		StringBuilder result = new StringBuilder();
		
		int len = str.length();
		if (repeat > len) {
			for (int i = 0; i < (repeat-len); i++) {
				result.append(padChar);
			}
		}
		result.append(str);
		
		return result.toString();
	}
}
