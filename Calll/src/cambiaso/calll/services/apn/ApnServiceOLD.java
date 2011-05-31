package cambiaso.calll.services.apn;

import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import cambiaso.calll.services.IConnectionService;
import cambiaso.calll.services.apn.ApnManager;
import cambiaso.calll.utils.Debug;

import com.google.code.apndroid.*;

public class ApnServiceOLD implements IConnectionService {
	private ApnManager apnmanager = null;
	private Context context = null;
	private boolean serviceWasEnabled = false;
	
	public ApnServiceOLD(Context ctx) {
		context = ctx;
	}
	
	private ApnManager getApnManager(Context ctx) {
		if(apnmanager == null) apnmanager = new ApnManager(context);
		return apnmanager;
	}
	
	@Override
	public boolean disable(Context ctx) {
		boolean canDisable = isApnDroidInstalled(ctx);
		if(!canDisable) return false;
		
		Debug.println("Inside ApnService.disable() - apndroid is installed");
		
		return false;
		
		/* FOLLOWING CODE IS VALID
		Debug.println("Inside ApnService.disable() - start");
		if(!ApplicationPreferences.isEnabled(ctx)) return false;
		if(!ApplicationPreferences.isApnEnabled(ctx)) return false;
		
		serviceWasEnabled = getApnManager(ctx).isDataConnectionEnabled();
		Debug.println("Inside ApnService.disable() - service was enabled? "+ (serviceWasEnabled ? "yes" : "no"));
		if(!serviceWasEnabled) return false;
		boolean result = getApnManager(ctx).disableDataConnection();
		Debug.println("Inside ApnService.disable() - success? "+ result);
		return result;
		*/
	}
	
	private boolean isApnDroidInstalled(Context ctx) {
		PackageManager pm = ctx.getPackageManager();
		
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		
		List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);
		String tmpName;
		for (ResolveInfo rInfo : list) {
			tmpName = rInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
			//Debug.println(tmpName);
			if(tmpName.startsWith("ApnDroid")) return true;
		}
		return false;
	}

	@Override
	public boolean enable() {
		/* FOLLOWING CODE IS VALID
		if(!serviceWasEnabled) return false;
		if(!ApplicationPreferences.isEnabled(context)) return false;
		if(!ApplicationPreferences.isApnEnabled(context)) return false;
		if(getApnManager(context).isDataConnectionEnabled()) return true;
		return getApnManager(context).enableDataConnection();
		*/
		return false;
	}

	@Override
	public void saveState() {
		serviceWasEnabled = getApnManager(context).isDataConnectionEnabled();
	}
	
}
