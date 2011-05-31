package cambiaso.calll.services.apn;

import android.content.Context;
import cambiaso.calll.exceptions.ApndroidNotSupportedException;
import cambiaso.calll.libs.Debug;
import cambiaso.calll.preferences.ApplicationPreferences;
import cambiaso.calll.services.IConnectionService;
import cambiaso.calll.services.apn.subservices.ApndroidService;
import cambiaso.calll.services.apn.subservices.CustomizedApnService;

public class ApnService implements IConnectionService {
	private IConnectionService apnService;
	public boolean enableCustomizedApnService = false;
	
	@Override
	public boolean disable(Context ctx) {
		if(!ApplicationPreferences.isApnEnabled(ctx)) return true;
		
		try {
			Debug.println("Running APNdroid...");
			apnService = new ApndroidService();
			apnService.disable(ctx);
			Debug.println("Apndroid successfully executed");
			return true;
		}
		catch(ApndroidNotSupportedException e) {
			Debug.println("Exception thrown on Apndroid: "+e.getStackTrace().toString());
			// Apndroid is not installed, so use customized Apn service
			if(enableCustomizedApnService) {
				apnService = new CustomizedApnService();
				apnService.disable(ctx);
			}
		}
		return false;
	}

	@Override
	public boolean enable() {
		apnService.enable();
		return false;
	}

}
