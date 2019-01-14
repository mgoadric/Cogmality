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
import android.support.v4.app.Fragment;
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

public class StoreFrontFragment extends Fragment {

	private CogmalitySQLHelper steamdb;

	private String owner;
	private List<ParseObject> items;
	private ListView storelist;
	private ProgressDialog pd;


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.store_layout, container, false);

	}

	@Override
	public void onStart() {
		super.onStart();

		if (getActivity() != null) {
			steamdb = new CogmalitySQLHelper(getActivity());


			Bundle bundle = getActivity().getIntent().getExtras();
			owner = bundle.getString("username");

			storelist = (ListView)getView().findViewById(R.id.storelist);

			pd = ProgressDialog.show(getActivity(),"Retrieving Store Data","Searching for items...",true,false,null);
			ParseQuery query = new ParseQuery("StoreItems");
			query.whereEqualTo("owner", owner);
			query.findInBackground(new FindCallback() {
				public void done(List<ParseObject> scoreList, ParseException e) {
					try {
						pd.dismiss();
					} catch (IllegalArgumentException iae) {

					}
					if (e == null) {

						items = scoreList;
						Log.d("items", "items = " + items);
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
					Toast.makeText(getActivity(), p.getObjectId(), Toast.LENGTH_SHORT).show();	

					StoreActivity myac = (StoreActivity)getActivity();
					myac.makeOffer(p.getObjectId(), p.getString("type"), p.getString("fullName"), p.getString("name"));

				}   	

			});    
		}
	}

	public void updateViews() {
		if (getActivity() != null) {
			storelist.setAdapter(new StoreListAdapter(
					getActivity(),
					R.layout.image_two_column_list_row,
					items));
			storelist.invalidateViews();
		}
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
				LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(layout, null);
			}
			ParseObject o = items.get(position);
			TextView label = (TextView) v.findViewById(R.id.toptext);
			ImageView img = (ImageView) v.findViewById(R.id.icon);

			img.setImageResource(CogmalitySQLHelper.getResByName(getActivity(), o.getString("name")));
			String what = o.getString("fullname");
			label.setText(what);
			return v;
		} 
	}
}