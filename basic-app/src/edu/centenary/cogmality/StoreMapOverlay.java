package edu.centenary.cogmality;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;


public class StoreMapOverlay extends ItemizedOverlay {

	private ArrayList<MapItem> mOverlays = new ArrayList<MapItem>();
	private CogmalitySQLHelper steamdb;
	private CogmalityMapActivity c;

	public StoreMapOverlay(Drawable defaultMarker, CogmalityMapActivity c) {
		super(boundCenterBottom(defaultMarker));
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
			String owner = mOverlays.get(i).getLocation().getProvider();
			Log.d("Store", "just tapped " + i + ", id=" + owner);

			Location l = c.getMyloc().getLastFix();
			double dist = Double.POSITIVE_INFINITY;
			if (l != null) {
				dist = l.distanceTo(mOverlays.get(i).getLocation());
			}
			Log.d("Overlay", "distance = " + dist);		

			// if within range, visit the store...
			if (dist < CogmalitySQLHelper.PICKUP_RANGE) {

				c.playSound(CogmalityMapActivity.DOOROPEN);

				Toast.makeText(c, "Entering Store by " + owner, Toast.LENGTH_SHORT).show();
				// Fire off intent 

				if (steamdb.getUsername().equals(owner)) {
					Intent in = new Intent(c, MyStoreActivity.class);
					c.startActivity(in);						
				} else {
					Intent in = new Intent(c, StoreActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("username", owner);
					in.putExtras(bundle);
					c.startActivity(in);											
				}

			} else if (dist >= CogmalitySQLHelper.PICKUP_RANGE){
				Toast.makeText(c, "Too far away", Toast.LENGTH_SHORT).show();
				c.playSound(CogmalityMapActivity.TOOFARAWAY);
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
