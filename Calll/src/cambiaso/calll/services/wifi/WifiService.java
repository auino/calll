package cambiaso.calll.services.wifi;

import android.content.Context;
import android.net.wifi.WifiManager;
import cambiaso.calll.exceptions.NullPointerCustomException;
import cambiaso.calll.preferences.ApplicationPreferences;
import cambiaso.calll.services.IConnectionService;
import cambiaso.calll.utils.Debug;

public class WifiService implements IConnectionService {
	private static final long WAIT_TIME = 5000;
	
	private WifiManager wifiMng = null;
	private boolean serviceWasEnabled = false;
	
	public WifiService(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiMng = wifi;
	}
	
	private WifiManager getWifiMng() throws NullPointerCustomException {
		if(wifiMng.equals(null)) throw new NullPointerCustomException();
		
		return wifiMng;
	}
	
	@Override
	public void saveState() {
		serviceWasEnabled = getWifiMng().isWifiEnabled();
	}
	
	@Override
	public boolean disable(Context ctx) {
		if(!ApplicationPreferences.isWifiEnabled(ctx)) return false;
		
		switch (getWifiMng().getWifiState()) {
		case WifiManager.WIFI_STATE_DISABLED:
			Debug.println("Wifi state is disabled...");
			//serviceWasEnabled = false;
			return false; // delete this line if Debug.println(s) are deleted
		case WifiManager.WIFI_STATE_DISABLING:
			Debug.println("Wifi state is disabling...");
			//serviceWasEnabled = false;
			return false;
		case WifiManager.WIFI_STATE_ENABLING:
			Debug.println("Wifi state is enabling...");
			try {
				Thread.sleep(WAIT_TIME);
			}
			catch (InterruptedException e) {
				Debug.printerr("WifiService: unable to wait before disable wifi", e);
				return true;
			}
		}
		//serviceWasEnabled = true;
		/*
		//serviceWasEnabled = getWifiMng().isWifiEnabled();
		if(!serviceWasEnabled) {
			Debug.println("Wifi was not enabled...");
			return false;
		}
		*/
		Debug.println("Disabling wifi...");
		try {
			getWifiMng().setWifiEnabled(false);
		}
		catch(NullPointerCustomException e) {
			Debug.println("Wifi disabling: nullpointer...");
			return true;
		}
		Debug.println("Wifi successfully disabled...");
		return true;
	}

	@Override
	public boolean enable() {
		if(!serviceWasEnabled) return false;
		switch (getWifiMng().getWifiState()) {
			case WifiManager.WIFI_STATE_ENABLED:
			case WifiManager.WIFI_STATE_ENABLING:
				return true;
			case WifiManager.WIFI_STATE_DISABLING:
				try {
					Thread.sleep(WAIT_TIME);
				}
				catch (InterruptedException e) {
					Debug.printerr("WifiService: unable to wait before enable wifi", e);
					return false;
				}
		}
		try {
			getWifiMng().setWifiEnabled(true);
			return true;
		}
		catch(NullPointerCustomException e) { return false; }
	}
}
