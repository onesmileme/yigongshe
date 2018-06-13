package com.weikan.app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * 
 * @author Crazy24k@gmail.com
 * 
 */
public class StringUtil {
	/**
	 * 是否不为空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNotEmpty(String s) {
		return s != null && !"".equals(s.trim());
	}

	/**
	 * 是否为空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		return s == null || "".equals(s.trim());
	}

	/**
	 * 通过{n},格式化.
	 * 
	 * @param src
	 * @param objects
	 * @return
	 */
	public static String format(String src, Object... objects) {
		int k = 0;
		for (Object obj : objects) {
			src = src.replace("{" + k + "}", obj.toString());
			k++;
		}
		return src;
	}

	/**
	 * ArrayList转化为String
	 * 
	 * @param arrayList
	 * @param seperaterString
	 * @return
	 */
	public static String formatArraylistToString(ArrayList<String> arrayList, String seperaterString) {
		if (arrayList == null) {
			return "";
		}
		if (arrayList instanceof ArrayList<?>) {
			String string = arrayList.toString();
			string = string.replace("[", "");
			string = string.replace("]", "");
			string = string.replace(" ", "");
			string = string.replace(",", seperaterString);
			return string;
		} else {
			return "";
		}

	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public static ArrayList<String> formatArraylistToString(String string) {
		ArrayList<String> arrayList = new ArrayList<String>();
		if (TextUtils.isEmpty(string)) {
			return arrayList;
		}
		String[] strings = string.split(",");
		for (String string2 : strings) {
			arrayList.add(string2);
		}
		return arrayList;
	}

	/**
	 * hashmap 转化为String
	 * 
	 * @param hashMap
	 * @return
	 */
	public static String formatArraylistToString(HashMap<String, String> hashMap) {
		if (hashMap == null) {
			return "";
		}
		if (hashMap instanceof HashMap<?, ?>) {
			String string = hashMap.toString();
			string = string.replace("{", "");
			string = string.replace("}", "");
			string = string.replace(" ", "");
			return string;
		} else {
			return "";
		}

	}

	public static String formatHashSetToString(HashSet<String> hashSet) {
		if (hashSet == null) {
			return "";
		}
		if (hashSet instanceof HashSet<?>) {
			String string = hashSet.toString();
			string = string.replace("[", "");
			string = string.replace("]", "");
			string = string.replace(" ", "");
			return string;
		} else {
			return "";
		}

	}

	public static HashSet<String> formatStringToHashSet(String string) {
		if (string == null) {
			return new HashSet<String>();
		}

		String[] strings = string.split(",");
		HashSet<String> hashSet = new HashSet<String>();
		for (String string2 : strings) {
			hashSet.add(string2);
		}
		return hashSet;
	}

	public static HashMap<String, String> formatStringToHashMap(String string) {
		if (string == null) {
			return new HashMap<String, String>();
		}

		String[] strings = string.split(",");
		HashMap<String, String> hashMap = new HashMap<String, String>();
		for (String aString : strings) {
			String[] mapString = aString.split("=");
			if (mapString.length == 2) {
				hashMap.put(mapString[0], mapString[1]);
			}
		}
		return hashMap;
	}

	/**
	 * json to map
	 * 
	 * @param jsonString
	 * @return
	 */
	public static Map<String, Object> getMap(String jsonString) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonString);
			@SuppressWarnings("unchecked")
			Iterator<String> keyIter = jsonObject.keys();
			String key;
			Object value;
			Map<String, Object> valueMap = new HashMap<String, Object>();
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				value = jsonObject.get(key);
				valueMap.put(key, value);
			}
			return valueMap;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}
}
