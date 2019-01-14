package edu.centenary.cogmality;


import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

public class CraftFragment extends Fragment {

	public static final int SEMI = 127;
	public static final int FULL = 255;
	
	private GridView table;
	private CogmalitySQLHelper steamdb;
	private Cursor c;
	private Cursor gc;
	private CraftCursorAdapter inv;
	private CraftGridAdapter gridadapter;
	private ImageView craftButton;
	private String activeTool;
	private ImageView whatToMake;
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.craft_layout, container, false);

    }
    
    @Override
    public void onStart() {
        super.onStart();

        activeTool = "none";
        
        ListView invlist = (ListView)getView().findViewById(R.id.invlist);
        table = (GridView) getView().findViewById(R.id.craftgrid);
        craftButton = (ImageView) getView().findViewById(R.id.makeButton);
        whatToMake = (ImageView) getView().findViewById(R.id.preview);
        
        steamdb = new CogmalitySQLHelper( getActivity());
        // Get all of the notes from the database and create the item list
		SQLiteDatabase db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.INVTABLE + " LEFT OUTER JOIN " + 
			     CogmalitySQLHelper.ITEMTABLE + " ON (" +
			     CogmalitySQLHelper.INVTABLE + "." + CogmalitySQLHelper.INV_ID + " = " +
			     CogmalitySQLHelper.ITEMTABLE + "." + CogmalitySQLHelper.ITEM_ID + 
			     ")", null, null, null,
				null, null, CogmalitySQLHelper.ITEM_NAME);
        inv = new CraftCursorAdapter( getActivity(), c);
        invlist.setAdapter(inv);
        
        
		gc = db.query(CogmalitySQLHelper.CRAFTTABLE + " LEFT OUTER JOIN " + 
			     CogmalitySQLHelper.ITEMTABLE + " ON (" +
			     CogmalitySQLHelper.CRAFTTABLE + "." + CogmalitySQLHelper.CRAFT_ID + " = " +
			     CogmalitySQLHelper.ITEMTABLE + "." + CogmalitySQLHelper.ITEM_ID + 
			     ")", null, null, null,
				null, null, CogmalitySQLHelper.ITEM_NAME);
       gridadapter = new CraftGridAdapter( getActivity(), gc);
       table.setAdapter(gridadapter);     
       
       invlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				Cursor c = ((CraftCursorAdapter) parent.getAdapter()).getCursor();
				c.moveToPosition(position);

				int type = c.getInt(c.getColumnIndex(CogmalitySQLHelper.INV_ID));
				int amount = c.getInt(c.getColumnIndex(CogmalitySQLHelper.INV_AMOUNT));
				boolean worked = steamdb.addCraft(getActivity(), type, 2);
				if (worked) {
					steamdb.takeInv(type, 2, amount);				
					updateViews();
				}
			}   	
       	
       });
       
       table.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				Cursor c = ((CraftGridAdapter) parent.getAdapter()).getCursor();
				c.moveToPosition(position);

				int type = c.getInt(c.getColumnIndex(CogmalitySQLHelper.CRAFT_ID));
				steamdb.takeCraft(type, 2);				
				steamdb.addInv(type, 2, 1);
				updateViews();				
			}
			
      });
       
       whatToMake.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!activeTool.equals("none")) {
					steamdb.makeCraft(getActivity(), activeTool);
					updateViews();					
				} else {
					((CogmalityActivity)getActivity()).playSound(CogmalityActivity.NOCRAFT);
				}
			}
       });
 

       if (steamdb.getStat(CogmalitySQLHelper.STAT_TUTORIAL) == 1) {
       	AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
    	builder2.setMessage("Crafting lets you build complicated items from simple resources.\n\n" + 
    			"You can make a beam from two pieces of wood, a rod from two beams, and " +
    			"a handle from two rods.\n\nThe item show up to indicate something can be crafted.");
    	builder2.setCancelable(false);
    	builder2.setPositiveButton("OK", null);
    	Dialog dialog = builder2.create();
    	dialog.show();
       }
       
       updateViews();
        
	}
	
	public void onResume() {
		super.onResume();
		updateViews();
	}
	
	public void updateViews() {
		
		activeTool = steamdb.possibleCraft();
		String tomake = "";
		if (!activeTool.equals("none")) {
			tomake = activeTool.substring(activeTool.indexOf("|") + 1, activeTool.length());
			whatToMake.setImageResource(CogmalitySQLHelper.getResByName(getActivity(), tomake));
			activeTool = activeTool.substring(0, activeTool.indexOf("|"));
			craftButton.setImageResource(CogmalitySQLHelper.getResByName(getActivity(), activeTool));

			Animation zoom = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_picture);
			whatToMake.startAnimation(zoom);
			craftButton.startAnimation(zoom);
		} else {
			whatToMake.setImageResource(R.drawable.smallgearcraft);			
			craftButton.setImageResource(R.drawable.smallgearcraft);			
		}
		
		SQLiteDatabase db = steamdb.getReadableDatabase();
		c = db.query(CogmalitySQLHelper.INVTABLE + " LEFT OUTER JOIN " + 
			     CogmalitySQLHelper.ITEMTABLE + " ON (" +
			     CogmalitySQLHelper.INVTABLE + "." + CogmalitySQLHelper.INV_ID + " = " +
			     CogmalitySQLHelper.ITEMTABLE + "." + CogmalitySQLHelper.ITEM_ID + 
			     ")", null, null, null,
				null, null, CogmalitySQLHelper.ITEM_NAME);
		inv.changeCursor(c);
		
		gc = db.query(CogmalitySQLHelper.CRAFTTABLE + " LEFT OUTER JOIN " + 
			     CogmalitySQLHelper.ITEMTABLE + " ON (" +
			     CogmalitySQLHelper.CRAFTTABLE + "." + CogmalitySQLHelper.CRAFT_ID + " = " +
			     CogmalitySQLHelper.ITEMTABLE + "." + CogmalitySQLHelper.ITEM_ID + 
			     ")", null, null, null,
				null, null, CogmalitySQLHelper.ITEM_NAME);
		gridadapter.changeCursor(gc);
		db.close();	
	}
	
}
