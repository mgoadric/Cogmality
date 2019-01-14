package edu.centenary.cogmality;

import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class InventoryFragment extends Fragment {
	
	private CogmalitySQLHelper steamdb;
	private TextView con;
	private ProgressBar pb;
	private GridView grid;
	private InvGridAdapter gridadapt;
	private Cursor c;
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.inventory_layout, container, false);
    }
    
    @Override
    public void onStart() {
        super.onStart();

        steamdb = new CogmalitySQLHelper(getActivity());
        con = (TextView) getView().findViewById(R.id.recipecount);
        pb = (ProgressBar) getView().findViewById(R.id.progressBar1);
        grid = (GridView)  getView().findViewById(R.id.grid);
        SQLiteDatabase db = steamdb.getReadableDatabase();
		
		gridadapt = new InvGridAdapter(getActivity(), c, CogmalitySQLHelper.ITEM_NAME);
		grid.setAdapter(gridadapt);    
		grid.setOnItemClickListener(new OnItemClickListener() {        
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {

				Cursor c = ((InvGridAdapter) parent.getAdapter()).getCursor();
				c.moveToPosition(position);

				String how = c.getString(c.getColumnIndex(CogmalitySQLHelper.REC_KEY));
				String tool = c.getString(c.getColumnIndex(CogmalitySQLHelper.REC_TOOL));
				String what = c.getString(c.getColumnIndex(CogmalitySQLHelper.ITEM_NAME));
				String fullName = c.getString(c.getColumnIndex(CogmalitySQLHelper.ITEM_FULLNAME));
//
//				Context mContext = getApplicationContext();
//				Dialog dialog = new Dialog(mContext);
//
//				dialog.setContentView(R.layout.custom_dialog);
//				dialog.setTitle("Custom Dialog");
//
//				TextView text = (TextView) dialog.findViewById(R.id.text);
//				text.setText("Hello, this is a custom dialog!");
//				ImageView image = (ImageView) dialog.findViewById(R.id.image);
//				image.setImageResource(R.drawable.android);
//								
				Builder adb = new AlertDialog.Builder(getActivity());
				LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService
			      (Context.LAYOUT_INFLATER_SERVICE);
				View recview = inflater.inflate(R.layout.recipe_layout, null);
				ImageView toolimg = (ImageView)recview.findViewById(R.id.toolimage);
				toolimg.setImageDrawable(getActivity().getResources().getDrawable(CogmalitySQLHelper.getResByName(getActivity(), tool)));
				ImageView[] images = {(ImageView)recview.findViewById(R.id.image1),
						(ImageView)recview.findViewById(R.id.image2),
						(ImageView)recview.findViewById(R.id.image3),
						(ImageView)recview.findViewById(R.id.image4)};
				TextView[] pluses = {(TextView)recview.findViewById(R.id.text1),
						(TextView)recview.findViewById(R.id.text2),
						(TextView)recview.findViewById(R.id.text3)};
				StringTokenizer st = new StringTokenizer(how, ",");
				int size = 0;
				while (st.hasMoreTokens()) {
					images[size].setImageDrawable(getActivity().getResources().getDrawable(CogmalitySQLHelper.getResByName(getActivity(), st.nextToken())));
					size++;
				}
				for (; size < images.length; size++) {
					images[size].setVisibility(View.GONE);
					pluses[size-1].setVisibility(View.GONE);
				}
				adb.setView(recview);
				adb.setTitle(fullName);
				adb.setIcon(CogmalitySQLHelper.getResByName(getActivity(), what));
				
				adb.setPositiveButton("Ok", null);
				adb.show();

				
			}    
		});
		db.close();
    }

	public void onResume(){
		super.onResume();
		updateViews();
	}

	public void onDestroy() {
		super.onDestroy();
	}
	
	public void updateViews() {
		int count = steamdb.completionCount();
		if (count == 0) {
			con.setText("No Knowledge Learned Yet");
		} else {
			con.setText(steamdb.getCompletion());
			//con.setVisibility(View.GONE);
		}
		pb.setProgress(count);
        pb.setMax(steamdb.completionTotal());
        //pb.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.gear));
		SQLiteDatabase db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.ITEMTABLE  + " JOIN " + 
			     CogmalitySQLHelper.RECIPETABLE + " ON (" +
			     CogmalitySQLHelper.RECIPETABLE + "." + CogmalitySQLHelper.REC_OUTPUT_ID + " = " +
			     CogmalitySQLHelper.ITEMTABLE + "." + CogmalitySQLHelper.ITEM_NAME + 
			     ")", null, 
				CogmalitySQLHelper.ITEM_DISCOVERED + "=1 AND " + 
				CogmalitySQLHelper.ITEM_LEVEL + ">=1", null,
				null, null, CogmalitySQLHelper.ITEM_BUYPRICE);
		gridadapt.changeCursor(c);
		db.close();		

	}

}
