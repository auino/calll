package cambiaso.calll;

import cambiaso.calll.contacts.ContactTheDeveloper;
import cambiaso.calll.notifications.CustomDialog;
import cambiaso.calll.preferences.ApplicationPreferences;
import cambiaso.calll.utils.Debug;
import cambiaso.calll.utils.Versioning;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Main extends Activity {
	private static Main main;
	
	private static final int PREFERENCES_GROUP_ID = 0;
	private static final int SETTINGS_ID = 1;
	
	private static final int CONTACT_GROUP_ID = 1;
	private static final int CONTACT_ID = 2;
	
	private static final int REFS_GROUP_ID = 2;
	private static final int REFS_ID = 3;
	
	private static final int TEST_GROUP_ID = 3;
	private static final int TEST_ID = 4;
	
	private static final int ABOUT_GROUP_ID = 4;
	private static final int ABOUT_ID = 5;
	
	public static Main get() {
		return main;
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        main = this;
        setVersion();
        
        //requestApndroidStatus();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(PREFERENCES_GROUP_ID, SETTINGS_ID, 0, R.string.settings_text).setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(CONTACT_GROUP_ID, CONTACT_ID, 0, R.string.contact_text).setIcon(android.R.drawable.ic_menu_send);
        menu.add(REFS_GROUP_ID, REFS_ID, 0, R.string.refs_text).setIcon(android.R.drawable.ic_menu_info_details);
        if(Debug.IS_ACTIVE) menu.add(TEST_GROUP_ID, TEST_ID, 0, "Test button").setIcon(android.R.drawable.ic_menu_info_details);
        //menu.add(DONATE_GROUP_ID, DONATE_ID, 0, R.string.donate_text).setIcon(android.R.drawable.ic_);
        menu.add(ABOUT_GROUP_ID, ABOUT_ID, 0, R.string.about_text).setIcon(android.R.drawable.ic_menu_help);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SETTINGS_ID:
            	Intent settingsActivity = new Intent(getBaseContext(), ApplicationPreferences.class);
                startActivity(settingsActivity);
                break;
            case CONTACT_ID:
                ContactTheDeveloper.showOptions(this, R.string.contact_popup_title, R.array.contacttypes);
            	break;
            case REFS_ID:
            	showRefs();
            	break;
            case ABOUT_ID:
            	new CustomDialog(Main.this,
            			R.string.about_title,
            			getString(R.string.version_title) +" "+ Versioning.getFullVersion(this, getString(R.string.app_version), getString(R.string.betastring)) +"\n"+ getString(R.string.author_title) +" "+ getString(R.string.author),
            			getString(R.string.licence_text));
            	break;
            case TEST_ID:
            	if(Debug.IS_ACTIVE) {
	            	try {
	            		//requestApndroidSwitch();
	        		}
	        		catch (Exception e) {
	        			Log.d("Calll", "Exception thrown on test button");
	        			e.printStackTrace();
	        		}
            	}
            	break;
        }
        return true;
    }
    
	private void showRefs() {
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	            //Yes button clicked
    	        	String url = getString(R.string.refs_url);
    	        	Intent i = new Intent(Intent.ACTION_VIEW);
    	        	i.setData(Uri.parse(url));
    	        	startActivity(i);
    	        	break;

    	        case DialogInterface.BUTTON_NEGATIVE:
    	            //No button clicked
    	            break;
    	        }
    	    }
    	};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	String question = getString(R.string.showrefs_question);
    	String yes = getString(R.string.showrefs_answeryes);
    	String no = getString(R.string.showrefs_answerno);
    	builder.setMessage(question)
    		.setPositiveButton(yes, dialogClickListener)
    		.setNegativeButton(no, dialogClickListener)
    		.show();
    }
    
    private void setVersion() {
    	TextView version = (TextView) findViewById(R.id.txtVersionTitle);
    	String versionText = getString(R.string.version_title) +" "+ Versioning.getFullVersion(this, getString(R.string.app_version), getString(R.string.betastring));
    	version.setText(versionText);
    }
    
    /*
    protected void onActivityResult(int requestedCode, int resultCode, Intent intent) {
        super.onActivityResult(requestedCode, resultCode, intent);
    	Debug.println("Result of an Apndroid activity");
        switch (requestedCode) {
            case ApndroidService.APN_STATE_REQUEST:
                if (resultCode == RESULT_OK && intent != null) {
                    if (ApplicationConstants.APN_DROID_RESULT.equals(intent.getAction())) {
                        int onState = ApplicationConstants.State.ON;
                        boolean state = intent.getIntExtra(ApplicationConstants.RESPONSE_APN_STATE, onState) == onState;
                        ApndroidService.setCurrentApnState(state);
                        String currentState = "Current state is " + (state ? "on" : "off");
                        if (!state) {
                            currentState += "\nMms state is " +
                                    (intent.getIntExtra(ApplicationConstants.RESPONSE_MMS_STATE, onState) == onState
                                            ? "on"
                                            : "off");
                        }
                        Debug.println(currentState);
                        //Toast toast = Toast.makeText(this, currentState, Toast.LENGTH_LONG);
                        //toast.show();
                    }
                }
                break;
            case ApndroidService.APN_CHANGE_REQUEST:
                if (resultCode == RESULT_OK && intent != null) {
                    if (ApplicationConstants.APN_DROID_RESULT.equals(intent.getAction())) {
                        String switchSuccess = "Switch was " + (intent.getBooleanExtra(ApplicationConstants.RESPONSE_SWITCH_SUCCESS, true) ? "successful" : "unsuccessful");
                        Debug.println(switchSuccess);
                        //Toast toast = Toast.makeText(this, switchSuccess, Toast.LENGTH_LONG);
                        //toast.show();
                    }
                }
                break;
        }
    }
    public static void requestApndroidStatus() {
    	try {
    		Intent intent = new Intent(ApplicationConstants.STATUS_REQUEST);
        	main.startActivityForResult(intent, ApndroidService.APN_STATE_REQUEST);
    	}
    	catch (Exception e) {
    		// Apndroid is not supported
    		Debug.println("Apndroid is not supported");
    	}
    }
    
    public static void requestApndroidSwitch() {
    	try {
	    	Debug.println("Requesting Apndroid Switch");
	    	boolean targetState = false;
	        boolean keepMms = false;
	        boolean showNotification = false;
	        Intent intent = new Intent(ApplicationConstants.CHANGE_STATUS_REQUEST);
	        int onState = ApplicationConstants.State.ON;
	        int offState = ApplicationConstants.State.OFF;
	        intent.putExtra(ApplicationConstants.TARGET_MMS_STATE, keepMms ? onState : offState);
	        intent.putExtra(ApplicationConstants.TARGET_APN_STATE, targetState ? onState : offState);
	        intent.putExtra(ApplicationConstants.SHOW_NOTIFICATION, showNotification);
	        //main.startActivityForResult(intent, ApndroidService.APN_CHANGE_REQUEST);
	        main.startActivity(intent);
	    	Debug.println("Apndroid switch requested");
    	}
    	catch (Exception e) {
    		// Apndroid is not supported
    		Debug.println("Apndroid is not supported");
    	}
    }
    */
    
}
