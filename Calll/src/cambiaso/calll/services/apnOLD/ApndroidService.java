package cambiaso.calll.services.apn;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import cambiaso.calll.services.IConnectionService;
import com.google.code.apndroid.ApplicationConstants;

public class ApndroidService implements IConnectionService {
    public static final int STATE_REQUEST = 0;
    public static final int CHANGE_REQUEST = 1;
    
    private IActionService actionService = null;
    
	// The connection to the service. The service is only bound when the user checks the relevant checkbox.
    private ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                    actionService = IActionService.Stub.asInterface(service);
                    //Toast.makeText(MyActivity.this, "Connected to service", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                    actionService = null;
                    //Toast.makeText(MyActivity.this, "Connection to service lost", Toast.LENGTH_LONG).show();
            }
    };
    
	
	@Override
	public boolean disable(Context ctx) {
		//ActionService a;
		return false;
	}

	@Override
	public boolean enable() {
		
		return false;
	}

	@Override
	public void saveState() {
		// TODO Auto-generated method stub
		
	}

}
