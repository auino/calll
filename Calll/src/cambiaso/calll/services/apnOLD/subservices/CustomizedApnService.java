package cambiaso.calll.services.apn.subservices;

import android.content.Context;
import cambiaso.calll.preferences.ApplicationPreferences;
import cambiaso.calll.services.IConnectionService;

public class CustomizedApnService implements IConnectionService {

	@Override
	public boolean disable(Context ctx) {
		if(!ApplicationPreferences.isApnEnabled(ctx)) return true;
		// TODO Auto-generated method stub
		
		return true;
	}

	@Override
	public boolean enable() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
