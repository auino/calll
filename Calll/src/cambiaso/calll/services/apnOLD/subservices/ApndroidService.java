package cambiaso.calll.services.apn.subservices;

import com.google.code.apndroid.ApplicationConstants;

import android.content.Context;
import android.content.Intent;
import cambiaso.calll.Main;
import cambiaso.calll.exceptions.ApndroidNotSupportedException;
import cambiaso.calll.libs.Debug;
import cambiaso.calll.preferences.ApplicationPreferences;
import cambiaso.calll.services.IConnectionService;

public class ApndroidService implements IConnectionService {
	public static final int APN_STATE_REQUEST = 0;
    public static final int APN_CHANGE_REQUEST = 1;
    
    private static final boolean APN_STATE_ON = true;
    private static final boolean APN_STATE_OFF = false;
    
    private enum ApnRequest { APN_REQUEST_DISABLE, APN_REQUEST_ENABLE, APN_REQUEST_NONE };
    
    private static boolean apnServiceState = APN_STATE_OFF;
	private static boolean currentApnState;
    
    private static ApnRequest currentRequest = ApnRequest.APN_REQUEST_DISABLE;
    
    private static Main getMain() {
    	return Main.get();
    }
    
    public static void setCurrentApnState(boolean value) {
    	currentApnState = value;
    }
    
	@Override
	public boolean disable(Context ctx) throws ApndroidNotSupportedException {
		if(!ApplicationPreferences.isApnEnabled(ctx)) return true;
		
		Debug.println("Inside Apndroid - disable()");
		if(currentApnState == APN_STATE_OFF) {
			Debug.println("Apndroid is already disabled");
			return true;
		}
		Main.requestApndroidSwitch();
		return true; //requestSwitch(ApnRequest.APN_REQUEST_DISABLE);
	}
	
	@Override
	public boolean enable() {
		Debug.println("Inside Apndroid - enable()");
		if(currentApnState == APN_STATE_ON) {
			Debug.println("Apndroid is already enabled");
			return true;
		}
		Main.requestApndroidSwitch();
		return true; //requestSwitch(ApnRequest.APN_REQUEST_ENABLE);
	}
	
	public static void manageResult(Context ctx, int requestedCode, int resultCode, Intent intent) {
		if(!ApplicationPreferences.isApnEnabled(ctx)) return;
		
		Debug.println("Inside Apndroid - manageResult() - "+ requestedCode);
		switch (requestedCode) {
        case APN_STATE_REQUEST:
            if (resultCode == Main.RESULT_OK && intent != null) {
                if (ApplicationConstants.APN_DROID_RESULT.equals(intent.getAction())) {
                    int onState = ApplicationConstants.State.ON;
                    apnServiceState = intent.getIntExtra(ApplicationConstants.RESPONSE_APN_STATE, onState) == onState;
                    
                    if((apnServiceState == APN_STATE_ON) && (currentRequest == ApnRequest.APN_REQUEST_DISABLE))
                    	switchApn(ctx);
                }
            }
            break;
        case APN_CHANGE_REQUEST:
            if (resultCode == Main.RESULT_OK && intent != null) {
                if (ApplicationConstants.APN_DROID_RESULT.equals(intent.getAction())) {
                    boolean success = intent.getBooleanExtra(ApplicationConstants.RESPONSE_SWITCH_SUCCESS, true);
                    Debug.println("Result on switch: "+ (success ? "success" : "fail"));
                }
            }
            break;
		}
	}

	private static void switchApn(Context ctx) {
		boolean targetState = false; //getCheckBoxState(R.id.target_state);
		boolean keepMms = false; //getCheckBoxState(R.id.keep_mms);
		boolean showNotification = false; //getCheckBoxState(R.id.show_notification);
		if(currentRequest == ApnRequest.APN_REQUEST_DISABLE) {
			targetState = false;
			keepMms = false;
			showNotification = false;
		}
		if(currentRequest == ApnRequest.APN_REQUEST_ENABLE) {
			targetState = true;
			keepMms = true;
			showNotification = true;
		}
		
		Intent intent = new Intent(ApplicationConstants.CHANGE_STATUS_REQUEST);
		int onState = ApplicationConstants.State.ON;
		int offState = ApplicationConstants.State.OFF;
		intent.putExtra(ApplicationConstants.TARGET_MMS_STATE, keepMms ? onState : offState);
		intent.putExtra(ApplicationConstants.TARGET_APN_STATE, targetState ? onState : offState);
		intent.putExtra(ApplicationConstants.SHOW_NOTIFICATION, showNotification);
		getMain().startActivityForResult(intent, APN_CHANGE_REQUEST);
	}
}
