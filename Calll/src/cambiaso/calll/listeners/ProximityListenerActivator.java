package cambiaso.calll.listeners;

import android.content.Context;
import android.hardware.SensorManager;
import cambiaso.calll.executors.CalllManager;
import cambiaso.calll.utils.Debug;

public class ProximityListenerActivator extends AProximityListener {
    // trigger proximity if distance is less than 5 cm
    private static final float PROXIMITY_THRESHOLD = 5.0f;
    
	public ProximityListenerActivator(Context ctx, SensorManager sm) {
		super(ctx, sm);
	}
	
	protected void proximityChanged(boolean active) {
		if(active) {
			Debug.println("Proximity sensor ON");
			CalllManager.wrap(context).forcedTurnOff();
		}
		else {
			Debug.println("Proximity sensor OFF");
			CalllManager.wrap(context).forcedTurnOn();
		}
	}

	@Override
	protected float getProximityThreashold() {
		return PROXIMITY_THRESHOLD;
	}
}
