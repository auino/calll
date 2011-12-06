package cambiaso.calll.services.proximityalert;

import android.content.Context;
import android.hardware.SensorManager;
import cambiaso.calll.listeners.AProximityListener;
import cambiaso.calll.utils.Debug;

public class ProximityListenerAlert extends AProximityListener {
    private static final float PROXIMITY_THRESHOLD = 2.0f;
    
	private IProximityCallback callback;
	
	public ProximityListenerAlert(Context ctx, SensorManager sm, IProximityCallback callback) {
		super(ctx, sm);
		this.callback = callback;
	}
	
	protected void proximityChanged(boolean active) {
		if(active) {
			Debug.println("Proximity sensor ON");
			callback.activate();
		}
		else {
			Debug.println("Proximity sensor OFF");
			callback.disactivate();
		}
	}
	
	@Override
	protected float getProximityThreashold() {
		return PROXIMITY_THRESHOLD;
	}
}
