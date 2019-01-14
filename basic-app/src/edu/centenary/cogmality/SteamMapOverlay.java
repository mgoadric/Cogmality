package edu.centenary.cogmality;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;


public class SteamMapOverlay extends ItemizedOverlay {
	
	private ArrayList<MapItem> mOverlays = new ArrayList<MapItem>();
	private int type;
	private CogmalitySQLHelper steamdb;
	private CogmalityMapActivity c;

	public SteamMapOverlay(Drawable defaultMarker, int type, CogmalityMapActivity c) {
		super(boundCenterBottom(defaultMarker));
		this.type = type;
        steamdb = new CogmalitySQLHelper(c);
        this.c = c;
	}
	
	public void addOverlay(MapItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
	  return mOverlays.get(i).getItem();
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	public boolean onTap(int i) {
		synchronized(c) {
			Log.d("Overlay", "" + type + " just tapped " + i + ", id=" + mOverlays.get(i).getdbIndex());
			
			if (!steamdb.getVisibility(type)) {
				Toast.makeText(c, "Change Visibility to pick up", Toast.LENGTH_SHORT).show();
				return false;				
			}
			
			Location l = c.getMyloc().getLastFix();
			double dist = Double.POSITIVE_INFINITY;
			if (l != null) {
				dist = l.distanceTo(mOverlays.get(i).getLocation());
			}
			Log.d("Overlay", "distance = " + dist);		
	
			// if within range for pickup...
			if (dist < CogmalitySQLHelper.PICKUP_RANGE && steamdb.getPickup(type) > 0) {
				
				//MediaPlayer mediaPlayer = MediaPlayer.create(c, R.raw.pickup);
				//mediaPlayer.start(); // no need to call prepare(); create() does that for you
				c.playSound(CogmalityMapActivity.PICKUP);
				
				//get values, based on experience?
				int quality = CogmalitySQLHelper.QUALITY_DEFAULT; /* ignore quality for now (int)(Math.random() * 4) + 1;*/
				int amount = steamdb.getPickup(type); // make based on level, or random? (int)(Math.random() * 3) + 1;

				//add to inventory
				steamdb.addInv(type, quality, amount);
	
				// add stat to gatherer
				steamdb.statIncrement(CogmalitySQLHelper.STAT_GATHERER);
				
				// only inactivate and cache
				SQLiteDatabase db = steamdb.getReadableDatabase();
				ContentValues values = new ContentValues();
				values.put(CogmalitySQLHelper.RES_TIME, "" + System.currentTimeMillis());
				values.put(CogmalitySQLHelper.RES_ACTIVE, "" + 0);
				db.update(CogmalitySQLHelper.RESOURCETABLE, values, BaseColumns._ID + "=" + mOverlays.get(i).getdbIndex(), null);
		        db.close();
		        
		        c.incFoundToday();
		        // update views?
		        c.updateViews();
			} else if (dist >= CogmalitySQLHelper.PICKUP_RANGE){
				Toast.makeText(c, "Too far away", Toast.LENGTH_SHORT).show();
				c.playSound(CogmalityMapActivity.TOOFARAWAY);
				return false;
			} else {
				Toast.makeText(c, "Better tools required", Toast.LENGTH_SHORT).show();				
				c.playSound(CogmalityMapActivity.BETTERTOOLS);
				return false;
			}
		}
		return true;	
	}
	
	public void clear() {
		mOverlays.clear();
		populate();
	}
	
}
