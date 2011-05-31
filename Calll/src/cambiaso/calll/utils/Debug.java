package cambiaso.calll.utils;

import android.util.Log;

/* Notes:
 * To run in debug mode launch adb as following:
 *   adb logcat *:S Calll:V
 */

public class Debug {
	public static final boolean IS_ACTIVE = false;
	
	private static final String APP_NAME = "Calll";
	
	public static void println(String text) {
		Log.d(APP_NAME, text);
	}

	public static void printerr(String text, Exception e) {
		Log.e(APP_NAME, text+": "+ e.getStackTrace().toString());
	}
	
}
