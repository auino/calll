package cambiaso.calll.services.bluetooth;

import cambiaso.calll.exceptions.NullPointerCustomException;
import cambiaso.calll.preferences.ApplicationPreferences;
import cambiaso.calll.services.IConnectionService;
import cambiaso.calll.utils.Debug;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;

// requires API v.5 or greater...
public class BluetoothService implements IConnectionService {
	private static final long WAIT_TIME = 5000;
	
	private BluetoothAdapter btMng = null;
	private boolean serviceWasEnabled = false;
	
	public BluetoothService(Context context) {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter != null)
            btMng = mBluetoothAdapter;
	}
	
	private BluetoothAdapter getBtMng() throws NullPointerCustomException {
		if(btMng.equals(null))
			throw new NullPointerCustomException();
		
		return btMng;
	}
	
	@Override
	public void saveState() {
		serviceWasEnabled = getBtMng().isEnabled();
	}
	
	@Override
	public boolean disable(Context ctx) {
		if(!ApplicationPreferences.isBluetoothEnabled(ctx)) return false;
		switch (getBtMng().getState()) {
		case BluetoothAdapter.STATE_OFF:
			Debug.println("Bluetooth state is off...");
			//serviceWasEnabled = false;
			return false; // delete this line is Debug.println(s) are deleted
		case BluetoothAdapter.STATE_TURNING_OFF:
			Debug.println("Bluetooth state is turning off...");
			//serviceWasEnabled = false;
			return false;
		case BluetoothAdapter.STATE_TURNING_ON:
			Debug.println("Bluetooth state is turning on...");
			try {
				Thread.sleep(WAIT_TIME);
			}
			catch (InterruptedException e) {
				Debug.printerr("BluetoothService: unable to wait before disable bluetooth", e);
				//return true;
			}
		}
		//serviceWasEnabled = true;
		/*
		//serviceWasEnabled = getBtMng().isEnabled();
		if(!serviceWasEnabled) {
			Debug.println("Bluetooth was not enabled...");
			return false;
		}
		*/
		Debug.println("Disabling Bluetooth...");
		try {
			getBtMng().disable();
		}
		catch(NullPointerCustomException e) {
			Debug.println("Bluetooth disabling: nullpointer exception thrown");
			return true;
		}
		Debug.println("Bluetooth successfully disabled...");
		return true;
	}
	
	@Override
	public boolean enable() {
		if(!serviceWasEnabled) return false;
		switch (getBtMng().getState()) {
		case BluetoothAdapter.STATE_ON:
		case BluetoothAdapter.STATE_TURNING_ON:
			return true;
		case BluetoothAdapter.STATE_TURNING_OFF:
			try {
				Thread.sleep(WAIT_TIME);
			}
			catch (InterruptedException e) {
				Debug.printerr("BluetoothService: unable to wait before enable bluetooth", e);
				//return false;
			}
		}
		try {
			getBtMng().enable();
		}
		catch(NullPointerCustomException e) { return false; }
		return true;
	}
	
}
