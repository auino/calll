package cambiaso.calll.utils;

import cambiaso.calll.Main;
import cambiaso.calll.R;
import cambiaso.calll.notifications.ToastNotificationService;
import cambiaso.calll.preferences.ApplicationPreferences;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Autostart extends BroadcastReceiver {
	
	// start of editable section
	
	private static final Class<Main> BOOT_CLASS = Main.class;
	
	// end of editable section
	
	private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		if(!ApplicationPreferences.isEnabled(ctx)) return;
		
		if(!ApplicationPreferences.autoboot(ctx)) return;
		
		if (intent.getAction().equals(ACTION)) {
			ctx.startService(new Intent(ctx, BOOT_CLASS));
			if(ApplicationPreferences.showBootNotification(ctx))
				ToastNotificationService.show(ctx, R.string.notification_autoboot);
        }
	}

}
