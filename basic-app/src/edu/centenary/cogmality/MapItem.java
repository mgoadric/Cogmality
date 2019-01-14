package edu.centenary.cogmality;

import android.location.Location;

import com.google.android.maps.OverlayItem;

public class MapItem {
	
	private OverlayItem item;
	private int dbindex;
	private Location itemloc;
	
	public MapItem(OverlayItem item, int dbindex, double lat, double lon, String provider){
		
		this.item = item;
		this.dbindex = dbindex;
		itemloc = new Location(provider);
		itemloc.setLatitude(lat);
		itemloc.setLongitude(lon);		
	}
	
	public int getdbIndex(){
		return dbindex;
	}
	
	public OverlayItem getItem(){
		return item;
	}
	
	public Location getLocation(){
		return itemloc;
	}	

}
