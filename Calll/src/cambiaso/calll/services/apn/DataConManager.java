package cambiaso.calll.services.apn;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

// Needs the following permissions:
// - "android.permission.MODIFY_PHONE_STATE"

public final class DataConManager {
	private TelephonyManager m_telManager = null;
	private ConnectivityManager m_conManager = null;
	
	public DataConManager(Context context) {
		try {
			// Get phone and connectivity services
			m_telManager = (TelephonyManager)context.getSystemService("phone");
			m_conManager = (ConnectivityManager)context.getSystemService("connectivity");
		}
		catch (Exception e) {
			m_telManager = null;
			m_conManager = null;
		}
	}
	
	public boolean switchState(boolean enable) {
		boolean bRes = false;
		
		// Data Connection mode (only if correctly initialized)
		if (m_telManager != null) {
			try {
				// Will be used to invoke hidden methods with reflection
				@SuppressWarnings("rawtypes")
				Class cTelMan = null;
				Method getITelephony = null;
				Object oTelephony = null;
				@SuppressWarnings("rawtypes")
				Class cTelephony = null;
				Method action = null;
				
				// Get the current object implementing ITelephony interface
				cTelMan = m_telManager.getClass();
				getITelephony = cTelMan.getDeclaredMethod("getITelephony");
				getITelephony.setAccessible(true);
				oTelephony = getITelephony.invoke(m_telManager);
				
				// Call the enableDataConnectivity/disableDataConnectivity method
				// of Telephony object
				cTelephony = oTelephony.getClass();
				if (enable) {
					action = cTelephony.getMethod("enableDataConnectivity");
				}
				else {
					action = cTelephony.getMethod("disableDataConnectivity");
				}
				action.setAccessible(true);
				bRes = (Boolean)action.invoke(oTelephony);
			}
			catch (Exception e) {
				bRes = false;
			}
		}
		
		return bRes;
	} 
	
	public boolean isEnabled() {
		boolean bRes = false;
		
		// Data Connection mode (only if correctly initialized)
		if (m_conManager != null) {
			try {
				// Get Connectivity Service state
				NetworkInfo netInfo = m_conManager.getNetworkInfo(0);
				
				// Data is enabled if state is CONNECTED
				bRes = (netInfo.getState() == NetworkInfo.State.CONNECTED);
			}
			catch (Exception e) {
				bRes = false;
			}
		}
		
		return bRes;
	}
}
