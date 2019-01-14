package edu.centenary.cogmality;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.location.Location;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
//import com.parse.FindCallback;
//import com.parse.ParseException;
//import com.parse.ParseGeoPoint;
//import com.parse.ParseObject;
//import com.parse.ParseQuery;
//import com.parse.SaveCallback;


public class CogmalityMapActivity extends MapActivity { // implements SensorEventListener {

	public static final int NONE = -1;
	public static final int RES = -2;
	public static final int CACHE = -3;
	public static final int DIALOG_LAYERS = 0;

	private CogmalitySQLHelper steamdb;
	private Handler mHandler = new Handler();
	private List<Overlay> mapOverlays;
	private ArrayList<SteamMapOverlay> overs;
	private MyLocationOverlay myloc;
	private MapView mapView;
	private ProgressDialog pd;
	private boolean manualRefresh;
	private int foundToday;
	private RefreshResTask restask;

	private SoundPool soundPool;
	private SparseIntArray soundsMap;
	public static int PICKUP=1;
	public static int TOOFARAWAY=2;
	public static int BETTERTOOLS=3;
	public static int DOOROPEN=4;

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * Called when the activity is begun. Initializes the DB and the map,
	 * adds in the current location, and updates the views.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.steam_map);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.getController().setZoom(18);
		myloc = new SteamLocationOverlay(this, mapView);

		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundsMap = new SparseIntArray();
		soundsMap.put(PICKUP, soundPool.load(this, R.raw.pickup, 1));
		soundsMap.put(TOOFARAWAY, soundPool.load(this, R.raw.toofaraway, 1));
		soundsMap.put(BETTERTOOLS, soundPool.load(this, R.raw.bettertools, 1));
		soundsMap.put(DOOROPEN, soundPool.load(this, R.raw.dooropen, 1));

		steamdb = new CogmalitySQLHelper(this);
		foundToday = 0;
		updateViews();	    
	}

	/**
	 * Turn back on the location and compass features, make an update call
	 */
	@Override
	public void onResume() {
		super.onResume();
		myloc.enableMyLocation();
		myloc.enableCompass();
		mHandler.removeCallbacks(mUpdateTimeTask);
		mHandler.postDelayed(mUpdateTimeTask, CogmalitySQLHelper.FIRST_LOOKUP);
		pd = ProgressDialog.show(CogmalityMapActivity.this,"Attempting Expedition","Finding Location...",true,false,null);
	}

	/**
	 * Stop the update call loop, and turn off the location manager
	 */
	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mUpdateTimeTask);
		myloc.disableMyLocation();
		myloc.disableCompass();
		manualRefresh = false;
		if (pd != null) {
			pd.dismiss();
		}
	}

	public void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(mUpdateTimeTask);
		if (restask != null) {
			restask.cancel(true);
		}

		if (foundToday > 10) {
			steamdb.journalEntry("location_map_large", 
					"What a haul! Procured " + foundToday + " resources in my most recent expedition.");
		} else if (foundToday > 1) {
			steamdb.journalEntry("location_map_large", 
					"Picked up " + foundToday + " resources in a recent expedition, looking forward to crafting with them later.");
		} else if (foundToday == 1) {
			steamdb.journalEntry("location_map_large", 
					"Found " + foundToday + " resource. I was feeling sluggish and unfocused. I can do better than this.");
		} else {
			//steamdb.journalEntry("location_map_large", "Well, that was a bust.");
		}
	}

	public void playSound(int sound) {
		AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = streamVolumeCurrent / streamVolumeMax;  

		soundPool.play(soundsMap.get(sound), volume, volume, 1, 0, 1);
	}

	/**
	 * Need to get the location for the Overlay
	 */
	public MyLocationOverlay getMyloc() {
		return myloc;
	}

	public void incFoundToday() {
		foundToday++;
	}

	/**
	 * The runnable object on the timer. When activated and able, it will run the 
	 * Asynchonous refresh task for talking to Google Places API
	 */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			if (pd != null) {
				pd.dismiss();
			}
			if (myloc.getLastFix() != null && myloc.getMyLocation() != null) {
				GeoPoint g = myloc.getMyLocation();

				if (myloc.getLastFix().getAccuracy() < CogmalitySQLHelper.ACCURACY) {
					mapView.getController().animateTo(g);
					restask = new RefreshResTask();
					restask.execute();
					manualRefresh = true;
				} else {
					Log.d("Time Task", "waiting for sensor");
					Toast.makeText(CogmalityMapActivity.this, "Waiting for sensor accuracy", Toast.LENGTH_SHORT).show();
					mHandler.removeCallbacks(mUpdateTimeTask);
					mHandler.postDelayed(mUpdateTimeTask, 2000);			   
				}
			} else {
				Log.d("Time Task", "can't find location..");
				Toast.makeText(CogmalityMapActivity.this, "Waiting for GPS", Toast.LENGTH_SHORT).show();
				mHandler.removeCallbacks(mUpdateTimeTask);
				mHandler.postDelayed(mUpdateTimeTask, 2000);			   
			}
		}
	};	

	/**
	 * Gathers resources from the Google Places API 
	 * or from the local cache
	 */
	private class RefreshResTask extends AsyncTask<String, Void, Integer> {
		protected void onPreExecute() {
			pd = ProgressDialog.show(CogmalityMapActivity.this,"Updating Resources","Gathering Information...",true,false,null);
		}

		protected Integer doInBackground(String... urls) {
			return refreshResources();
		}

		protected void onPostExecute(Integer v) {
			pd.dismiss();
			switch(v) {
			case RES:
			case CACHE:
				if (!isCancelled()) {
					SQLiteDatabase db = steamdb.getReadableDatabase();
					GeoPoint g = myloc.getMyLocation();
					if (g != null) {
						ContentValues values = new ContentValues();
						values.put(CogmalitySQLHelper.STAT_LASTRES, "" + System.currentTimeMillis());
						values.put(CogmalitySQLHelper.STAT_LASTLAT, "" + g.getLatitudeE6()/1E6);
						values.put(CogmalitySQLHelper.STAT_LASTLON, "" + g.getLongitudeE6()/1E6);
						db.update(CogmalitySQLHelper.STATTABLE, values, null, null);
					}
					db.close();
				}
				if (!isCancelled()) {
					updateViews();
				}
				break;
			}
			if (v > 0) {
				if (v < 60000) {
					Toast.makeText(CogmalityMapActivity.this, "Less than a minute left", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(CogmalityMapActivity.this, "About " + (1 + (v/60000)) + " minutes left", Toast.LENGTH_SHORT).show();	    			 
				}
				mHandler.removeCallbacks(mUpdateTimeTask);
				mHandler.postDelayed(mUpdateTimeTask, v + 100);
			}
		}
	}

	// TODO Move this to CogmalitySQLHelper
	public synchronized Integer refreshResources() {
		int updated = NONE;
		Log.d("Map", "Refreshing Resources");

		// Make sure you have a valid location before refreshing resources
		GeoPoint g = myloc.getMyLocation();
		if (g == null) {
			return NONE;
		}

		if (restask.isCancelled()) {
			return NONE;
		}

		// Make sure you are after time, or are far enough away from previous update
		long since = System.currentTimeMillis() - steamdb.getLastResTime();
		double lastlat = steamdb.getLastLat();
		double lastlon = steamdb.getLastLon();
		Location l = myloc.getLastFix();
		double dist = Double.POSITIVE_INFINITY;
		if (l != null) {
			Location itemloc = new Location("temp");
			itemloc.setLatitude(lastlat);
			itemloc.setLongitude(lastlon);	
			dist = l.distanceTo(itemloc);

		}
		Log.d("Map", "distance = " + dist);		
		Log.d("Map", "Refresh time since " + since);

		if (since < CogmalitySQLHelper.RESTIME && dist < CogmalitySQLHelper.LOOKUP_RANGE) {
			return (int)(CogmalitySQLHelper.RESTIME - since);
		}		

		if (restask.isCancelled()) {
			return NONE;
		}

		// The standard items you always find around you.
		// In the tutorial, get only Wood HARDCODED??
		ArrayList<Integer> types = new ArrayList<Integer>();
		int tutLevel = steamdb.currentLevel();
		if (tutLevel == 1) {
			for (int i = 0; i < 4; i++) {
				types.add(CogmalitySQLHelper.WOOD_TYPE);  // WATCH OUT FOR HARDCODING!!!!
			}
		} else { // Otherwise get up to 6, but always at least 1.
			int level = steamdb.currentLevel();
			do {
				int type = CogmalitySQLHelper.WOOD_TYPE;
				do {
					type = steamdb.randomBasic();
				} while (steamdb.getPickup(type) == 0);
				types.add(type);
				level++;
			} while (level <= 5);
		}

		SQLiteDatabase db = steamdb.getReadableDatabase();
		db.beginTransaction();

		// Always put some near you at low exp levels
		try {
			for (int i = 0; i < types.size(); i++) {
				if (restask.isCancelled()) {
					return NONE;
				}

				int type = types.get(i);
				String wherenow1 = CogmalitySQLHelper.RES_GOOGLEID + "=\"here" + i + "\"";
				ContentValues myvalues = new ContentValues();
				db.delete(CogmalitySQLHelper.RESOURCETABLE, wherenow1, null);
				myvalues.put(CogmalitySQLHelper.RES_TIME, "" + System.currentTimeMillis());
				myvalues.put(CogmalitySQLHelper.RES_ID, "" + type);
				myvalues.put(CogmalitySQLHelper.RES_LAT, "" + (g.getLatitudeE6()/1E6 + (0.002 * Math.random()) - 0.001));
				myvalues.put(CogmalitySQLHelper.RES_LON, "" + (g.getLongitudeE6()/1E6 + (0.002 * Math.random()) - 0.001));
				myvalues.put(CogmalitySQLHelper.RES_AMOUNT, "" + 1);
				myvalues.put(CogmalitySQLHelper.RES_ACTIVE, "" + 1);
				myvalues.put(CogmalitySQLHelper.RES_GOOGLEID, "" + "here" + i);
				db.insertOrThrow(CogmalitySQLHelper.RESOURCETABLE, null, myvalues);
			}			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		// Look for stores
//		storeLookup();

		if (restask.isCancelled()) {
			return NONE;
		}

		Cursor cursor = db.query(CogmalitySQLHelper.PLACESTABLE, null, null, null,
				null, null, null);
		boolean foundclose = false;
		long whichplace = 0;
		while (cursor.moveToNext() && !foundclose) {		
			Location placeloc = new Location("temp");
			placeloc.setLatitude(cursor.getDouble(cursor.getColumnIndex(CogmalitySQLHelper.PLACES_LAT)));
			placeloc.setLongitude(cursor.getDouble(cursor.getColumnIndex(CogmalitySQLHelper.PLACES_LON)));		
			l = myloc.getLastFix();
			dist = Double.POSITIVE_INFINITY;
			if (l != null) {
				dist = l.distanceTo(placeloc);
			}
			if (dist < CogmalitySQLHelper.LOOKUP_RANGE * 0.8) {
				foundclose = true;
				whichplace = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
			}
		}

		if (foundclose) {
			if (restask.isCancelled()) {
				return NONE;
			}

			Log.d("Map", "Resource Cache successful, found place " + whichplace);
			String wherenow = CogmalitySQLHelper.RES_PLACE + "=\"" + whichplace + "\"";
			ContentValues values = new ContentValues();
			values.put(CogmalitySQLHelper.RES_TIME, "" + System.currentTimeMillis());
			//values.put(CogmalitySQLHelper.RES_ACTIVE, "" + 1);
			// Make resources disappear forever when picked up.
			db.update(CogmalitySQLHelper.RESOURCETABLE, values, wherenow, null);			
			updated = CACHE;

		} else {	

			// Otherwise, send to web
			String where = "https://maps.googleapis.com/maps/api/place/search/json?" + 
			"location=" + g.getLatitudeE6()/1E6 + "," + g.getLongitudeE6()/1E6 + 
			"&radius=" + CogmalitySQLHelper.LOOKUP_RANGE + 
			"&sensor=true" + 
			"&key=" + CogmalitySQLHelper.PLACES_API_KEY;
			Log.d("LocRun", where);

			HttpGet httpget = new HttpGet (where);
			HttpParams httpParameters = new BasicHttpParams();

			// Set the timeout in milliseconds until a connection is established.
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient httpclient = new DefaultHttpClient(httpParameters);

			//get back JSON
			db.beginTransaction();
			try{

				ContentValues values = new ContentValues();
				values.put(CogmalitySQLHelper.PLACES_TIME, "" + System.currentTimeMillis());
				values.put(CogmalitySQLHelper.PLACES_LAT, "" + g.getLatitudeE6()/1E6);
				values.put(CogmalitySQLHelper.PLACES_LON, "" + g.getLongitudeE6()/1E6);
				long whatplace = db.insertOrThrow(CogmalitySQLHelper.PLACESTABLE, null, values);

				int success;
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String response = httpclient.execute(httpget, responseHandler);
				//Log.d("getData: ", response);
				if (restask.isCancelled()) {
					return NONE;
				}

				JSONObject obj = new JSONObject(response);
				JSONArray jarray = obj.getJSONArray("results");

				for(int i = 0; i< jarray.length(); i++){
					if (restask.isCancelled()) {
						return NONE;
					}

					JSONObject place = jarray.getJSONObject(i);
					//Log.d("MapStuff", "" + place.getString("name"));
					String id = place.getString("id");
					JSONArray ary = place.getJSONArray("types");

					// See if we should ignore this type of place
					int type = steamdb.labeledBasic(ary);
//					int type = 0;
//					for(int j = 0; j < ary.length() ; j++){
//						if (ary.getString(j).equals("neighborhood")) {
//							type = -1;
//							break;
//						}
//					}

					// When valid see if it exists in the database
					if (type != -1) {
						
						// replace with particular based on type from google
						//type = steamdb.randomBasic();
						
						JSONObject geo = place.getJSONObject("geometry");
						JSONObject loc = geo.getJSONObject("location");
						double lat = loc.getDouble("lat");
						double lon = loc.getDouble("lng");
						//Log.d("MapStuff", "Latitude " + lat);
						//Log.d("MapStuff", "Longitude" + lon);

						String wherenow = CogmalitySQLHelper.RES_GOOGLEID + "=\"" + id + "\"";
						values = new ContentValues();
						values.put(CogmalitySQLHelper.RES_TIME, "" + System.currentTimeMillis());
						values.put(CogmalitySQLHelper.RES_ACTIVE, "" + 1);
						success = db.update(CogmalitySQLHelper.RESOURCETABLE, values, wherenow, null);

						// If not found, assign a random type and add it to the database
						if (success == 0) {
							values.put(CogmalitySQLHelper.RES_LAT, "" + lat);
							values.put(CogmalitySQLHelper.RES_LON, "" + lon);
							values.put(CogmalitySQLHelper.RES_AMOUNT, "" + 1);
							values.put(CogmalitySQLHelper.RES_ID, "" + type);
							values.put(CogmalitySQLHelper.RES_ACTIVE, "" + 1);
							values.put(CogmalitySQLHelper.RES_PLACE, "" + whatplace);
							values.put(CogmalitySQLHelper.RES_GOOGLEID, "" + id);
							db.insertOrThrow(CogmalitySQLHelper.RESOURCETABLE, null, values);
						}
					}
				}

				updated = RES;
				db.setTransactionSuccessful();

			} catch (ClientProtocolException e) {
				Log.d("SniffServiceCPE:", e.toString());
			} catch (IOException e) {
				Log.d("SniffServiceIO:", e.toString());
			} catch (JSONException je) {
				Log.d("SniffService:", je.getMessage());
			} catch (IllegalStateException ise) {
				Log.d("sniffService:", ise.getMessage());
			} finally {
				db.endTransaction();
			}
			db.close();
		}

		return updated;
	}

	public synchronized void updateViews() {
		mapOverlays = mapView.getOverlays();
		mapOverlays.clear();
		overs = new ArrayList<SteamMapOverlay>();
		StoreMapOverlay storeovers = new StoreMapOverlay(this.getResources().getDrawable(R.drawable.store), this);
		StoreMapOverlay mystoreovers = new StoreMapOverlay(this.getResources().getDrawable(R.drawable.mystore), this);

		// TODO move this into CogmalitySQLHelper to reduce clutter
		String whoami = steamdb.getUsername();
		SQLiteDatabase db = steamdb.getReadableDatabase();

		// Only get the basic items, not everything...
		Cursor cursor = db.query(CogmalitySQLHelper.ITEMTABLE, null, CogmalitySQLHelper.ITEM_LEVEL + " = 0", null,
				null, null, null);

		while (cursor.moveToNext()) {
			String name = cursor.getString(cursor.getColumnIndex(CogmalitySQLHelper.ITEM_NAME));
			int i = cursor.getInt(cursor.getColumnIndex(CogmalitySQLHelper.ITEM_ID));
			int vis = cursor.getInt(cursor.getColumnIndex(CogmalitySQLHelper.ITEM_VISIBLE));
			if (vis == 1) {
				overs.add(new SteamMapOverlay(this.getResources().getDrawable(CogmalitySQLHelper.getResByName(this, name)), i, this));
			} else {
				overs.add(new SteamMapOverlay(this.getResources().getDrawable(R.drawable.square), i, this));
			}
		}

		// TODO make this so we only active resources within range
		cursor = db.query(CogmalitySQLHelper.RESOURCETABLE, null, CogmalitySQLHelper.RES_ACTIVE + " = 1", null,
				null, null, null);

		while (cursor.moveToNext()) {
			double lat = cursor.getDouble(cursor.getColumnIndex(CogmalitySQLHelper.RES_LAT));
			double lon = cursor.getDouble(cursor.getColumnIndex(CogmalitySQLHelper.RES_LON));
			int type = cursor.getInt(cursor.getColumnIndex(CogmalitySQLHelper.RES_ID));
			GeoPoint point = new GeoPoint((int)(lat*1e6),(int)(lon*1e6));
			OverlayItem w = new OverlayItem(point, "", "");
			int x = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
			MapItem m = new MapItem(w, x, lat, lon, "item");
			overs.get(type).addOverlay(m);

		}
		cursor.close();
		//db.close();

		cursor = db.query(CogmalitySQLHelper.OTHERSTORETABLE, null, null, null,
				null, null, null);

		while (cursor.moveToNext()) {
			double lat = cursor.getDouble(cursor.getColumnIndex(CogmalitySQLHelper.OTHERSTORE_LAT));
			double lon = cursor.getDouble(cursor.getColumnIndex(CogmalitySQLHelper.OTHERSTORE_LON));
			String name = cursor.getString(cursor.getColumnIndex(CogmalitySQLHelper.OTHERSTORE_NAME));
			GeoPoint point = new GeoPoint((int)(lat*1e6),(int)(lon*1e6));
			OverlayItem w = new OverlayItem(point, "", "");
			int x = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
			MapItem m = new MapItem(w, x, lat, lon, name);
			if (name.equals(whoami)) {
				mystoreovers.addOverlay(m);
			} else {
				storeovers.addOverlay(m);
			}
		}
		cursor.close();
		db.close();

		mapOverlays.add(myloc);
		for (SteamMapOverlay smo : overs) {
			if (smo.size() > 0) {
				mapOverlays.add(smo);
			}
		}
		if (storeovers.size() > 0) {
			mapOverlays.add(storeovers);
		}
		if (mystoreovers.size() > 0) {
			mapOverlays.add(mystoreovers);
		}
	}

//	public void storeLookup() {
//		// Look for stores
//		GeoPoint g = myloc.getMyLocation();
//		if (g == null) {
//			return;
//		}
//
//		ParseGeoPoint point = new ParseGeoPoint(g.getLatitudeE6()/1E6, g.getLongitudeE6()/1E6);
//		ParseQuery query = new ParseQuery("PlaceObject");
//		query.whereNear("location", point);
//		query.setLimit(10);
//		query.findInBackground(new FindCallback() {
//
//			@Override
//			public void done(List<ParseObject> arg0, ParseException arg1) {
//				// TODO Auto-generated method stub
//				//Toast.makeText(CogmalityMapActivity.this, "Found " + arg0.size() + " store!", Toast.LENGTH_SHORT).show();
//				if (restask.isCancelled()) {
//					return;
//				}
//
//				synchronized(CogmalityMapActivity.this) {
//					SQLiteDatabase db = steamdb.getReadableDatabase();
//					db.delete(CogmalitySQLHelper.OTHERSTORETABLE, null, null);
//					ContentValues myvalues = new ContentValues();
//					for (ParseObject p : arg0) {
//						myvalues.put(CogmalitySQLHelper.OTHERSTORE_LAT, "" + ((ParseGeoPoint)p.get("location")).getLatitude());
//						myvalues.put(CogmalitySQLHelper.OTHERSTORE_LON, "" + ((ParseGeoPoint)p.get("location")).getLongitude());
//						myvalues.put(CogmalitySQLHelper.OTHERSTORE_NAME, "" + p.get("username"));
//						db.insertOrThrow(CogmalitySQLHelper.OTHERSTORETABLE, null, myvalues);
//					}				
//					db.close();
//				}
//				updateViews();
//			} 
//		});
//
//	}

	/**
	 * Extends the image on the screen to show a green transparent circle where the 
	 * user can pick up items in that range.
	 * @author mgoadric
	 *
	 */
	private class SteamLocationOverlay extends MyLocationOverlay {

		/* Paint for the circle */
		protected final Paint mCirclePaintFill;
		protected final Paint mCirclePaintBorder;

		private final Point mMapCoords = new Point();

		public SteamLocationOverlay(Context context, MapView mapView) {
			super(context, mapView);

			// Make the inner circle 
			mCirclePaintFill = new Paint();
			this.mCirclePaintFill.setColor(Color.GREEN);
			this.mCirclePaintFill.setAlpha(20);
			this.mCirclePaintFill.setStyle(Style.FILL);

			// Color up the border 
			mCirclePaintBorder = new Paint();
			this.mCirclePaintBorder.setColor(Color.GREEN);
			this.mCirclePaintBorder.setAlpha(150);
			this.mCirclePaintBorder.setStyle(Style.STROKE);
		}

		@Override
		public boolean draw(Canvas c, MapView view, boolean shadow, long when) {
			//super.draw(c, view, shadow, when);

			// Make sure the location is there and is accurate
			if (getMyLocation() == null || getLastFix().getAccuracy() >= CogmalitySQLHelper.ACCURACY) {
				return false;
			} 

			// Get the projection and turn the location into pixels
			final Projection pj = view.getProjection();
			pj.toPixels(getMyLocation(), mMapCoords);
			final float radius = pj.metersToEquatorPixels(CogmalitySQLHelper.PICKUP_RANGE + 10);

			// Draw the circle on the map
			c.drawCircle(mMapCoords.x, mMapCoords.y, radius, this.mCirclePaintFill);            
			c.drawCircle(mMapCoords.x, mMapCoords.y, radius, this.mCirclePaintBorder);
			c.drawCircle(mMapCoords.x, mMapCoords.y, 5, this.mCirclePaintBorder);

			return true;
		}
	}

	/* OPTIONS MENU */

	/**
	 * Creates the option menu from the XML
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		//		if (steamdb.getStat(CogmalitySQLHelper.STAT_STORE) == 1) {
		//getMenuInflater().inflate(R.menu.map_store_menu, menu);
		//		} else {
					getMenuInflater().inflate(R.menu.map_menu, menu);			
		//		}
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Show the selected option from the menu. 
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.find_me:
			// Find your current GPS location
			GeoPoint g = myloc.getMyLocation();
			if (g != null) {
				mapView.getController().animateTo(g);
				mapView.postInvalidate();
			}
			return true;
		case R.id.marker:
			// Refresh the items on the map
			//new RefreshResTask().execute();
			if (manualRefresh) {
				mHandler.removeCallbacks(mUpdateTimeTask);
				mHandler.postDelayed(mUpdateTimeTask, 100);
			}
			return true;

		case R.id.layers:
			mHandler.removeCallbacks(mUpdateTimeTask);
			showDialog(DIALOG_LAYERS);
			return true;
//		case R.id.store:
//			GeoPoint g2 = myloc.getMyLocation();
//			if (g2 != null) {
//				ParseGeoPoint point = new ParseGeoPoint(g2.getLatitudeE6()/1E6, g2.getLongitudeE6()/1E6);
//				ParseObject placeObject = new ParseObject("PlaceObject");
//				placeObject.put("location", point);
//				placeObject.put("username", steamdb.getUsername());
//				placeObject.saveInBackground(new SaveCallback() {
//					  public void done(ParseException e) {
//							storeLookup();						  }
//						});
//				steamdb.statIncrement(CogmalitySQLHelper.STAT_STORE);
//				ContentValues values = new ContentValues();
//				values.put(CogmalitySQLHelper.STAT_STORELAT, "" + g2.getLatitudeE6()/1E6);
//				values.put(CogmalitySQLHelper.STAT_STORELON, "" + g2.getLongitudeE6()/1E6);
//				SQLiteDatabase db = steamdb.getReadableDatabase();				
//				db.update(CogmalitySQLHelper.STATTABLE, values, null, null);
//				db.close();
//
//			}
//			return true;
		default:
			return false;
		}
	}

	/** 
	 * Wish there was a better way to do this with Fragment Dialogs like everything else
	 * but the MapActivity and Fragments don't get along yet??? booo
	 */
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch(id) {
		case DIALOG_LAYERS:
			ArrayList<CharSequence> names = new ArrayList<CharSequence>();
			ArrayList<CharSequence> fullnames = new ArrayList<CharSequence>();
			ArrayList<Boolean> visible = new ArrayList<Boolean>();
			steamdb.getBasicFullNames(fullnames, names, visible);		

			final CharSequence[] items = fullnames.toArray(new CharSequence[names.size()]);
			final CharSequence[] references = names.toArray(new CharSequence[names.size()]);
			final boolean[] checks = toPrimitiveArray(visible);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Visible Resources");
			builder.setCancelable(false);
			builder.setMultiChoiceItems(items, checks, new OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int item, boolean arg2) {
					//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
					steamdb.setVisibility("" + references[item], arg2);
				}
			});
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					updateViews();
					mHandler.postDelayed(mUpdateTimeTask, 100);
				}
			});
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	/** 
	 * Needed to convert a Boolean[] into a boolean[]
	 * @param booleanList
	 * @return
	 */
	private boolean[] toPrimitiveArray(final List<Boolean> booleanList) {
		final boolean[] primitives = new boolean[booleanList.size()];
		int index = 0;
		for (Boolean object : booleanList) {
			primitives[index++] = object;
		}
		return primitives;
	}

	/**
	 * Create the menu for the Map
	 */
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.find_me).setTitle(R.string.find_me);
		menu.findItem(R.id.find_me).setIcon(R.drawable.ic_menu_mylocation);
		menu.findItem(R.id.marker).setTitle(R.string.marker);
		menu.findItem(R.id.marker).setIcon(R.drawable.ic_menu_refresh);
		menu.findItem(R.id.layers).setTitle(R.string.layers);
		menu.findItem(R.id.layers).setIcon(R.drawable.ic_menu_layers);
		if (steamdb.getStat(CogmalitySQLHelper.STAT_STORE) == 1) {
			menu.findItem(R.id.store).setTitle(R.string.store);
			menu.findItem(R.id.store).setIcon(R.drawable.store);
		} else {
			menu.removeItem(R.id.store);
		}
		return super.onPrepareOptionsMenu(menu);
	}


}
