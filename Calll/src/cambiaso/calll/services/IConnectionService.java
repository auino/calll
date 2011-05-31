package cambiaso.calll.services;

import android.content.Context;

public interface IConnectionService {
	public void saveState();
	public boolean disable(Context ctx);
	public boolean enable();
}
