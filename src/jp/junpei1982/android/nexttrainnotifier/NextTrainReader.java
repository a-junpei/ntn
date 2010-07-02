package jp.junpei1982.android.nexttrainnotifier;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NextTrainReader {
	public static List<NextTrainTable> loadTBLFile(String fileName) {
		List<NextTrainTable> result = new ArrayList<NextTrainTable>();
		boolean flag = false;
		String title = "";
		NextTrainRecord[][] records = new NextTrainRecord[24][];
		Map<Character, String> notesTable = new HashMap<Character, String>();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "SJIS"));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.length() == 0 || line.startsWith(";")) {
					// 空行 or ';' で始まる行はコメントなので何もしない
				} else if (line.startsWith("[")) {
					// 曜日情報の読み込みはしない。平日ダイヤと休日ダイヤとして扱う
					if (flag) {
						result.add(new NextTrainTable(title, records));
						// 作業用領域をクリア
						title = "";
						records = new NextTrainRecord[24][];
					} else {
						flag = true;
					}
				} else if (line.startsWith("# ")) {
					// "# "に続く部分がタイトル
					title = line.substring(2);
				} else {
					String[] values = line.split(":");
					if (Character.isLetter(values[0].charAt(0))) {
						// 備考をテーブルに
						notesTable.put(values[0].charAt(0), values[1].substring(0, values[1].indexOf(";")));
					} else {
						int h = Integer.valueOf(values[0]);
						// 最初の' 'を抜いて渡す
						records[h] = parseRecordList(h, values[1].substring(1),
								notesTable);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.add(new NextTrainTable(title, records));
		return result;
	}
	
	private static NextTrainRecord[] parseRecordList(int h, String data,
			Map<Character, String> notesTable) {
		String[] values = data.split(" ");
		NextTrainRecord[] result = new NextTrainRecord[values.length];

		for (int i = 0; i < values.length; i++) {
			// 備考読み込んでテーブルで変換(最後の2文字は分なので除外)
			String[] notes = new String[values[i].length() - 2];
			for (int j = 0; j < notes.length; j++) {
				notes[j] = notesTable.get(values[i].charAt(j));
			}
			result[i] = new NextTrainRecord(h, Integer.valueOf(values[i]
					.substring(values[i].length() - 2)), notes);
		}

		return result;
	}
}
