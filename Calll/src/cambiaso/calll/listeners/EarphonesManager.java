package cambiaso.calll.listeners;

public class EarphonesManager {
	private static boolean areAttached = false;
	
	public static boolean areAttached() {
		return areAttached;
	}
	
	/*
	public static void checkStatus(Context context, Intent intent) {
		String data = intent.getDataString();
        Bundle extraData = intent.getExtras();
        
        String st = intent.getStringExtra("state");
        String nm = intent.getStringExtra("name");
        String mic = intent.getStringExtra("microphone");
        String all = String.format("st=%s, nm=%s, mic=%s", st, nm, mic);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Headset broadcast");
        builder.setMessage(all);
        builder.setPositiveButton("Okey-dokey", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
	}
	*/
}
