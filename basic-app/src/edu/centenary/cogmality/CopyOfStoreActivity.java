package edu.centenary.cogmality;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CopyOfStoreActivity extends SherlockFragmentActivity {

	private CogmalitySQLHelper steamdb;

	private String owner;
	private List<ParseObject> items;
	private ListView storelist;
	private ProgressDialog pd;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_layout);

        steamdb = new CogmalitySQLHelper(this);

		
		Bundle bundle = this.getIntent().getExtras();
		owner = bundle.getString("username");

		getSupportActionBar().setTitle(owner + "'s store");		

		storelist = (ListView)findViewById(R.id.storelist);

		pd = ProgressDialog.show(this,"Retrieving Store Data","Searching for items...",true,false,null);
		ParseQuery query = new ParseQuery("StoreItems");
		query.whereEqualTo("owner", owner);
		query.findInBackground(new FindCallback() {
			public void done(List<ParseObject> scoreList, ParseException e) {
				pd.dismiss();

				if (e == null) {
					
					items = scoreList;
					updateViews();
					Log.d("score", "Retrieved " + scoreList.size() + " items");
				} else {
					Log.d("score", "Error: " + e.getMessage());
				}
			}
		});
		
		storelist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				ParseObject p = items.get(position);
				Toast.makeText(CopyOfStoreActivity.this, p.getObjectId(), Toast.LENGTH_SHORT).show();	
				
				try {
				ParsePush push = new ParsePush();
				push.setChannel(owner);
				//push.setExpirationTimeInterval(86400);
				push.setData(new JSONObject("{\"action\":\"edu.centenary.cogmality.TRADE_OFFER\","+
						"\"mailmessage\":\"" + "My message here" + "\","+
						"\"itemtype\":\"" + p.get("name") + "\","+
						"\"userfrom\":\"" + steamdb.getUsername() + "\","+
						"\"parseitem_id\":\"" + p.getObjectId() + "\"}"));
				push.sendInBackground();
				} catch (JSONException e) {
					Log.d("StoreMakeOffer", "JSONException: " + e.getMessage());
				}

			}   	

		});     
	}

	public void updateViews() {
		storelist.setAdapter(new StoreListAdapter(
				CopyOfStoreActivity.this,
				R.layout.image_two_column_list_row,
				items));
		storelist.invalidateViews();
	}

	private class StoreListAdapter extends ArrayAdapter<ParseObject> {

		private List<ParseObject> items;
		private int layout;

		public StoreListAdapter(Context context, int textViewResourceId,
				List<ParseObject> items) {
			super(context, textViewResourceId, items);
			layout = textViewResourceId;
			this.items = items;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(layout, null);
			}
			ParseObject o = items.get(position);
			TextView label = (TextView) v.findViewById(R.id.toptext);
			ImageView img = (ImageView) v.findViewById(R.id.icon);

			img.setImageResource(CogmalitySQLHelper.getResByName(CopyOfStoreActivity.this, o.getString("name")));
			String what = o.getString("fullname");
			label.setText(what);
			return v;
		} 
	}
}