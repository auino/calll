package cambiaso.calll.preferences;

import cambiaso.calll.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.provider.Settings;

public class ApplicationPreferences extends PreferenceActivity {
	
	/* Other useful functions:
	 * - detect earphones
	 * - detect handsfree
	 * - disable gps
	 */
	
	private static final String isEnabledKey = "enableService",
		autobootKey = "autoboot",
		activationTypeKey = "activationType",
		notificationTypeKey = "notificationType",
		showBootNotificationKey = "showBootNotification",
		showEmptyNotificationKey = "showEmptyNotification",
		apnKey = "apn",
		viewApnKey = "viewApn",
		wifiKey = "wifi",
		btKey = "bt";
	
	private static final boolean DEFAULT_ISENABLED = true,
		DEFAULT_AUTOBOOT = true,
		DEFAULT_SHOWBOOTNOTIFICATION = false,
		DEFAULT_SHOWEMPTYNOTIFICATION = false,
		DEFAULT_APN = false,
		DEFAULT_WIFI = false,
		DEFAULT_BT = false;
	
	private static final String DEFAULT_ACTIVATIONTYPE = "1",
		DEFAULT_NOTIFICATIONTYPE = "3";
	
	private static boolean isEnabled = DEFAULT_ISENABLED,
		autoboot = DEFAULT_AUTOBOOT,
		showBootNotification = DEFAULT_SHOWBOOTNOTIFICATION,
		showEmptyNotification = DEFAULT_SHOWEMPTYNOTIFICATION,
		apn = DEFAULT_APN,
		wifi = DEFAULT_WIFI,
		bt = DEFAULT_BT;
	
	private static ActivationType activationType = wrapActivation(DEFAULT_ACTIVATIONTYPE);
	private static NotificationType notificationType = wrapNotification(DEFAULT_NOTIFICATIONTYPE);
	
	private static SharedPreferences prefs = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setupActivationTypePreference();
		setupNotificationTypePreference();
		//setupViewApnPreference(); // TODO to uncomment
	}
	
	private void setupActivationTypePreference() {
		final ListPreference pref = (ListPreference) findPreference(activationTypeKey);
		setupActivationTypePreferenceSummary(pref, pref.getValue());
		
        pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				setupActivationTypePreferenceSummary(pref, (String)newValue);
				return true;
			}
		});
	}
	
	protected void setupActivationTypePreferenceSummary(Preference pref, String newValue) {
		ActivationType nt = wrapActivation((String)newValue);
		if(nt == ActivationType.PROXIMITYSENSOR) pref.setSummary(getString(R.string.notificationtype_proximitysensor));
		if(nt == ActivationType.ANSWER) pref.setSummary(getString(R.string.notificationtype_answer));
	}
	
	private void setupNotificationTypePreference() {
		final ListPreference pref = (ListPreference) findPreference(notificationTypeKey);
		setupNotificationTypePreferenceSummary(pref, pref.getValue());
		
        pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				setupNotificationTypePreferenceSummary(pref, (String)newValue);
				return true;
			}
		});
	}
	
	protected void setupNotificationTypePreferenceSummary(Preference pref, String newValue) {
		NotificationType nt = wrapNotification((String)newValue);
		if(nt == NotificationType.NONE) pref.setSummary(getString(R.string.notificationtype_none));
		if(nt == NotificationType.SIMPLE) pref.setSummary(getString(R.string.notificationtype_simple));
		if(nt == NotificationType.TOAST) pref.setSummary(getString(R.string.notificationtype_toast));
	}
	
	private void setupViewApnPreference() {
		Preference viewApn = (Preference) findPreference(viewApnKey);
        viewApn.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
        		Intent apnEditActivity = new Intent(Settings.ACTION_APN_SETTINGS);
                startActivity(apnEditActivity);
        		return true;
        	}
        });
	}
	
	private static void loadPrefs(Context ctx) {
		// Get the xml/preferences.xml preferences
		if(prefs == null)
			prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
	}
	
	public static boolean isEnabled(Context ctx) {
		loadPrefs(ctx);
		isEnabled = prefs.getBoolean(isEnabledKey, DEFAULT_ISENABLED);
        return isEnabled;
	}
	
	public static boolean autoboot(Context ctx) {
		loadPrefs(ctx);
		autoboot = prefs.getBoolean(autobootKey, DEFAULT_AUTOBOOT);
        return (isEnabled && autoboot);
	}
	
	private static ActivationType wrapActivation(String value) {
		if (value.equals("1")) return ActivationType.PROXIMITYSENSOR;
		if (value.equals("2")) return ActivationType.ANSWER;
		
		return ActivationType.PROXIMITYSENSOR;
	}
	
	public static ActivationType activationType(Context ctx) {
		loadPrefs(ctx);
		activationType = wrapActivation(prefs.getString(activationTypeKey, DEFAULT_ACTIVATIONTYPE));
		return activationType;
	}
	
	private static NotificationType wrapNotification(String value) {
		if (value.equals("1")) return NotificationType.NONE;
		if (value.equals("2")) return NotificationType.SIMPLE;
		if (value.equals("3")) return NotificationType.TOAST;
		
		return NotificationType.NONE;
	}
	
	public static NotificationType notificationType(Context ctx) {
		loadPrefs(ctx);
		notificationType = wrapNotification(prefs.getString(notificationTypeKey, DEFAULT_NOTIFICATIONTYPE));
        return (!isEnabled(ctx) ? NotificationType.NONE : notificationType);
	}
	
	public static boolean showBootNotification(Context ctx) {
		loadPrefs(ctx);
		showBootNotification = prefs.getBoolean(showBootNotificationKey, DEFAULT_SHOWBOOTNOTIFICATION);
        return (isEnabled && autoboot && showBootNotification);
	}
	
	public static boolean showEmptyNotification(Context ctx) {
		loadPrefs(ctx);
		showEmptyNotification = prefs.getBoolean(showEmptyNotificationKey, DEFAULT_SHOWEMPTYNOTIFICATION);
        return (isEnabled && showEmptyNotification);
	}
	
	public static boolean isApnEnabled(Context ctx) {
		//return false; // TODO: To disable
		loadPrefs(ctx);
		apn = prefs.getBoolean(apnKey, DEFAULT_APN);
        return apn;
	}
	
	public static boolean isWifiEnabled(Context ctx) {
		loadPrefs(ctx);
		wifi = prefs.getBoolean(wifiKey, DEFAULT_WIFI);
        return (isEnabled && wifi);
	}
	
	public static boolean isBluetoothEnabled(Context ctx) {
		loadPrefs(ctx);
		bt = prefs.getBoolean(btKey, DEFAULT_BT);
		return (isEnabled && bt);
	}
	
}