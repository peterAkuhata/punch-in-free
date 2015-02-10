package com.aku.apps.punchin.free.utils;

import java.util.ArrayList;

public class ArrayListUtil {
	
	/**
	 * Splits the string using a comma, creates and returns an array list of strings.
	 * @param item
	 * @return
	 */
	public static ArrayList<String> split(String item) {
		ArrayList<String> list = new ArrayList<String>();
		
		for (String i : item.split(","))
			list.add(i);
		
		return list;
	}

	/**
	 * Joins the arraylist into a single string, combining them with commas.
	 * @param list
	 * @return
	 */
	public static String join(ArrayList<String> list) {
		String item = "";
		
		for (int i = 0; i < list.size(); i++) {
			if (i > 0)
				item += ",";
			
			item += list.get(i);
		}
		
		return item;
	}
}
