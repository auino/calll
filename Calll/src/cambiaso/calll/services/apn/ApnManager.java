package cambiaso.calll.services.apn;

/* //device/content/providers/telephony/TelephonyProvider.java
**
** Copyright 2006, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;


public class ApnManager {
	private Context context;
	private static final Uri APN_TABLE_URI =   Uri.parse("content://telephony/carriers");
    private static final Uri PREFERRED_APN_URI =  Uri.parse("content://telephony/carriers/preferapn"); 
	private static final int DUMMY_NETWORK = 9999999;
	
	private boolean isDataConnectionEnabled ;
	private int originalNetworkId = -1;
	private int[]currentNetworks = null;

	public ApnManager(Context ctx) {
		Debug.pri
		this.context = ctx;
		this.currentNetworks = this.getAllNetworksFor(ApnManager.DUMMY_NETWORK) ;
		
		if(currentNetworks == null) {
			// there are no dummynetworks , service is enabled 
			this.isDataConnectionEnabled = true;
		}
		else this.isDataConnectionEnabled = false;
		
		this.currentNetworks = this.getCurrentNetworks();
	}
	
	protected void finalize() {
		try {
			if(!this.isDataConnectionEnabled) this.restoreApnTable();
			super.finalize();
		} catch(Throwable e) {
			Log.e("apnManager","",e);
		}
	}
	
	private boolean restoreApnTable() {
		if ( this.currentNetworks != null ) {
			if ( this.originalNetworkId <= 0  ) {
				Log.e("apnManager","internal error, found dummy networks , but dont have the original networkId ");
				return false;
			}
			this.changeNetworks(this.currentNetworks, this.originalNetworkId);
			return true;
		}
		return false;
	}
	
	public boolean enableDataConnection() {
		if(this.isDataConnectionEnabled) return false;
		this.isDataConnectionEnabled = this.restoreApnTable();
		return this.isDataConnectionEnabled;}
	
	public boolean disableDataConnection() {
		//restoreApnTable();
		if ( !this.isDataConnectionEnabled ) return false;
		if ( this.currentNetworks == null) return false;
		
		this.changeNetworks(this.currentNetworks, ApnManager.DUMMY_NETWORK);
		this.isDataConnectionEnabled = false;
		
		return true;
	}
	
	public boolean isDataConnectionEnabled() {
		return this.isDataConnectionEnabled;
	}
	
	private void changeNetworks(int[] networks,int value ) {
		ContentResolver content = context.getContentResolver();
		int size = networks.length;
		ContentValues values = new ContentValues();
		
		for(int i=0;i<size;i++) {
			values.clear();
			// allocation inside the loop , could be more efficient , improve if you bother
			Uri uri = ContentUris.withAppendedId(APN_TABLE_URI, networks[i]);
			values.put("numeric", value);
			content.update(uri, values, null, null);
		}
    }
	
	private int[] getCurrentNetworks() {
		Cursor c = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
		if (c == null ) return null;
		int size = c.getCount();
		if ( size == 0 ) return null;
		c.moveToFirst();
		this.originalNetworkId = c.getInt(2); 
		c.close();
		return getAllNetworksFor(originalNetworkId);
	}
	
	private int[] getAllNetworksFor(int carrier) {
		Cursor c = context.getContentResolver().query(APN_TABLE_URI, null, "numeric = " + carrier, null, null);
		if (c == null ) return null;
		int size = c.getCount();
		if ( size == 0 ) return null;
		int[] ret = new int[size];
		c.moveToFirst();
		for(int i=0;i<size;i++) {
			ret[i] =  c.getInt(0);
			c.moveToNext();
		}
		c.close();
	    
		return ret;
    }
}
