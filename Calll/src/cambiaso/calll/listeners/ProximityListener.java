package cambiaso.calll.listeners;

import cambiaso.calll.executors.CalllManager;
import cambiaso.calll.preferences.ActivationType;
import cambiaso.calll.preferences.ApplicationPreferences;
import cambiaso.calll.utils.Debug;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ProximityListener implements SensorEventListener {
	//private static final float MAX_VALUE = SensorManager.SENSOR_STATUS_ACCURACY_HIGH
	
    // trigger proximity if distance is less than 5 cm
    private static final float PROXIMITY_THRESHOLD = 5.0f;
    
	private Context context;
	private SensorManager sensorManager;
	
	public ProximityListener(Context ctx, SensorManager sm) {
		context = ctx;
		sensorManager = sm;
	}
	
	public void registerListener() {
		if(sensorManager==null) {
			Debug.println("No sensor manager");
			return;
		}
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
				SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void unregisterListener() {
		if(sensorManager==null) {
			Debug.println("No sensor manager");
			return;
		}
		sensorManager.unregisterListener(this);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Debug.println("Proximity accuracy changed to "+ accuracy);
	}
	
	@Override
	// http://www.netmite.com/android/mydroid/2.0/frameworks/base/services/java/com/android/server/PowerManagerService.java
	public void onSensorChanged(SensorEvent event) {
		if(ApplicationPreferences.activationType(context).equals(ActivationType.PROXIMITYSENSOR)) {
			float distance = event.values[0];
            // compare against getMaximumRange to support sensors that only return 0 or 1
            boolean isActive = (distance >= 0.0
            		&& distance < PROXIMITY_THRESHOLD
            		&& distance < event.sensor.getMaximumRange());
			Debug.println("Proximity sensor value "+ (isActive?"":"NOT ") +"ACTIVE: ("+event.values[0]+"/"+ getMaxValue(event.sensor));
			
		    proximityChanged(isActive);
		}
	}
	
	private void proximityChanged(boolean active) {
		if(active) {
			Debug.println("Proximity sensor ON");
			CalllManager.wrap(context).forcedTurnOff();
		}
		else {
			Debug.println("Proximity sensor OFF");
			CalllManager.wrap(context).forcedTurnOn();
		}
	}
	
	private float getMaxValue(Sensor sensor) {
		return sensor.getMaximumRange();
	}
	
}
