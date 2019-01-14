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

public class StatsFragment extends Fragment {
	
	private CogmalitySQLHelper steamdb;
	private TextView toolcon;
	private GridView toolgrid;
	private InvGridAdapter toolgridadapt;
	private Cursor c;

	private TextView pickupcon;
	private GridView pickupgrid;
	private InvGridAdapter pickupgridadapt;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stats_layout, container, false);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        steamdb = new CogmalitySQLHelper(getActivity());
        
        toolcon = (TextView) getView().findViewById(R.id.tooltext);        
        toolgrid = (GridView)  getView().findViewById(R.id.toolgrid);
		toolgridadapt = new InvGridAdapter(getActivity(), c, CogmalitySQLHelper.CRAFTTOOL_NAME);
		toolgrid.setAdapter(toolgridadapt);    
		
        pickupcon = (TextView) getView().findViewById(R.id.pickuptext);        
        pickupgrid = (GridView)  getView().findViewById(R.id.pickupgrid);
		pickupgridadapt = new InvGridAdapter(getActivity(), c, CogmalitySQLHelper.CRAFTTOOL_NAME);
		pickupgrid.setAdapter(pickupgridadapt);    

		updateViews();
		
    }

	public void updateViews() {
        toolcon.setText("Crafting");
		SQLiteDatabase db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.CRAFTTOOLTABLE, null, 
				CogmalitySQLHelper.CRAFTTOOL_TYPE + "=\"craft\"", null,
				null, null, null);
		toolgridadapt.changeCursor(c);
		db.close();		

        pickupcon.setText("Exploring");
		db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.CRAFTTOOLTABLE, null, 
				CogmalitySQLHelper.CRAFTTOOL_TYPE + "=\"pickup\"", null,
				null, null, null);
		pickupgridadapt.changeCursor(c);
		db.close();		
	}
}
