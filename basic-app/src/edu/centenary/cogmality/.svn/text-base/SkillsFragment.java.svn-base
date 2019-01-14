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

public class SkillsFragment extends Fragment {
	
	private CogmalitySQLHelper steamdb;
	private Cursor c;

	private ListView skilllist;
	private SkillCursorAdapter skilllistadapt;
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.skills_layout, container, false);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        steamdb = new CogmalitySQLHelper(getActivity());
        
        skilllist = (ListView)getView().findViewById(R.id.skilllist);
		SQLiteDatabase db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.SKILLTABLE, null, null, null,
				null, null, CogmalitySQLHelper.SKILL_POINTS + " DESC");	
        skilllistadapt = new SkillCursorAdapter(getActivity(), c);
        skilllist.setAdapter(skilllistadapt);
		db.close();	
		updateViews();
		
    }

	public void updateViews() {
		SQLiteDatabase db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.SKILLTABLE, null, null, null,
				null, null, CogmalitySQLHelper.SKILL_POINTS + " DESC");
		skilllistadapt.changeCursor(c);
		db.close();		
	}
}
