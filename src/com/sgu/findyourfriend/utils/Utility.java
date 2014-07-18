package com.sgu.findyourfriend.utils;

/**
 * Utility is a just an ordinary class to have some Utility methods
 * 
 * @author Adil Soomro
 *
 */

public class Utility {
	public static final String [] sender= new String [] {"Lalit", "RobinHood", "Captain", "HotVerySpicy", "Dharmendra", "PareshMayani", "Abhi", "SpK", "CapDroid"};
	public static final String [] messages= new String [] {
		"Aah! thats cool",
		"Tu really CoLor 6e", 
		"Get Lost!!",
		"@AdilSoomro @AdilSoomro",
		"Lets see what the Rock is cooking..!!",
		"Yeah! thats great.",
		"Awesome Awesome!",
		"@RobinHood.",
		"Lalit ka Dillllll...!!!",
		"I'm fine, thanks, what about you?"};
	
	
	// example: 30' 10s
	public static String convertMicTimeToString(Long mics) {
		return convertSecTimeToString(mics / 1000);
	}
	
	public static String convertSecTimeToString(Long sumSecs) {
		// int sumSecs = (int) (mics / 1000);
		int days = (int) (sumSecs / (24 * 60 * 60)); sumSecs %= (24 * 60 * 60); 
		int hours = (int) (sumSecs / (60 * 60)); sumSecs %= (60 * 60);
		int mins = (int) (sumSecs / 60); sumSecs %= 60;
		Long secs = sumSecs;
		
		StringBuilder builder = new StringBuilder();
		if (days > 0) builder.append(" " + days + "d");
		if (hours > 0) builder.append(" " + hours + "h");
		if (mins > 0) builder.append(" " + mins + "'");
		if (secs > 0) builder.append(" " + secs + "''");
		return builder.toString();
	}
	
}
