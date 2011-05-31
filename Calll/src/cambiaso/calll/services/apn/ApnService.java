package cambiaso.calll.services.apn;

import android.content.Context;
import cambiaso.calll.services.IConnectionService;
import cambiaso.calll.utils.Debug;

// http://forum.xda-developers.com/archive/index.php/t-899515.html
public class ApnService implements IConnectionService {
	private DataConManager manager;
	
	public ApnService(Context ctx) {
		manager = new DataConManager(ctx);
		Debug.println("ApnService created");
	}
	
	@Override
	public void saveState() {
		// manager.isEnabled();
	}

	@Override
	public boolean disable(Context ctx) {
		return manager.switchState(false);
	}

	@Override
	public boolean enable() {
		return manager.switchState(true);
	}

}
