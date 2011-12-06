package cambiaso.calll.listeners;

import cambiaso.calll.preferences.ActivationType;
import cambiaso.calll.preferences.ApplicationPreferences;
import cambiaso.calll.utils.Debug;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public abstract class AProximityListener implements SensorEventListener {
	//private static final float MAX_VALUE = SensorManager.SENSOR_STATUS_ACCURACY_HIGH
	
    protected Context context;
    protected SensorManager sensorManager;
	
	protected AProximityListener(Context ctx, SensorManager sm) {
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
            		&& distance < getProximityThreashold()
            		&& distance < event.sensor.getMaximumRange());
			Debug.println("Proximity sensor value "+ (isActive?"":"NOT ") +"ACTIVE: ("+event.values[0]+"/"+ getMaxValue(event.sensor));
			
		    proximityChanged(isActive);
		}
	}
	
	protected abstract float getProximityThreashold();
	protected abstract void proximityChanged(boolean active);
	
	private float getMaxValue(Sensor sensor) {
		return sensor.getMaximumRange();
	}
	
}
