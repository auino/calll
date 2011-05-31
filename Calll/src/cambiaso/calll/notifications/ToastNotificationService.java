package cambiaso.calll.notifications;

import cambiaso.calll.utils.Debug;
import android.content.Context;
import android.widget.Toast;

public class ToastNotificationService {
	private final static long MESSAGES_DELAY = 3000;
	
	private static long lastToastMessage = 0;
	
	public static void show(Context ctx, int textResourceId) {
		//if(!ApplicationPreferences.isEnabled(ctx)) return;
		long currentTime = System.currentTimeMillis();
		if((lastToastMessage + MESSAGES_DELAY + Toast.LENGTH_LONG) <= currentTime) {
			lastToastMessage = currentTime;
			Debug.println("Showing message "+ ctx.getString(textResourceId));
			Toast.makeText(ctx, ctx.getString(textResourceId), Toast.LENGTH_LONG).show();
		}
	}

}
