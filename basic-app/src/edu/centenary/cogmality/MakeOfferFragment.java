package edu.centenary.cogmality;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MakeOfferFragment extends Fragment {

	private CraftCursorAdapter inv;
	private StoreListAdapter gridadapter;
	private GridView table;
	private Button makeOffer;
	private CogmalitySQLHelper steamdb;
	private Cursor c;
	private List<ParseObject> items;
	private ProgressDialog pd;

	private String owner;
	private String item;
	private String type;
	private String fullName;
	private String name;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			this.item = getArguments().getString("item");
			this.type = getArguments().getString("type");
			this.fullName = getArguments().getString("fullName");
			this.name = getArguments().getString("name");
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.make_offer_layout, container, false);

	}

	@Override
	public void onStart() {
		super.onStart();

		Bundle bundle = getActivity().getIntent().getExtras();
		owner = bundle.getString("username");

		Button makeOffer = (Button)getView().findViewById(R.id.offerButton);
		ImageView itemOffer = (ImageView)getView().findViewById(R.id.itemImage);
		Log.d("makeoffer", "itemOffer = " + itemOffer);
		itemOffer.setImageResource(CogmalitySQLHelper.getResByName(getActivity(), name));

		ListView invlist = (ListView)getView().findViewById(R.id.storelist);
		table = (GridView)getView().findViewById(R.id.storegrid);

		steamdb = new CogmalitySQLHelper( getActivity());
		// Get all of the notes from the database and create the item list
		SQLiteDatabase db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.INVTABLE + " LEFT OUTER JOIN " + 
				CogmalitySQLHelper.ITEMTABLE + " ON (" +
				CogmalitySQLHelper.INVTABLE + "." + CogmalitySQLHelper.INV_ID + " = " +
				CogmalitySQLHelper.ITEMTABLE + "." + CogmalitySQLHelper.ITEM_ID + 
				")", null, null, null,
				null, null, CogmalitySQLHelper.ITEM_NAME);
		inv = new CraftCursorAdapter(getActivity(), c);
		invlist.setAdapter(inv);

		items = new ArrayList<ParseObject>();

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

				if (items.size() < CogmalitySQLHelper.OFFER_SIZE) {
					steamdb.takeInv(type, CogmalitySQLHelper.QUALITY_DEFAULT, amount);				


					ParseObject storeItem = new ParseObject("StoreItems");
					storeItem.put("owner", steamdb.getUsername());
					storeItem.put("type", type);
					storeItem.put("name", name);
					storeItem.put("fullname", fullname);
					items.add(storeItem);
					updateViews();

				} else {
					Toast.makeText(getActivity(), "Limit of 5 per offer", Toast.LENGTH_SHORT).show();	
				}
			}   	

		});     

		table.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				ParseObject p = items.get(position);

				int type = p.getInt("type");
				items.remove(position);

				// TODO also remove any mail for that object

				steamdb.addInv(type, CogmalitySQLHelper.QUALITY_DEFAULT, 1);
				updateViews();				
			}

		});

		makeOffer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (items.size() > 0) {
					try {
						ParsePush push = new ParsePush();
						push.setChannel(owner);
						//push.setExpirationTimeInterval(86400);

						StringBuffer myoffer = new StringBuffer();
						for (ParseObject p: items) {
							myoffer.append(p.getString("name") + "|");
						}
						String myOffer = myoffer.substring(0, myoffer.length() - 1);
						push.setData(new JSONObject("{\"action\":\"edu.centenary.cogmality.TRADE_OFFER\","+
								"\"mailmessage\":\"" + "My message here" + "\","+
								"\"itemtype\":\"" + name + "\","+
								"\"maildata\":\"" + myOffer + "\","+
								"\"fullname\":\"" + fullName + "\","+
								"\"userfrom\":\"" + steamdb.getUsername() + "\","+
								"\"parseitem_id\":\"" + item + "\"}"));
						push.sendInBackground();
					} catch (JSONException e) {
						Log.d("StoreMakeOffer", "JSONException: " + e.getMessage());
					}
					items.removeAll(items);
					((StoreActivity)getActivity()).returnFront();
					//getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_BACK)); 
				}
			}

		});

	}

	public void onDestroy() {
		while (items != null && items.size() > 0) {
			ParseObject p = items.get(0);

			int type = p.getInt("type");
			items.remove(0);

			steamdb.addInv(type, CogmalitySQLHelper.QUALITY_DEFAULT, 1);
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

		gridadapter = new StoreListAdapter(getActivity(), R.layout.row, items);
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
				v = new ImageView(getActivity());
			}

			ParseObject o = items.get(position);
			ImageView img = (ImageView)v;
			img.setLayoutParams(new GridView.LayoutParams(60, 60));
			img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			img.setImageResource(CogmalitySQLHelper.getResByName(getActivity(), o.getString("name")));
			return v;
		} 
	}


}

