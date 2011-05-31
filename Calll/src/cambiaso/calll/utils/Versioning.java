package cambiaso.calll.utils;

import android.content.Context;
import android.content.pm.PackageInfo;

public class Versioning {
	
	public static String getFullVersion(Context ctx, String app_version, String beta_string) {
		try {
			PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			app_version = pinfo.versionName;
		} catch (android.content.pm.PackageManager.NameNotFoundException e) {
			// do nothing
		}
		return app_version + ((Float.parseFloat(app_version)<1.0) ? " "+beta_string : "");
	}
}
