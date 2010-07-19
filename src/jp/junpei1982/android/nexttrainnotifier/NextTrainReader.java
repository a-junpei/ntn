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
		String title = "";
		NextTrainRecord[][] records = new NextTrainRecord[24][];
		Map<Character, String> notesTable = new HashMap<Character, String>();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "SJIS"));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.length() == 0 || line.startsWith(";") || line.startsWith("$")) {
					// 空行 or ';' で始まる行はコメントなので何もしない
					// 到着駅データ('$'で始まる行)は未サポートなので無視
				} else if (line.startsWith("[")) {
					// 曜日情報の読み込みはしない。平日ダイヤと休日ダイヤとして扱う
					addTable(result, title, records);
					
					// 作業用領域をクリア
					title = "";
					records = new NextTrainRecord[24][];
				} else if (line.startsWith("#")) {
					// "#"に続く部分がタイトル。#のあとに空白がある場合は取り除く
					title = line.substring(1).trim();
				} else {
					String[] values = line.split(":");
					if (Character.isLetter(values[0].charAt(0))) {
						// 備考をテーブルに
						if (values[1].contains(";")) { // 備考の途中に;があったら、その前までだけを切り出す
							notesTable.put(values[0].charAt(0), values[1].substring(0, values[1].indexOf(";")));
						} else {
							notesTable.put(values[0].charAt(0), values[1]);
						}
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

		addTable(result, title, records);
		return result;
	}
	
	private static void addTable(List<NextTrainTable> result, String title,
			NextTrainRecord[][] records) {
		if (isEmptyRecords(records) == false) {
			if (title.length() == 0) {
				title = "(タイトルなし)";
			}
			result.add(new NextTrainTable(title, records));
		}
	}

	
	private static boolean isEmptyRecords(NextTrainRecord[][] records) {
		// 順番に調べて全部nullなら時刻表全体が空とみなす
		for (NextTrainRecord[] array : records) {
			if (array != null) {
				return false;
			}
		}
		
		return true;
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
