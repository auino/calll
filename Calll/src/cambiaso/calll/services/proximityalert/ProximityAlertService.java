package cambiaso.calll.services.proximityalert;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Vibrator;
import cambiaso.calll.listeners.AProximityListener;
import cambiaso.calll.services.IConnectionService;
import cambiaso.calll.utils.Debug;

public class ProximityAlertService implements IConnectionService, IProximityCallback {
	private AProximityListener proximityListener = null;
	private Context context = null;
	private Vibrator vibrator = null;
	
	public ProximityAlertService(Context ctx) {
		context = ctx;
	}
	
	@Override
	public void saveState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean disable(Context ctx) {
		// register proximity listener
		Debug.println("Registering proximity listener");
		if(proximityListener != null) proximityListener.unregisterListener();
		proximityListener = new ProximityListenerAlert(ctx, (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE), this);
		proximityListener.registerListener();
		
		return false;
	}

	@Override
	public boolean enable() {
		if(proximityListener != null) {
			proximityListener.unregisterListener();
			proximityListener = null;
			return true;
		}
		
		return false;
	}

	@Override
	public void activate() {
		if(context == null) return;
		
		// Fonte: http://goo.gl/FFkxV
		// Get instance of Vibrator from current Context
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		 
		// Start immediately
		// Vibrate for 200 milliseconds
		// Sleep for 500 milliseconds
		long[] pattern = { 0, 200, 500 };
		 
		// The "0" means to repeat the pattern starting at the beginning
		// CUIDADO: If you start at the wrong index (e.g., 1) then your pattern will be off --
		// You will vibrate for your pause times and pause for your vibrate times !
		vibrator.vibrate(pattern, 0);
	}

	@Override
	public void disactivate() {
		if(vibrator != null) {
			vibrator.cancel();
			vibrator = null;
		}
	}
	
}
