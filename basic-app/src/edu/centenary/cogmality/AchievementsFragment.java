package edu.centenary.cogmality;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class AchievementsFragment extends Fragment {
	
	private CogmalitySQLHelper steamdb;
	private TextView con;
	private ListView invlist;
	private ActivityCursorAdapter inv;
	private Cursor c;
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bazaar_buy_layout, container, false);

    }
    
    @Override
    public void onStart() {
        super.onStart();
        steamdb = new CogmalitySQLHelper(getActivity());
        invlist = (ListView)getView().findViewById(R.id.listView1);

        // Get all of the notes from the database and create the item list
        SQLiteDatabase db = steamdb.getReadableDatabase();
        db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.ACHTABLE, null, 
				null, null,
				null, null, null);

        inv = new ActivityCursorAdapter(getActivity(), c);
        invlist.setAdapter(inv);
		db.close();        
        
	}
	
	public void onResume(){
		super.onResume();
		updateViews();
	}

	public void updateViews() {
		SQLiteDatabase db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.ACHTABLE, null, 
				null, null,
				null, null, null);
		inv.changeCursor(c);
		db.close();		

	}
	
	protected class ActivityCursorAdapter extends CursorAdapter {

		private LayoutInflater mInflater;
		private int mName;
		private int mType;
		private int mUnlocked;

		public ActivityCursorAdapter(Context context, Cursor c) {
			super(context, c);
	
			mName = c.getColumnIndex(CogmalitySQLHelper.ACH_TEXT);
			mType = c.getColumnIndex(CogmalitySQLHelper.ACH_LABEL);
			mUnlocked = c.getColumnIndex(CogmalitySQLHelper.ACH_UNLOCKED);	
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context arg1, Cursor c) {
			TextView desc = (TextView) view.findViewById(R.id.toptext);
			TextView label = (TextView) view.findViewById(R.id.bottomtext);
			ImageView img = (ImageView) view.findViewById(R.id.icon);
			int unlocked = c.getInt(mUnlocked);
			if (unlocked == 0) {
				img.setImageDrawable(arg1.getResources().getDrawable(R.drawable.q));				
			} else {
				img.setImageDrawable(arg1.getResources().getDrawable(R.drawable.ic_launcher));				
			}
			desc.setText(c.getString(mName));
			label.setText(c.getString(mType));
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			return mInflater.inflate(R.layout.row, null);
		}

	}


}
