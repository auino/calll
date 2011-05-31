package cambiaso.calll.contacts;

import cambiaso.calll.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class ContactTheDeveloper {
	public static void showOptions(final Context ctx, int titleId, int listResourceId) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
    	builder.setTitle(ctx.getText(titleId));
    	builder.setItems(listResourceId, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        sendEmail(ctx, item);
    	    }
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
	}
	
	private static void sendEmail(final Context ctx, int item) {
		String address = ctx.getString(R.string.author_email);
		String subject = ctx.getString(R.string.contact_write_subject);
		String content = ctx.getString(R.string.contact_write_content);
		switch(item) {
		case 0: // ask a question
			subject = ctx.getString(R.string.contact_ask_subject);
			content = ctx.getString(R.string.contact_ask_content);
			break;
		case 1: // report a bug
			subject = ctx.getString(R.string.contact_bug_subject);
			content = ctx.getString(R.string.contact_bug_content);
			break;
		case 2: // help in translation
			subject = ctx.getString(R.string.contact_helpintranslation_subject);
			content = ctx.getString(R.string.contact_helpintranslation_content);
			break;
		case 3: // become a tester
			subject = ctx.getString(R.string.contact_tester_subject);
			content = ctx.getString(R.string.contact_tester_content);
			break;
		case 4: // other...
			subject = ctx.getString(R.string.contact_write_subject);
			content = ctx.getString(R.string.contact_write_content);
			break;
		}
		
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ address });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
		ctx.startActivity(Intent.createChooser(emailIntent, ctx.getString(R.string.sendemail)));
	}
	
}
