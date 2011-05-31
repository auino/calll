package cambiaso.calll.listeners;

import cambiaso.calll.preferences.ApplicationPreferences;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallStatusReceiver extends BroadcastReceiver {
	private static PhoneStateListenerService phoneListener;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(!ApplicationPreferences.isEnabled(context)) return;
		
		if(phoneListener == null)
			phoneListener = new PhoneStateListenerService(context);
		
		manageTelephony(context, phoneListener);
    }
	
	private void manageTelephony(Context context, PhoneStateListenerService phoneListener) {
		TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
}
