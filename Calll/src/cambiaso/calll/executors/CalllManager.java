package cambiaso.calll.executors;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import android.content.Context;
import android.hardware.SensorManager;
import cambiaso.calll.R;
import cambiaso.calll.listeners.ProximityListener;
import cambiaso.calll.notifications.NotificationService;
import cambiaso.calll.notifications.ToastNotificationService;
import cambiaso.calll.preferences.ActivationType;
import cambiaso.calll.preferences.ApplicationPreferences;
import cambiaso.calll.preferences.NotificationType;
import cambiaso.calll.services.IConnectionService;
import cambiaso.calll.services.apn.ApnService;
import cambiaso.calll.services.bluetooth.BluetoothService;
import cambiaso.calll.services.wifi.WifiService;
import cambiaso.calll.utils.Debug;

public class CalllManager {
	private static CalllManager obj = null;
	
	private Context context;
	
	private ProximityListener proximityListener = null;
	
	private List<IConnectionService> connectionServices = null;
	
	public enum CallStatus { IDLE, RINGING, ANSWERED };
	private static Lock callStatusMutex = new ReentrantLock();
	private static CallStatus callStatus = CallStatus.IDLE;
	
	private enum ServiceStatus { STATUS_OFF, STATUS_ON };
	private static Lock serviceStatusMutex = new ReentrantLock();
	private static ServiceStatus serviceStatus = ServiceStatus.STATUS_ON;

	private static boolean atLeastOneConnection = false;
	
	private CalllManager(Context ctx) {
		Debug.println("CalllManager constructor");
		context = ctx;
		connectionServices = new LinkedList<IConnectionService>();
		connectionServices.add(new WifiService(ctx));
		connectionServices.add(new BluetoothService(ctx));
	    //connectionServices.add(new ApnService(ctx));
		Debug.println("CalllManager constructor: connectionService.size="+connectionServices.size());
	}
	
	public static CalllManager wrap(Context ctx) {
		if(obj == null) obj = new CalllManager(ctx);
		return obj;
	}
	
	public boolean callStatusCheck(CallStatus value) {
		boolean result;
		callStatusMutex.lock();
		result = (callStatus == value);
		callStatusMutex.unlock();
		return result;
	}
	
	public void saveConnectionsStates() {
		for(IConnectionService cs : connectionServices)
			cs.saveState();
	}
	
	public void callStatusAssign(CallStatus value) {
		if(ApplicationPreferences.activationType(context).equals(ActivationType.PROXIMITYSENSOR)) {
			if(value.equals(CallStatus.IDLE)) {
				// unregister proximity listener
				Debug.println("Unregistering proximity listener");
				if(proximityListener != null) {
					proximityListener.unregisterListener();
					proximityListener = null;
				}
			}
			else { // Status = RINGING or ANSWERED
				// register proximity listener
				Debug.println("Registering proximity listener");
				if(proximityListener != null) proximityListener.unregisterListener();
				proximityListener = new ProximityListener(context, (SensorManager) context.getSystemService(Context.SENSOR_SERVICE));
				proximityListener.registerListener();
			}
		}
		callStatusMutex.lock();
		callStatus = value;
		callStatusMutex.unlock();
	}
	
	private boolean serviceStatusCheck(ServiceStatus value) {
		boolean result;
		serviceStatusMutex.lock();
		result = (serviceStatus == value);
		serviceStatusMutex.unlock();
		return result;
	}
	
	private void serviceStatusAssign(ServiceStatus value) {
		serviceStatusMutex.lock();
		serviceStatus = value;
		serviceStatusMutex.unlock();
	}
	
	/**
	 * Used to turn off data, if activation type is ANSWER and it's not PROXIMITYSENSOR
	 */
	public void tryToTurnOffData() {
		Debug.println("Inside tryToTurnOffData() method");
		
		// never turn it off if status is idle (eventually, turn it on)
		if(callStatusCheck(CallStatus.IDLE)) return;
		
		if(callStatusCheck(CallStatus.RINGING)) return;
		
		if(ApplicationPreferences.activationType(context).equals(ActivationType.ANSWER))
			forcedTurnOff();
	}
	
	// called by proximity listener
	public void forcedTurnOff() {
		Debug.println("Inside forcedTurnOff() method");
		if(!ApplicationPreferences.isEnabled(context)) return;
		if(serviceStatusCheck(ServiceStatus.STATUS_OFF)) return;
		serviceStatusAssign(ServiceStatus.STATUS_OFF);
		Debug.println("Status is OFF");
		
		Debug.println("Disabling #"+ connectionServices.size() +" connections");
		
		atLeastOneConnection = false;
		for(IConnectionService cs : connectionServices) {
			try {
				atLeastOneConnection |= cs.disable(context);
			}
			catch (Exception e) {
				Debug.printerr("Exception thrown trying to disable connection", e);
			}
		}
		
		manageTurningOffNotifications();
	}
	
