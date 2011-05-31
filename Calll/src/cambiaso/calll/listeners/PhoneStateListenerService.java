package cambiaso.calll.listeners;

import cambiaso.calll.executors.CalllManager;
import cambiaso.calll.executors.CalllManager.CallStatus;
import cambiaso.calll.preferences.ActivationType;
import cambiaso.calll.preferences.ApplicationPreferences;
import cambiaso.calll.utils.Debug;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneStateListenerService extends PhoneStateListener {
	private Context context;
	
	public PhoneStateListenerService(Context ctx) {
		context = ctx;
	}
	
	public void onCallStateChanged(int state,String incomingNumber){
		switch(state) {
		case TelephonyManager.CALL_STATE_RINGING:
			Debug.println("Ringing...");
			CalllManager.wrap(context).saveConnectionsStates();
			CalllManager.wrap(context).callStatusAssign(CallStatus.RINGING);
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			Debug.println("Answered...");
			if(CalllManager.wrap(context).callStatusCheck(CallStatus.IDLE))
				CalllManager.wrap(context).saveConnectionsStates();
			CalllManager.wrap(context).callStatusAssign(CallStatus.ANSWERED);
			if(ApplicationPreferences.activationType(context).equals(ActivationType.ANSWER))
				CalllManager.wrap(context).tryToTurnOffData();
			break;
		case TelephonyManager.CALL_STATE_IDLE:
			Debug.println("Idle...");
			if(CalllManager.wrap(context).callStatusCheck(CallStatus.IDLE)) return;
			CalllManager.wrap(context).callStatusAssign(CallStatus.IDLE);
			CalllManager.wrap(context).tryToTurnOnData();
			CalllManager.wrap(context).showFinalNotification(); // only if proximity sensor is enabled
			break;
		default:
			// do nothing
		}
	}
	
	
	
}