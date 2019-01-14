package edu.centenary.cogmality;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;

public class SellFragment extends Fragment {
	private CogmalitySQLHelper steamdb;
	private ListView invlist;
	private InventoryCursorAdapter inv;
	private Cursor c;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bazaar_sell_layout, container, false);

    }
    
    @Override
    public void onStart() {
        super.onStart();
        steamdb = new CogmalitySQLHelper(getActivity());
        invlist = (ListView)getView().findViewById(R.id.listView1);
        
        // Get all of the notes from the database and create the item list
		SQLiteDatabase db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.INVTABLE + " LEFT OUTER JOIN " + 
			     CogmalitySQLHelper.ITEMTABLE + " ON (" +
			     CogmalitySQLHelper.INVTABLE + "." + CogmalitySQLHelper.INV_ID + " = " +
			     CogmalitySQLHelper.ITEMTABLE + "." + CogmalitySQLHelper.ITEM_ID + 
			     ")", null, null, null,
				null, null, CogmalitySQLHelper.ITEM_SELLPRICE + " DESC");
        inv = new InventoryCursorAdapter(getActivity(), c);
        db.close();
        invlist.setAdapter(inv);

        
        invlist.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				Cursor c = ((InventoryCursorAdapter) parent.getAdapter()).getCursor();
				c.moveToPosition(position);
				
				int sellprice = c.getInt(c.getColumnIndex(CogmalitySQLHelper.ITEM_SELLPRICE));

				steamdb.changeMoney(sellprice);
				if (steamdb.getMoney() >= 10000) {
					steamdb.unlockAchievement("TENK");		
				} else if (steamdb.getMoney() >= 1000) {
					steamdb.unlockAchievement("ONEK");
				}
				
				int type = c.getInt(c.getColumnIndex(CogmalitySQLHelper.INV_ID));
				int amount = c.getInt(c.getColumnIndex(CogmalitySQLHelper.INV_AMOUNT));
				steamdb.takeInv(type, 2, amount);				

				updateViews();
				return true;
				
				
			}
        	
        	
        });
        
	}
	
	public void onResume(){
		super.onResume();
		updateViews();
	}

	
	public void updateViews() {
		SQLiteDatabase db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.INVTABLE + " LEFT OUTER JOIN " + 
			     CogmalitySQLHelper.ITEMTABLE + " ON (" +
			     CogmalitySQLHelper.INVTABLE + "." + CogmalitySQLHelper.INV_ID + " = " +
			     CogmalitySQLHelper.ITEMTABLE + "." + CogmalitySQLHelper.ITEM_ID + 
			     ")", null, null, null,
				null, null, CogmalitySQLHelper.ITEM_SELLPRICE + " DESC");
		inv.changeCursor(c);
		db.close();		

	}
}
