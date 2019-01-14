package edu.centenary.cogmality;

import java.util.List;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyStoreActivity extends SherlockFragmentActivity {

	private CraftCursorAdapter inv;
	private StoreListAdapter gridadapter;
	private GridView table;
	private CogmalitySQLHelper steamdb;
	private Cursor c;
	private List<ParseObject> items;
	private ProgressDialog pd;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_store_layout);

		ListView invlist = (ListView)findViewById(R.id.storelist);
		table = (GridView)findViewById(R.id.storegrid);

		getSupportActionBar().setTitle("My store");		
		
		steamdb = new CogmalitySQLHelper( this);
		// Get all of the notes from the database and create the item list
		SQLiteDatabase db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.INVTABLE + " LEFT OUTER JOIN " + 
				CogmalitySQLHelper.ITEMTABLE + " ON (" +
				CogmalitySQLHelper.INVTABLE + "." + CogmalitySQLHelper.INV_ID + " = " +
				CogmalitySQLHelper.ITEMTABLE + "." + CogmalitySQLHelper.ITEM_ID + 
				")", null, null, null,
				null, null, CogmalitySQLHelper.ITEM_NAME);
		inv = new CraftCursorAdapter(this, c);
		invlist.setAdapter(inv);


		ParseQuery query = new ParseQuery("StoreItems");
		query.whereEqualTo("owner", steamdb.getUsername());
		pd = ProgressDialog.show(this,"Retrieving Store Data","Searching for items...",true,false,null);

		query.findInBackground(new FindCallback() {
			public void done(List<ParseObject> scoreList, ParseException e) {
				pd.dismiss();
				if (e == null) {
					items = scoreList;
					updateViews();
					Log.d("score", "Retrieved " + scoreList.size() + " scores");
				} else {
					Log.d("score", "Error: " + e.getMessage());
				}
			}
		});

		
		db.close();	

		invlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				Cursor c = ((CraftCursorAdapter) parent.getAdapter()).getCursor();
				c.moveToPosition(position);

				int type = c.getInt(c.getColumnIndex(CogmalitySQLHelper.INV_ID));
				String name = c.getString(c.getColumnIndex(CogmalitySQLHelper.ITEM_NAME));
				String fullname = c.getString(c.getColumnIndex(CogmalitySQLHelper.ITEM_FULLNAME));
				int amount = c.getInt(c.getColumnIndex(CogmalitySQLHelper.INV_AMOUNT));

				if (items.size() < CogmalitySQLHelper.STORE_SIZE) {
					steamdb.takeInv(type, CogmalitySQLHelper.QUALITY_DEFAULT, amount);				


					ParseObject storeItem = new ParseObject("StoreItems");
					storeItem.put("owner", steamdb.getUsername());
					storeItem.put("type", type);
					storeItem.put("name", name);
					storeItem.put("fullname", fullname);
					items.add(storeItem);
					updateViews();

				} else {
					Toast.makeText(MyStoreActivity.this, "Store is full", Toast.LENGTH_SHORT).show();	
				}
			}   	

		});     

		table.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				ParseObject p = items.get(position);
//				steamdb.rejectOffers(p.getObjectId());

				int type = p.getInt("type");
				items.remove(p);
				p.deleteInBackground();
				
				// TODO also remove any mail for that object
				
				steamdb.addInv(type, CogmalitySQLHelper.QUALITY_DEFAULT, 1);
				updateViews();				
			}

		});

	}

	public void onDestroy() {
		if (items != null) {
			try {
				ParseObject.saveAll(items);
			} catch (ParseException pe) {
				
			}
		}
		super.onDestroy();
	}

	public void updateViews() {		
		SQLiteDatabase db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.INVTABLE + " LEFT OUTER JOIN " + 
				CogmalitySQLHelper.ITEMTABLE + " ON (" +
				CogmalitySQLHelper.INVTABLE + "." + CogmalitySQLHelper.INV_ID + " = " +
				CogmalitySQLHelper.ITEMTABLE + "." + CogmalitySQLHelper.ITEM_ID + 
				")", null, null, null,
				null, null, CogmalitySQLHelper.ITEM_NAME);
		inv.changeCursor(c);		
		db.close();	
		
		gridadapter = new StoreListAdapter(this, R.layout.row, items);
		table.setAdapter(gridadapter);     

	}

	private class StoreListAdapter extends ArrayAdapter<ParseObject> {

		private List<ParseObject> items;

		public StoreListAdapter(Context context, int textViewResourceId,
				List<ParseObject> items) {
			super(context, textViewResourceId, items);
			this.items = items;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = new ImageView(MyStoreActivity.this);
			}

			ParseObject o = items.get(position);
			ImageView img = (ImageView)v;
	        img.setLayoutParams(new GridView.LayoutParams(60, 60));
	        img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			img.setImageResource(CogmalitySQLHelper.getResByName(MyStoreActivity.this, o.getString("name")));
			return v;
		} 
	}

	
}
