package cambiaso.calll.notifications;

import cambiaso.calll.R;
import cambiaso.calll.preferences.ApplicationPreferences;
import cambiaso.calll.preferences.NotificationType;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

public class NotificationService {
	private static int APP_ID = 1;
	private static String app_name = "Calll";
	
	public static void show(Context ctx, String text) {
		show(ctx, app_name, text);
	}
	
	public static void show(Context ctx, String statusText, String text) {
		//if(!ApplicationPreferences.isEnabled(ctx)) return;
		if(ApplicationPreferences.notificationType(ctx).equals(NotificationType.NONE)) return;
		
		// 1
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(ns);
		
		// 2
		int icon = R.drawable.icon;
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(icon, statusText, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		// 3
		Context context = ctx.getApplicationContext();
		//Intent notificationIntent = new Intent(context, Main.class);
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, null, 0);
		String title = app_name;
		
		notification.setLatestEventInfo(context, title, text, contentIntent);
		
		// 4
		mNotificationManager.cancel(APP_ID);
		mNotificationManager.notify(APP_ID, notification);
	}
	
	public static void hide(Context ctx) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(ns);
		
		mNotificationManager.cancel(APP_ID);
	}
}
