package edu.centenary.cogmality;

import android.app.NotificationManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

public class MailFragment extends Fragment {
	
	private CogmalitySQLHelper steamdb;
	private Cursor c;
	private TextView con;

	private ListView journallist;
	private MailCursorAdapter journallistadapt;
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mail_layout, container, false);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        steamdb = new CogmalitySQLHelper(getActivity());
        con = (TextView) getView().findViewById(R.id.mailcount);
       
        journallist = (ListView)getView().findViewById(R.id.journallist);
		SQLiteDatabase db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.MAILTABLE, null, null, null,
				null, null, CogmalitySQLHelper.MAIL_TIME + " DESC");	
        journallistadapt = new MailCursorAdapter(getActivity(), c, this);
        journallist.setAdapter(journallistadapt);
		db.close();	
		updateViews();
		
    }

    public void onResume() {
    	super.onResume();
    	updateViews();
    }
    
	public void updateViews() {
		int count = steamdb.mailCount();
		if (count == 0) {
			con.setText("No Mail Today.");
			con.setVisibility(View.VISIBLE);
		} else {
			con.setVisibility(View.GONE);
			NotificationManager nm = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
			nm.cancel(CogmalitySQLHelper.OFFER_NOTIFICATION);
			nm.cancel(CogmalitySQLHelper.ANSWER_NOTIFICATION);
		}
		SQLiteDatabase db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.MAILTABLE, null, null, null,
				null, null, CogmalitySQLHelper.MAIL_TIME + " DESC");
		journallistadapt.changeCursor(c);
		db.close();		
	}
}
