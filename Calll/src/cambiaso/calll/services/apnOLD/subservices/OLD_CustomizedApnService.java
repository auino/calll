package cambiaso.calll.services.apn;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;
import cambiaso.calll.Main;
import cambiaso.calll.exceptions.NullPointerCustomException;
import cambiaso.calll.preferences.ApplicationPreferences;
import cambiaso.calll.services.IConnectionService;

public class OLD_CustomizedApnService implements IConnectionService {
	private static String apn_name = "Calll_apn";
	private static String apn_addr = "calll.apn";

	private static final Uri APN_TABLE_URI = Uri.parse("content://telephony/carriers");
    private static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");  
	private static final int APN_ID_INDEX = 0;
	private static final int APN_NAME_INDEX = 1;
	
	private int oldApn, customApn;
	
	@Override
	public boolean disable(Context ctx) {
		if(!ApplicationPreferences.isApnEnabled(ctx)) return true;
		
		if(isApnRegistered(apn_name, apn_addr))
			customApn = getCustomApn(apn_name, apn_addr);
		else
			customApn = insertAPN(apn_name, apn_addr);
		
		oldApn = getCurrentApn();
		
		if(oldApn != customApn) return SetDefaultAPN(customApn);
		
		return true;
	}
	
	@Override
	public boolean enable() {
		if(oldApn != customApn)
			return SetDefaultAPN(oldApn);
		
		return true;
	}
	
	private static int insertAPN(String name, String apn_addr) 
    {
		int id = -1;
        ContentResolver resolver = Main.main.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("apn", apn_addr);
        
        /*
         * The following three field values are for testing in Android emulator only
         * The APN setting page UI will ONLY display APNs whose 'numeric' filed is
         * TelephonyProperties.PROPERTY_SIM_OPERATOR_NUMERIC.
         * On Android emulator, this value is 310260, where 310 is mcc, and 260 mnc.
         * With these field values, the newly added apn will appear in system UI.
         */
        values.put("mcc", "310");
        values.put("mnc", "260");
        values.put("numeric", "310260");
        
        Cursor c = null;
        try
        {
            Uri newRow = resolver.insert(APN_TABLE_URI, values);
            if(newRow != null)
            {
                c = resolver.query(newRow, null, null, null, null);
                Log.d("Calll", "Newly added APN:");
                
                // Obtain the apn id
                c.moveToFirst();
                id = c.getShort(APN_ID_INDEX);
                Log.d("Calll", "New ID: " + id + ": Inserting new APN succeeded!");
            }
        }
        catch(SQLException e)
        {
            Log.d("Calll", e.getMessage());
        }

        if(c !=null) c.close();
        
        return id;
    }
	
	private static boolean SetDefaultAPN(int id) {
        boolean res = false;
        ContentResolver resolver = Main.main.getContentResolver();
        ContentValues values = new ContentValues();
        
        //See /etc/apns-conf.xml. The TelephonyProvider uses this file to provide 
        //content://telephony/carriers/preferapn URI mapping
        values.put("apn_id", id); 
        try
        {
            resolver.update(PREFERRED_APN_URI, values, null, null);
            Cursor c = resolver.query(PREFERRED_APN_URI, new String[]{"name","apn"}, "_id="+id, null, null);
            
            if(c != null) {
                res = true;
                c.close();
            }
        }
        catch (SQLException e)
        {
            Log.d("Calll", e.getMessage());
        }
        
        return res;
    }
	
	private static boolean isApnRegistered(String name, String addr) {
		try {
			getCustomApn(name, addr);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	
	private static int getCustomApn(String name, String addr) throws NullPointerCustomException {
		Cursor c = Main.main.getContentResolver().query(APN_TABLE_URI, null, null, null, null);
		
		if(c == null) throw new NullPointerCustomException();
		
		if(c.moveToFirst()) {
			do {
				int id_tmp = c.getInt(APN_ID_INDEX);
				String name_tmp = c.getString(APN_NAME_INDEX);
				
				if(name_tmp.equals(name)) {
					c.close();
					return id_tmp;
				}
	    	} while(c.moveToNext());
			
			c.close();
	    }
		
		throw new NullPointerCustomException();
	}
	
	private static int getCurrentApn() {
		String id = null;
		Cursor cursor = null;
		ContentResolver resolver = Main.main.getContentResolver(); 
		try {
			cursor = resolver.query(PREFERRED_APN_URI, new String[]{"_id"}, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					id = cursor.getString(0);
					//Log.d("Calll", "Found APN with id: " + id);
				}
			}
		} catch (Exception ex){
			//Handle exceptions here
		} finally {
			if (cursor != null) cursor.close();
		} 
		
		if(id == null){ // it was != before
			throw new NullPointerCustomException();
			//ContentValues values = new ContentValues();
			//values.put(id, "newvalue"); //does this works?
			//resolver.update(contentUri, values, null, null);
		}
		return Integer.parseInt(id);
	}
	
}
