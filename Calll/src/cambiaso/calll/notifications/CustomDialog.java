package cambiaso.calll.notifications;

import cambiaso.calll.R;
import android.app.Dialog;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomDialog extends Dialog {
	
	public CustomDialog(Context context, int title, String text, String licence) {
		super(context);
		
		setContentView(R.layout.customdialog);
		
		setTitle(title);
		
		ImageView image = (ImageView) findViewById(R.id.dialogimage);
		image.setImageResource(R.drawable.logo);
		TextView dialogtext = (TextView) findViewById(R.id.dialogtext);
		dialogtext.setText(text + "\n\n" + licence);
		
		setCancelable(true);
		
    	show();
	}
}