	private void manageTurningOffNotifications() {
		// manage notification
		Debug.println("Notifications on turning OFF...");
		
		NotificationType nt = ApplicationPreferences.notificationType(context);
		if(nt.equals(NotificationType.NONE)) {
			Debug.println("Notification is NONE, so don't show nothing...");
			return;
		}
		
		// no notification if proximity sensor has been used (user cannot see it)
		if(ApplicationPreferences.activationType(context).equals(ActivationType.PROXIMITYSENSOR)) {
			Debug.println("Proximity listener is configured, so don't show nothing...");
			return;
		}
		
		if(!atLeastOneConnection) {
			Debug.println("No connections to turn off...");
			// if showemptynotification and notification is not configured for none (temporary or permanent)
			if(ApplicationPreferences.showEmptyNotification(context)) {
				Debug.println("Showing empty notification...");
				if(nt.equals(NotificationType.SIMPLE))
					NotificationService.show(context, context.getString(R.string.notification_none_preview), context.getString(R.string.notification_none_summary));
				else // notification is TOAST
					ToastNotificationService.show(context, R.string.notification_none_summary);
			}
			else
				Debug.println("No empty notification, so don't show nothing...");
			return;
		}
		Debug.println("Some connections to disable, so show notification...");
		if(nt.equals(NotificationType.SIMPLE))
			NotificationService.show(context, context.getString(R.string.notification_active_preview), context.getString(R.string.notification_active_summary));
		else // notification is TOAST
			ToastNotificationService.show(context, R.string.notification_active_summary);
	}
	
	/**
	 * Used to turn on data, if activation type is ANSWER and it's not PROXIMITYSENSOR
	 */
	public void tryToTurnOnData() {
		Debug.println("Inside tryToTurnOnData() method");
		if(!ApplicationPreferences.isEnabled(context)) return;
		
		// never turn it on if status is not idle (eventually, turn it off)
		if(!callStatusCheck(CallStatus.IDLE)) return;
		
		//TO TEST:
		//if(ApplicationPreferences.activationType(context).equals(ActivationType.ANSWER))
			forcedTurnOn();
	}
	
	public void forcedTurnOn() {
		Debug.println("Inside forcedTurnOn() method");
		if(serviceStatusCheck(ServiceStatus.STATUS_ON)) return;
		serviceStatusAssign(ServiceStatus.STATUS_ON);
		Debug.println("Status is ON");
		
		if(!atLeastOneConnection) {
			managePreTurningOnNotifications();
			return;
		}
		
		Debug.println("Enabling #"+ connectionServices.size() +" connections");
		for(IConnectionService cs : connectionServices) {
			try {
				cs.enable();
			}
			catch (Exception e) {
				Debug.printerr("Exception thrown trying to enable connection", e);
			}
		}
		
		manageTurningOnNotifications();
	}
	
	private void managePreTurningOnNotifications() {
		Debug.println("Notification on PRE turning ON");
		
		if(ApplicationPreferences.activationType(context).equals(ActivationType.PROXIMITYSENSOR)) {
			Debug.println("Activation type is on proximity sensor, so only final notification will be shown...");
			return;
		}
		
		if(ApplicationPreferences.showEmptyNotification(context)) {
			Debug.println("Showing an empty notification...");
			if(ApplicationPreferences.notificationType(context).equals(NotificationType.SIMPLE)) {
				NotificationService.show(context, context.getString(R.string.notification_none_preview), context.getString(R.string.notification_none_summary));
				NotificationService.hide(context);
			}
			else // notification is TOAST
				ToastNotificationService.show(context, R.string.notification_none_summary);
		}
	}
	
	private void manageTurningOnNotifications() {
		// manage notifications
		Debug.println("Notifications on (POST) turning ON...");
		
		NotificationType nt = ApplicationPreferences.notificationType(context);
		if(nt.equals(NotificationType.NONE)) {
			Debug.println("Notification is NONE, so don't show nothing...");
			return;
		}
		
		// only final notification if type is proximity sensor
		if(ApplicationPreferences.activationType(context).equals(ActivationType.PROXIMITYSENSOR)) {
			Debug.println("Activation type is on proximity sensor, so only final notification will be shown...");
			return;
		}
		
		Debug.println("Showing a notification...");
		
		if(nt.equals(NotificationType.SIMPLE)) {
			NotificationService.show(context, context.getString(R.string.notification_finished_preview), context.getString(R.string.notification_finished_summary));
			NotificationService.hide(context);
		}
		else // notification is TOAST
			ToastNotificationService.show(context, R.string.notification_finished_summary);
	}
	
	public void showFinalNotification() {
		Debug.println("Notifications on end call...");
		
		NotificationType nt = ApplicationPreferences.notificationType(context);
		
		if(nt.equals(NotificationType.NONE)) return;
		
		if(nt.equals(NotificationType.SIMPLE)) {
			NotificationService.show(context, context.getString(R.string.notification_finished_preview), context.getString(R.string.notification_finished_summary));
			NotificationService.hide(context);
		}
		else // notification is TOAST
			ToastNotificationService.show(context, R.string.notification_finished_summary);
	}
	
}
