package edu.centenary.cogmality;

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

public class JournalFragment extends Fragment {
	
	private CogmalitySQLHelper steamdb;
	private Cursor c;

	private ListView journallist;
	private JournalCursorAdapter journallistadapt;
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.journal_layout, container, false);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        steamdb = new CogmalitySQLHelper(getActivity());
        
        journallist = (ListView)getView().findViewById(R.id.journallist);
		SQLiteDatabase db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.JOURNALTABLE, null, null, null,
				null, null, CogmalitySQLHelper.JOURNAL_TIME + " DESC");	
        journallistadapt = new JournalCursorAdapter(getActivity(), c);
        journallist.setAdapter(journallistadapt);
		db.close();	
		updateViews();
		
    }

    public void onResume() {
    	super.onResume();
    	updateViews();
    }
    
	public void updateViews() {
		SQLiteDatabase db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.JOURNALTABLE, null, null, null,
				null, null, CogmalitySQLHelper.JOURNAL_TIME + " DESC");
		journallistadapt.changeCursor(c);
		db.close();		
	}
}
