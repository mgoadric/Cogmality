package edu.centenary.cogmality;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.parse.ParsePush;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/** Helper to the database, manages versions and creation */
public class CogmalitySQLHelper extends SQLiteOpenHelper {

	// Constants for the whole app
	public static String PACKAGE = "edu.centenary.cogmality";
	public static int[] PROBS = {8, 2, 4, 3, 3, 1, 8, 3, 4, 2, 1};  // Move to Item Table
	public static int[] PROBSSIMPLE = {8, 2, 4, 0, 0, 0, 8, 3, 0, 2, 0};  // Move to Item Table
	public static final int FIRST_LOOKUP = 4000;
	public static final int PICKUP_RANGE = 200; //200
	public static final int LOOKUP_RANGE = 500;
	public static final int ACCURACY = 8000;
	public static final int RESTIME = 180000;
	public static final int START_MONEY = 0;
	public static final int WOOD_TYPE = 0;
	public static final int QUALITY_DEFAULT = 2;
	public static final int CRAFT_SIZE = 4;
	public static final int STORE_SIZE = 10;
	public static final int OFFER_SIZE = 5;
	public static final int INV_SIZE = 30;
	public static final int OFFER_NOTIFICATION = 1;
	public static final int ANSWER_NOTIFICATION = 2;
	public static final String PLACES_API_KEY = "AIzaSyCjFlpcwTnB92B6jr26oGkWJdd5ctIEA7E";

	private static final String DATABASE_NAME = "Cogmality.db";
	private static final int DATABASE_VERSION = 1;

	// Table names
	public static final String INVTABLE = "inventory";
	public static final String STATTABLE = "stats";
	public static final String RESOURCETABLE = "resources";
	public static final String PLACESTABLE = "placeswaypoints";
	public static final String ITEMTABLE = "item";
	public static final String CRAFTTABLE = "craft";
	public static final String CRAFTTOOLTABLE = "crafttool";
	public static final String RECIPETABLE = "recipe";
	public static final String SKILLTABLE = "itemtypes";
	public static final String ACHTABLE = "achievements";
	public static final String JOURNALTABLE = "journaltimeline";
	public static final String OTHERSTORETABLE = "otherstores";
	public static final String MAILTABLE = "mailmessages";

	// Columns
	public static final String INV_ID = "item_id";
	public static final String INV_QUALITY = "quality";
	public static final String INV_AMOUNT = "amount";

	public static final String STAT_NAME = "name";
	public static final String STAT_PASS = "password";
	public static final String STAT_GATHERER = "gatherer";
	public static final String STAT_MONEY = "money";
	public static final String STAT_REFRESH = "refresh";
	public static final String STAT_VISION = "vision";
	public static final String STAT_TIMEEXP = "gametime";
	public static final String STAT_LASTRES = "lastres";
	public static final String STAT_LASTLAT = "lastlat";
	public static final String STAT_LASTLON = "lastlon";
	public static final String STAT_STORE = "store";
	public static final String STAT_STORELAT = "storelat";
	public static final String STAT_STORELON = "storelon";
	public static final String STAT_TUTORIAL = "tutorial";
	public static final String STAT_UPDATE = "version";

	public static final String CRAFT_ID = "item_id";
	public static final String CRAFT_QUALITY = "quality";
	public static final String CRAFT_AMOUNT = "amount";

	public static final String ACH_LABEL = "label";
	public static final String ACH_TEXT = "fulltext";
	public static final String ACH_UNLOCKED = "unlocked";

	public static final String REC_KEY = "key";
	public static final String REC_OUTPUT_ID = "output_id";
	public static final String REC_AMOUNT = "amount";
	public static final String REC_TOOL = "tool";

	public static final String MAIL_FROM = "userfrom";
	public static final String MAIL_ITEM = "parseitem_id";
	public static final String MAIL_ITEMTYPE = "itemtype";
	public static final String MAIL_ITEMFULLNAME = "fullname";
	public static final String MAIL_TYPE = "mailtype";
	public static final String MAIL_OFFER = "maildata";
	public static final String MAIL_MESSAGE = "mailmessage";	
	public static final String MAIL_STATUS = "status";	
	public static final String MAIL_TIME = "mailtime";	

	public static final String SKILL_NAME = "skill";
	public static final String SKILL_POINTS = "active";

	public static final String CRAFTTOOL_NAME = "craftTool";
	public static final String CRAFTTOOL_TYPE = "craftToolType";

	public static final String RES_ID = "item_id";
	public static final String RES_LAT = "latitude";
	public static final String RES_LON = "longitude";
	public static final String RES_AMOUNT = "amount";
	public static final String RES_TIME = "updatetime";
	public static final String RES_GOOGLEID = "googleid";
	public static final String RES_ACTIVE = "isactive";
	public static final String RES_PLACE = "place_id";

	public static final String PLACES_LAT = "latitude";
	public static final String PLACES_LON = "longitude";
	public static final String PLACES_TIME = "updatetime";

	public static final String OTHERSTORE_LAT = "latitude";
	public static final String OTHERSTORE_LON = "longitude";
	public static final String OTHERSTORE_NAME = "username";

	public static final String JOURNAL_IMAGE = "image";
	public static final String JOURNAL_TEXT = "mytext";
	public static final String JOURNAL_TIME = "updatetime";
	public static final String JOURNAL_STYLE = "style";

	public static final String ITEM_ID = "item_id";
	public static final String ITEM_NAME = "name";
	public static final String ITEM_FULLNAME = "fullname";
	public static final String ITEM_TYPE = "type";
	public static final String ITEM_SKILL = "skill";
	public static final String ITEM_SELLPRICE = "sellprice";
	public static final String ITEM_BUYPRICE = "buyprice";
	public static final String ITEM_DISCOVERED = "discovered";
	public static final String ITEM_LEVEL = "level";
	public static final String ITEM_TOOL = "tooltype";
	public static final String ITEM_COUNT = "count";
	public static final String ITEM_VISIBLE = "visible";
	public static final String ITEM_PROB = "probability";
	public static final String ITEM_PICKUP = "pickup";
	public static final String ITEM_DESC = "description";

	public CogmalitySQLHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + INVTABLE + "( " + BaseColumns._ID
		+ " integer primary key autoincrement, "
		+ INV_ID + " integer,"
		+ INV_QUALITY + " integer,"
		+ INV_AMOUNT + " integer);";
		Log.d("SQLdb", "onCreate: " + sql);
		db.execSQL(sql);

		sql = "create table " + CRAFTTABLE + "( " + BaseColumns._ID
		+ " integer primary key autoincrement, "
		+ CRAFT_ID + " integer,"
		+ CRAFT_QUALITY + " integer);";
		Log.d("SQLdb", "onCreate: " + sql);
		db.execSQL(sql);

		sql = "create table " + MAILTABLE + "( " + BaseColumns._ID
		+ " integer primary key autoincrement, "
		+ MAIL_FROM + " string,"
		+ MAIL_ITEM + " string,"
		+ MAIL_ITEMTYPE + " string,"
		+ MAIL_ITEMFULLNAME + " string,"
		+ MAIL_TYPE + " string,"
		+ MAIL_TIME + " integer,"
		+ MAIL_OFFER + " string,"
		+ MAIL_MESSAGE + " string,"
		+ MAIL_STATUS + " string);";
		Log.d("SQLdb", "onCreate: " + sql);
		db.execSQL(sql);

		sql = "create table " + ACHTABLE + "( " + BaseColumns._ID
		+ " integer primary key autoincrement, "
		+ ACH_LABEL + " text,"
		+ ACH_TEXT + " text,"
		+ ACH_UNLOCKED + " integer);";
		Log.d("SQLdb", "onCreate: " + sql);
		db.execSQL(sql);

		sql = "create table " + STATTABLE + "( " + BaseColumns._ID
		+ " integer primary key autoincrement, "

		+ STAT_NAME + " text,"
		+ STAT_PASS + " text,"
		+ STAT_GATHERER + " integer,"
		+ STAT_MONEY + " integer,"
		+ STAT_STORE + " integer,"
		+ STAT_TUTORIAL + " integer,"
		+ STAT_UPDATE + " integer,"
		+ STAT_LASTRES + " text,"
		+ STAT_LASTLAT + " real,"
		+ STAT_LASTLON + " real,"
		+ STAT_STORELAT + " real,"
		+ STAT_STORELON + " real,"
		+ STAT_TIMEEXP + " integer);";
		Log.d("SQLdb", "onCreate: " + sql);
		db.execSQL(sql);

		sql = "create table " + RESOURCETABLE + "( " + BaseColumns._ID
		+ " integer primary key autoincrement, "
		+ RES_ID + " integer,"
		+ RES_LAT + " real,"
		+ RES_LON + " real,"
		+ RES_TIME + " integer,"
		+ RES_GOOGLEID + " text,"
		+ RES_AMOUNT + " integer,"
		+ RES_ACTIVE + " integer,"
		+ RES_PLACE + " integer);";
		Log.d("SQLdb", "onCreate: " + sql);
		db.execSQL(sql);

		sql = "create table " + PLACESTABLE + "( " + BaseColumns._ID
		+ " integer primary key autoincrement, "
		+ PLACES_LAT + " real,"
		+ PLACES_LON + " real,"
		+ PLACES_TIME + " integer);";
		Log.d("SQLdb", "onCreate: " + sql);
		db.execSQL(sql);

		sql = "create table " + OTHERSTORETABLE + "( " + BaseColumns._ID
		+ " integer primary key autoincrement, "
		+ OTHERSTORE_LAT + " real,"
		+ OTHERSTORE_LON + " real,"
		+ OTHERSTORE_NAME + " string);";
		Log.d("SQLdb", "onCreate: " + sql);
		db.execSQL(sql);

		sql = "create table " + JOURNALTABLE + "( " + BaseColumns._ID
		+ " integer primary key autoincrement, "
		+ JOURNAL_IMAGE+ " real,"
		+ JOURNAL_TEXT + " real,"
		+ JOURNAL_STYLE + " text,"
		+ JOURNAL_TIME + " integer);";
		Log.d("SQLdb", "onCreate: " + sql);
		db.execSQL(sql);

		sql = "create table " + RECIPETABLE + "( " + BaseColumns._ID
		+ " integer primary key autoincrement, "
		+ REC_KEY + " text,"
		+ REC_AMOUNT + " integer,"
		+ REC_TOOL + " text,"
		+ REC_OUTPUT_ID + " text);";
		Log.d("SQLdb", "onCreate: " + sql);
		db.execSQL(sql);

		sql = "create table " + SKILLTABLE + "( " + BaseColumns._ID
		+ " integer primary key autoincrement, "
		+ SKILL_NAME + " text,"
		+ SKILL_POINTS + " integer);";
		Log.d("SQLdb", "onCreate: " + sql);
		db.execSQL(sql);

		sql = "create table " + CRAFTTOOLTABLE + "( " + BaseColumns._ID
		+ " integer primary key autoincrement, "
		+ CRAFTTOOL_TYPE + " text,"
		+ CRAFTTOOL_NAME + " text);";
		Log.d("SQLdb", "onCreate: " + sql);
		db.execSQL(sql);

		sql = "create table " + ITEMTABLE + "( " + BaseColumns._ID		
		+ " integer primary key autoincrement, "
		+ ITEM_ID + " integer,"
		+ ITEM_NAME + " text,"
		+ ITEM_DESC + " text,"
		+ ITEM_FULLNAME + " text,"
		+ ITEM_TYPE + " text,"
		+ ITEM_DISCOVERED + " integer,"
		+ ITEM_COUNT + " integer,"
		+ ITEM_TOOL + " text,"
		+ ITEM_LEVEL + " integer,"
		+ ITEM_SKILL + " text,"
		+ ITEM_SELLPRICE + " integer,"
		+ ITEM_PICKUP + " integer,"
		+ ITEM_VISIBLE + " integer,"
		+ ITEM_PROB + " integer,"
		+ ITEM_BUYPRICE + " integer);";
		Log.d("SQLdb", "onCreate: " + sql);
		db.execSQL(sql);
	}

	/**
	 * LOADING UP THE FILES 
	 */

	public Void addItems(Activity a) {
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(ITEMTABLE, null, null);
		// Should load from website for updates
		try {
			
			
			AssetManager am = a.getAssets();
			InputStream is = am.open("items.csv");
			InputStreamReader in= new InputStreamReader(is);
			BufferedReader bin= new BufferedReader(in);
			Log.d("loadItems: ", "opened file");
			while (bin.ready()) {
				ContentValues values = new ContentValues();
				String stuff = bin.readLine();
				//Log.d("loadItems: ", stuff);
				StringTokenizer line = new StringTokenizer(stuff, ",");
				int what = Integer.parseInt(line.nextToken());
				values.put(ITEM_ID, "" + what);
				values.put(ITEM_NAME, "" + line.nextToken());
				values.put(ITEM_FULLNAME, "" + line.nextToken());
				values.put(ITEM_TYPE, "" + line.nextToken());
				values.put(ITEM_SKILL, "" + line.nextToken());
				values.put(ITEM_TOOL, "" + line.nextToken());
				String disc = line.nextToken();
				values.put(ITEM_DISCOVERED, "" + disc);
				values.put(ITEM_LEVEL, "" + line.nextToken());
				values.put(ITEM_BUYPRICE, "" + line.nextToken());
				values.put(ITEM_SELLPRICE, "" + line.nextToken());
				values.put(ITEM_PICKUP, "" + line.nextToken());
				values.put(ITEM_VISIBLE, "" + line.nextToken());
				values.put(ITEM_PROB, "" + line.nextToken());
				values.put(ITEM_DESC, "" + line.nextToken());
				db.insertOrThrow(ITEMTABLE, null, values);
				// For testing, add 500 of each item.
				if (disc.equals("1")) {
					addInv(what, 2, 150);
					db = getReadableDatabase();
				}
			}
			bin = null;
			is.close();
			is = null;
		} catch (IOException e) {
			Log.d("loadNodes: ", "Problem with items.csv!\n");
		}
		db.close();
		return null;

	}

	public Void addRecipes(Activity a) {
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(RECIPETABLE, null, null);
		// Should load from website for updates
		try {


			AssetManager am = a.getAssets();
			InputStream is = am.open("recipes.csv");
			InputStreamReader in= new InputStreamReader(is);
			BufferedReader bin= new BufferedReader(in);
			Log.d("loadRecs: ", "opened file");
			while (bin.ready()) {
				ContentValues values = new ContentValues();
				String stuff = bin.readLine();
				//Log.d("loadrecs: ", stuff);
				StringTokenizer line = new StringTokenizer(stuff, ",");
				values.put(REC_OUTPUT_ID, "" + line.nextToken());
				values.put(REC_AMOUNT, "" + line.nextToken());
				values.put(REC_TOOL, "" + line.nextToken());
				String key = "";
				while (line.hasMoreTokens()) {
					key += line.nextToken() + ",";
				}
				key = key.substring(0, key.length() - 1);
				//Log.d("loadrecs: ", key);
				values.put(REC_KEY, "" + key);
				db.insertOrThrow(RECIPETABLE, null, values);
			}
			bin = null;
			is.close();
			is = null;
		} catch (IOException e) {
			Log.d("loadNodes: ", "Problem with recipes.csv!\n");
		}
		db.close();
		return null;
	}

	public Void addAchievements(Activity a) {
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(ACHTABLE, null, null);
		// Should load from website for updates
		try {


			AssetManager am = a.getAssets();
			InputStream is = am.open("achievements.csv");
			InputStreamReader in= new InputStreamReader(is);
			BufferedReader bin= new BufferedReader(in);
			Log.d("loadACHs: ", "opened file");
			while (bin.ready()) {
				ContentValues values = new ContentValues();
				String stuff = bin.readLine();
				//Log.d("loadrecs: ", stuff);
				StringTokenizer line = new StringTokenizer(stuff, ",");
				values.put(ACH_UNLOCKED, "" + line.nextToken());
				values.put(ACH_LABEL, "" + line.nextToken());
				values.put(ACH_TEXT, "" + line.nextToken());
				db.insertOrThrow(ACHTABLE, null, values);
			}
			bin = null;
			is.close();
			is = null;
		} catch (IOException e) {
			Log.d("loadNodes: ", "Problem with achievements!\n");
		}
		db.close();
		return null;
	}

	public Void addSkills(Activity a) {
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(SKILLTABLE, null, null);
		// Should load from website for updates
		try {


			AssetManager am = a.getAssets();
			InputStream is = am.open("itemtypes.csv");
			InputStreamReader in= new InputStreamReader(is);
			BufferedReader bin= new BufferedReader(in);
			Log.d("loadSKILLs: ", "opened file");
			while (bin.ready()) {
				ContentValues values = new ContentValues();
				String stuff = bin.readLine();
				//Log.d("loadrecs: ", stuff);
				StringTokenizer line = new StringTokenizer(stuff, ",");
				values.put(SKILL_NAME, "" + line.nextToken());
				values.put(SKILL_POINTS, "" + 0);
				db.insertOrThrow(SKILLTABLE, null, values);
			}
			bin = null;
			is.close();
			is = null;
		} catch (IOException e) {
			Log.d("loadNodes: ", "Problem with skills!\n");
		}
		db.close();
		return null;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion >= newVersion)
			return;

		String sql = null;
		if (oldVersion == 2)
			sql = "";

		Log.d("EventsData", "onUpgrade	: " + sql);
		if (sql != null)
			db.execSQL(sql);
	}

	/**
	 * Creates a new user in the database. Should really go to remote system and try to 
	 * register the user.
	 */
	public int newUser(String uname, String pword, String email) {

		SQLiteDatabase db = this.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put(CogmalitySQLHelper.STAT_NAME, "" + uname);
		values.put(CogmalitySQLHelper.STAT_STORE, "" + 0);
		values.put(CogmalitySQLHelper.STAT_PASS, "" + pword);
		values.put(CogmalitySQLHelper.STAT_MONEY, "" + CogmalitySQLHelper.START_MONEY);
		db.insertOrThrow(CogmalitySQLHelper.STATTABLE, null, values);
		values = new ContentValues();
		values.put(CogmalitySQLHelper.CRAFTTOOL_NAME, "hand");
		values.put(CogmalitySQLHelper.CRAFTTOOL_TYPE, "craft");
		db.insertOrThrow(CogmalitySQLHelper.CRAFTTOOLTABLE, null, values);
		values = new ContentValues();
		values.put(CogmalitySQLHelper.CRAFTTOOL_NAME, "lefthand");
		values.put(CogmalitySQLHelper.CRAFTTOOL_TYPE, "pickup");
		db.insertOrThrow(CogmalitySQLHelper.CRAFTTOOLTABLE, null, values);
		values = new ContentValues();
		values.put(CogmalitySQLHelper.JOURNAL_STYLE, "basic");
		values.put(CogmalitySQLHelper.JOURNAL_TIME, System.currentTimeMillis() - 2 * 86400000);
		values.put(CogmalitySQLHelper.JOURNAL_IMAGE, "airship");
		
		values.put(CogmalitySQLHelper.JOURNAL_TEXT, "First day on the job.\n\nLessons Learned:\n\nThere is no going back.\n\nSee further, look sharper, fly higher." + 
													"\n\nRead \"The Knowledge\" by Dartnell.\n\nShould be a fun place to work.");
		db.insertOrThrow(CogmalitySQLHelper.JOURNALTABLE, null, values);
		values = new ContentValues();
		values.put(CogmalitySQLHelper.JOURNAL_STYLE, "basic");
		values.put(CogmalitySQLHelper.JOURNAL_TIME, System.currentTimeMillis());
		values.put(CogmalitySQLHelper.JOURNAL_IMAGE, "location_map_large");
		
		values.put(CogmalitySQLHelper.JOURNAL_TEXT, "It happend last night. It wasn't supposed to be ready " +
				"yet.. I'm still confused as to how it worked; I have never felt so sick in my life.\n\nIt wasn't supposed to be me, I was " +
				"not trained, I haven't studied and read the right books, I just started yesterday. Unlike her, I know almost nothing of how the world really works and how things are made.\n\n" +
				"But I can't change it, and I can't go back; if nothing else, that was very clear when I started. What I have will make do as a tent for now, and " +
				"I'll start exploring tomorrow to see what I can find, maybe I make it all the same. " +
				"And while I'm glad I dragged this journal and pen with me, I fear if I write more about who I really am, it could fall into the wrong hands.\n\nI'm not supposed to be here.");
		db.insertOrThrow(CogmalitySQLHelper.JOURNALTABLE, null, values);
		db.close();
		return LoginActivity.SUCCESS;
	}

	/**
	 * Delete the database and refresh everything to empty tables. Only used for testing
	 */
	public void wipe() {
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(CogmalitySQLHelper.INVTABLE, null, null);
		db.delete(CogmalitySQLHelper.RESOURCETABLE, null, null);
		db.delete(CogmalitySQLHelper.PLACESTABLE, null, null);
		db.delete(CogmalitySQLHelper.CRAFTTABLE, null, null);
		db.delete(CogmalitySQLHelper.CRAFTTOOLTABLE, null, null);
		db.delete(CogmalitySQLHelper.RECIPETABLE, null, null);
		db.delete(CogmalitySQLHelper.ITEMTABLE, null, null);
		db.delete(CogmalitySQLHelper.SKILLTABLE, null, null);
		db.delete(CogmalitySQLHelper.STATTABLE, null, null);
		db.delete(CogmalitySQLHelper.JOURNALTABLE, null, null);
		db.delete(CogmalitySQLHelper.OTHERSTORETABLE, null, null);
		db.delete(CogmalitySQLHelper.MAILTABLE, null, null);
		db.close();
	}

	/**
	 * Looks up the resource image by name. Works for images included in the app resource directory,
	 * otherwise returns a default image.
	 * TODO How to handle images download from the server once new items are added?
	 */
	public static int getResByName(Context c, String name) {
		int resID = c.getResources().getIdentifier(name, "drawable", PACKAGE);
		//Log.d("InvGrid", "name = " + name + " resID = " + resID);
		if (resID != 0) {
			return resID;
		} else {
			return R.drawable.ic_launcher;
		}
	}


	public void journalEntry(String image, String text) {
		ContentValues values = new ContentValues();
		if (itemDiscovered("watch")) {
			values.put(CogmalitySQLHelper.JOURNAL_STYLE, "watch");
		} else if (itemDiscovered("hourglass")) {
			values.put(CogmalitySQLHelper.JOURNAL_STYLE, "hourglass");			
		} else {
			values.put(CogmalitySQLHelper.JOURNAL_STYLE, "simple");
		}
		values.put(CogmalitySQLHelper.JOURNAL_TIME, System.currentTimeMillis());
		values.put(CogmalitySQLHelper.JOURNAL_IMAGE, image);
		text = text.replace("|", ",");
		text = text.replace("PP", "\n\n");
		values.put(CogmalitySQLHelper.JOURNAL_TEXT, text);
		SQLiteDatabase db = this.getReadableDatabase();
		db.insertOrThrow(CogmalitySQLHelper.JOURNALTABLE, null, values);
		db.close();
	}

	public void mailReceived(Context c, String jsontext, String type) {
		try {

			ContentValues values = new ContentValues();
			values.put(CogmalitySQLHelper.MAIL_TIME, System.currentTimeMillis());
			values.put(CogmalitySQLHelper.MAIL_STATUS, "UNREAD");
			JSONObject json = new JSONObject(jsontext);
			if (type.equals("OFFER")) {
				values.put(CogmalitySQLHelper.MAIL_TYPE, type);
			} else if (type.equals("REJECT") || type.equals("ACCEPT")) {
				StringTokenizer pickup = new StringTokenizer(json.getString("maildata"), "|");
				while (pickup.hasMoreTokens()) {
					addInvName(c, pickup.nextToken(), 2, 1);
					Log.d("Answer", "adding trade item to inventory");
				}
			}
			Iterator itr = json.keys();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				Log.d("CogSQL", "..." + key + " => " + json.getString(key));
				if (!key.equals("action")) {
					values.put(key, json.getString(key));
				}
			}
			SQLiteDatabase db = this.getReadableDatabase();
			db.insertOrThrow(CogmalitySQLHelper.MAILTABLE, null, values);
			db.close();
		} catch (JSONException e) {
			Log.d("CogSQL", "JSONException: " + e.getMessage());
		}
	}

	public int labeledBasic(JSONArray ary) {
		int type = -1;
		try {
			for(int j = 0; j < ary.length() ; j++){
				if (ary.getString(j).equals("electrician") ||
						ary.getString(j).equals("furniture_store") ||
						ary.getString(j).equals("general_contractor") ||
						ary.getString(j).equals("hardware_store") ||
						ary.getString(j).equals("home_goods_store") ||
						ary.getString(j).equals("insurance_agency") ||
						ary.getString(j).equals("lawyer") ||
						ary.getString(j).equals("locksmith") ||
						ary.getString(j).equals("moving_company") ||
						ary.getString(j).equals("painter") ||
						ary.getString(j).equals("plumber") ||
						ary.getString(j).equals("real_estate_agency") ||
						ary.getString(j).equals("storage") ||
						ary.getString(j).equals("roofing_contractor")) {
					type = 2;
					break;// stone
				} else if (ary.getString(j).equals("cafe") ||
						ary.getString(j).equals("bakery") ||
						ary.getString(j).equals("bar") ||
						ary.getString(j).equals("food") ||
						ary.getString(j).equals("grocery_or_supermarket") ||
						ary.getString(j).equals("liquor_store") ||
						ary.getString(j).equals("meal_delivery") ||
						ary.getString(j).equals("meal_takeaway") ||
						ary.getString(j).equals("restaurant")) {
					type = 8; // coal
					break;
				} else if (ary.getString(j).equals("amusement_park") ||
						ary.getString(j).equals("aquarium") ||
						ary.getString(j).equals("art_gallery") ||
						ary.getString(j).equals("bowling_alley") ||
						ary.getString(j).equals("casino") ||
						ary.getString(j).equals("movie_rental") ||
						ary.getString(j).equals("movie_theater") ||
						ary.getString(j).equals("night_club") ||
						ary.getString(j).equals("zoo") ||
						ary.getString(j).equals("stadium")) {
					type = 4; // iron
					break;
				} else if (ary.getString(j).equals("dentist") ||
						ary.getString(j).equals("doctor") ||
						ary.getString(j).equals("gym") ||
						ary.getString(j).equals("health") ||
						ary.getString(j).equals("hospital") ||
						ary.getString(j).equals("pharmacy") ||
						ary.getString(j).equals("physiotherapist") ||
						ary.getString(j).equals("spa") ||
						ary.getString(j).equals("veterinary_care")) {
					type = 10; // zinc
					break;
				} else if (ary.getString(j).equals("book_store") ||
						ary.getString(j).equals("library") ||
						ary.getString(j).equals("museum") ||
						ary.getString(j).equals("school") ||
						ary.getString(j).equals("university")) {
					type = 3; // copper ore
					break;
				} else if (ary.getString(j).equals("accounting") ||
						ary.getString(j).equals("atm") ||
						ary.getString(j).equals("bank") ||
						ary.getString(j).equals("finance")) {
					type = 5; // gold ore
					break;
				} else if (ary.getString(j).equals("city_hall") ||
						ary.getString(j).equals("courthouse") ||
						ary.getString(j).equals("fire_station") ||
						ary.getString(j).equals("local_government_office") ||
						ary.getString(j).equals("police") ||
						ary.getString(j).equals("post_office") ||
						ary.getString(j).equals("park") ||
						ary.getString(j).equals("parking")) {
					type = 9; // clay
					break;
				} else if (ary.getString(j).equals("airport") ||
						ary.getString(j).equals("bus_station") ||
						ary.getString(j).equals("campground") ||
						ary.getString(j).equals("car_dealer") ||
						ary.getString(j).equals("car_rental") ||
						ary.getString(j).equals("car_repair") ||
						ary.getString(j).equals("car_wash") ||
						ary.getString(j).equals("embassy") ||
						ary.getString(j).equals("gas_station") ||
						ary.getString(j).equals("lodging") ||
						ary.getString(j).equals("rv_park") ||
						ary.getString(j).equals("subway_station") ||
						ary.getString(j).equals("taxi_stand") ||
						ary.getString(j).equals("travel_agency")) {
					type = 7; // sand
					break;
				} else if (ary.getString(j).equals("beauty_salon") ||
						ary.getString(j).equals("bicycle_store") ||
						ary.getString(j).equals("convenience_store") ||
						ary.getString(j).equals("department_store") ||
						ary.getString(j).equals("electronics_store") ||
						ary.getString(j).equals("florist") ||
						ary.getString(j).equals("hair_care") ||
						ary.getString(j).equals("jewelry_store") ||
						ary.getString(j).equals("laundry") ||
						ary.getString(j).equals("pet_store") ||
						ary.getString(j).equals("shoe_store") ||
						ary.getString(j).equals("shopping_mall") ||
						ary.getString(j).equals("clothing_store")) {
					type = 6; // wool
					break;
				} else {
					type = 0;
				}
			}
		} catch (JSONException je) {
			
		}
		return type;
	}
	
	public int randomBasic() {
		int total = 0;
		int[] whichProb = PROBS;
		if (currentLevel() < 5) {
			whichProb = PROBSSIMPLE;
		}
		for (Integer p: whichProb) {
			total += p;
		}
		double r = Math.random();
		double c = 0;
		for (int i = 0; i < whichProb.length; i++) {
			c += whichProb[i] / (float)total;
			if (r < c) {
				return i;
			}
		}
		return 0;
	}

	public boolean ready() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(STATTABLE, null, null, null,
				null, null, null);
		boolean r = cursor.moveToNext();
		cursor.close();
		db.close();
		return r;
	}

	public int getMoney() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(STATTABLE, null, null, null,
				null, null, null);
		int mon = 0;
		if (cursor.moveToNext()) {
			mon = cursor.getInt(cursor.getColumnIndex(STAT_MONEY));
		} 
		cursor.close();
		db.close();
		return mon;
	}

	public String getUsername() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(STATTABLE, null, null, null,
				null, null, null);
		String username = "";
		if (cursor.moveToNext()) {
			username = cursor.getString(cursor.getColumnIndex(STAT_NAME));
		} 
		cursor.close();
		db.close();
		return username;
	}

	public boolean itemDiscovered(String name) {
		SQLiteDatabase db = this.getReadableDatabase();
		boolean found = false;
		Cursor cursor = db.query(CogmalitySQLHelper.ITEMTABLE, null, 
				CogmalitySQLHelper.ITEM_DISCOVERED + "=1 AND " + 
				CogmalitySQLHelper.ITEM_NAME + "='" + name + "'", null,
				null, null, null);
		if (cursor.moveToNext()) {
			found = true;
		} 
		cursor.close();
		db.close();
		return found;
	}

	public long getLastResTime() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(STATTABLE, null, null, null,
				null, null, null);
		long time = 0;
		if (cursor.moveToNext()) {
			String s = cursor.getString(cursor.getColumnIndex(STAT_LASTRES));
			if (s != null) {
				time = Long.parseLong(s);
			}
		} 
		cursor.close();
		db.close();
		return time;
	}

	public double getLastLat() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(STATTABLE, null, null, null,
				null, null, null);
		double lat = 0;
		if (cursor.moveToNext()) {
			lat = cursor.getDouble(cursor.getColumnIndex(STAT_LASTLAT));
		} 
		cursor.close();
		db.close();
		return lat;
	}

	public double getLastLon() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(STATTABLE, null, null, null,
				null, null, null);
		double lon = 0;
		if (cursor.moveToNext()) {
			lon = cursor.getDouble(cursor.getColumnIndex(STAT_LASTLON));
		} 
		cursor.close();
		db.close();
		return lon;
	}

	public int getStat(String what) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(STATTABLE, null, null, null,
				null, null, null);
		int ham = 0;
		if (cursor.moveToNext()) {
			ham = cursor.getInt(cursor.getColumnIndex(what));
		} 
		cursor.close();
		db.close();
		return ham;
	}

	public boolean getVisibility(String which) {
		SQLiteDatabase db = this.getReadableDatabase();
		String tot = "SELECT " + ITEM_VISIBLE + " FROM " + ITEMTABLE + 
		" WHERE " + ITEM_NAME + "=\"" + which + "\";";
		long total = (db.compileStatement(tot)).simpleQueryForLong();
		db.close();
		return 1 == total;
	}

	public boolean getVisibility(int which) {
		SQLiteDatabase db = this.getReadableDatabase();
		String tot = "SELECT " + ITEM_VISIBLE + " FROM " + ITEMTABLE + 
		" WHERE " + ITEM_ID + "=\"" + which + "\";";
		long total = (db.compileStatement(tot)).simpleQueryForLong();
		db.close();
		return 1 == total;
	}

	public void setVisibility(String which, boolean visible) {
		SQLiteDatabase db = this.getReadableDatabase();
		ContentValues values = new ContentValues();
		String where = ITEM_NAME + "=\"" + which + "\"";
		values.put(ITEM_VISIBLE, "" + (visible ? 1 : 0));
		db.update(ITEMTABLE, values, where, null);
		db.close();
	}

	public void getBasicFullNames(ArrayList<CharSequence> fullnames, ArrayList<CharSequence> names, ArrayList<Boolean> visible) {

		SQLiteDatabase db = this.getReadableDatabase();

		// Only get the basic items, not everything...
		Cursor cursor = db.query(CogmalitySQLHelper.ITEMTABLE, null, CogmalitySQLHelper.ITEM_LEVEL + " = 0", null,
				null, null, CogmalitySQLHelper.ITEM_FULLNAME);

		while (cursor.moveToNext()) {
			fullnames.add(cursor.getString(cursor.getColumnIndex(CogmalitySQLHelper.ITEM_FULLNAME)));
			names.add(cursor.getString(cursor.getColumnIndex(CogmalitySQLHelper.ITEM_NAME)));
			visible.add(1 == cursor.getInt(cursor.getColumnIndex(CogmalitySQLHelper.ITEM_VISIBLE)));
		}
		db.close();
	}

	public int getPickup(int type) {
		boolean good = true;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(ITEMTABLE, null, ITEM_ID + "=" + type, null,
				null, null, null);
		int level = 0;
		if (cursor.moveToNext()) {
			level = cursor.getInt(cursor.getColumnIndex(ITEM_PICKUP));
		} 
		cursor.close();
		db.close();
		return level;
	}

	public boolean possesion(String item) {
		SQLiteDatabase db = this.getReadableDatabase();
		String tot = "SELECT COUNT(*) FROM " + INVTABLE + " LEFT OUTER JOIN " + 
		ITEMTABLE + " ON (" +
		INVTABLE + "." + INV_ID + " = " +
		ITEMTABLE + "." + ITEM_ID + ")" +
		" WHERE " + ITEM_NAME + "=\"" + item + "\";";
		long total = (db.compileStatement(tot)).simpleQueryForLong();
		db.close();
		return total > 0;
	}

	public void statIncrement(String which) {
		ContentValues values = new ContentValues();
		int up = getStat(which) + 1;
		values.put(which, "" + up);
		SQLiteDatabase db = this.getReadableDatabase();
		db.update(STATTABLE, values, null, null);
		db.close();
	}

	/**
	 * Find the current skill points
	 */
	public int getSkill(String what) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(SKILLTABLE, null, SKILL_NAME + "=\"" + what + "\"", null,
				null, null, null);
		int ham = 0;
		if (cursor.moveToNext()) {
			ham = cursor.getInt(cursor.getColumnIndex(SKILL_POINTS));
		} 
		cursor.close();
		db.close();
		return ham;
	}

	public int currentSkillLevel(String which) {
		return (int)((1 + Math.pow(1 + (4 * (getSkill(which)/10.0)),0.5))/2);
	}

	/**
	 * Update skill points based on itemLevel and itemCount for an item
	 */
	public void skillUpdate(String which, int itemLevel, int itemCount) {
		ContentValues values = new ContentValues();
		int up = getSkill(which) + 1 + (int)(10 * itemLevel * currentSkillLevel(which) * Math.pow(0.5, itemCount));
		values.put(SKILL_POINTS, "" + up);
		SQLiteDatabase db = this.getReadableDatabase();
		db.update(SKILLTABLE, values, SKILL_NAME + "=\"" + which + "\"", null);
		db.close();
	}

	public int currentLevel() {
		SQLiteDatabase db = this.getReadableDatabase();
		String howmany = "SELECT SUM(" + SKILL_POINTS + ") FROM " + SKILLTABLE;
		long count = (db.compileStatement(howmany)).simpleQueryForLong();
		db.close();
		int total = (int)count +  
		getStat(STAT_GATHERER) / 2;
		return (int)(Math.log((2 / 25.0) * (total + 25)) / Math.log(2));
	}

	public String currentType() {
		int x = currentLevel();

		if (x >= 10) {
			return "Legend";
		} else if (x >= 8) {
			return "Master";
		} else if (x >= 5) {
			return "Journeyman";
		} else if (x >= 3) {
			return "Apprentice";
		} else if (x >= 1) {
			return "Novice";
		} else {
			return "WHAT?";
		}
	}

	public void unlockAchievement(String which) {
		ContentValues values = new ContentValues();
		values.put(ACH_UNLOCKED, "" + 1);
		SQLiteDatabase db = this.getReadableDatabase();
		db.update(ACHTABLE, values, ACH_LABEL + "=\"" + which + "\"", null);
		db.close();
	}

	public void itemIncrement(String which) {
		SQLiteDatabase db = this.getReadableDatabase();
		String tot = "SELECT " + ITEM_COUNT + " FROM " + ITEMTABLE + 
		" WHERE " + ITEM_NAME + "=\"" + which + "\";";
		String where = ITEM_NAME + "=\"" + which + "\"";
		long total = (db.compileStatement(tot)).simpleQueryForLong();
		ContentValues values = new ContentValues();
		values.put(ITEM_COUNT, (int)(total + 1));
		db.update(ITEMTABLE, values, where, null);
		db.close();
	}

	public void pickupIncrement(String which) {
		SQLiteDatabase db = this.getReadableDatabase();
		String tot = "SELECT " + ITEM_PICKUP + " FROM " + ITEMTABLE + 
		" WHERE " + ITEM_NAME + "=\"" + which + "\";";
		String where = ITEM_NAME + "=\"" + which + "\"";
		long total = (db.compileStatement(tot)).simpleQueryForLong();
		ContentValues values = new ContentValues();
		values.put(ITEM_PICKUP, (int)(total + 1));
		db.update(ITEMTABLE, values, where, null);
		db.close();
	}

	public void toolAddition(String which, String type) {
		SQLiteDatabase db = this.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put(CogmalitySQLHelper.CRAFTTOOL_NAME, "" + which);
		values.put(CogmalitySQLHelper.CRAFTTOOL_TYPE, "" + type);
		db.insertOrThrow(CogmalitySQLHelper.CRAFTTOOLTABLE, null, values);
		db.close();
	}

	public String getCompletion() {
		String comp = "";
		comp += completionCount() + " of " + completionTotal();
		return comp;
	}

	public int completionCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		String howmany = "SELECT COUNT(*) FROM " + ITEMTABLE + " WHERE " + 
		ITEM_DISCOVERED + "=1 AND " + 
		ITEM_LEVEL + ">=1;";
		long count = (db.compileStatement(howmany)).simpleQueryForLong();
		return (int)count;
	}

	public int mailCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		String howmany = "SELECT COUNT(*) FROM " + MAILTABLE;
		long count = (db.compileStatement(howmany)).simpleQueryForLong();
		return (int)count;
	}

	public int itemCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		String howmany = "SELECT SUM(" + ITEM_COUNT + ") FROM " + ITEMTABLE;
		long count = (db.compileStatement(howmany)).simpleQueryForLong();
		return (int)count;
	}

	public int completionTotal() {
		SQLiteDatabase db = this.getReadableDatabase();
		String howmany = "SELECT COUNT(*) FROM " + ITEMTABLE + " WHERE " + 
		ITEM_LEVEL + ">=1;";
		long count = (db.compileStatement(howmany)).simpleQueryForLong();
		return (int)count;
	}

	public void changeMoney(int delta) {
		ContentValues values = new ContentValues();
		values.put(STAT_MONEY, "" + (getMoney() + delta));
		SQLiteDatabase db = this.getReadableDatabase();
		db.update(STATTABLE, values, null, null);
		db.close();
	}

	public void addInv(int type, int quality, int amount) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] backtome = {INV_AMOUNT};
		String where = INV_ID + "=" + type + " AND " + 
		INV_QUALITY + "=" + quality;
		Cursor cursor = db.query(INVTABLE, backtome, 
				where, null,
				null, null, null);
		if (cursor.moveToNext()) {
			amount += cursor.getInt(cursor.getColumnIndex(INV_AMOUNT));
			ContentValues values = new ContentValues();
			values.put(INV_AMOUNT, "" + amount);
			db.update(INVTABLE, values, where, null);
		} else {
			ContentValues values = new ContentValues();
			values.put(INV_ID, "" + type);
			values.put(INV_QUALITY, "" + quality);
			values.put(INV_AMOUNT, "" + amount);
			db.insertOrThrow(INVTABLE, null, values);
		}		
		cursor.close();
		db.close();
	}

	public void addInvName(Context c, String what, int quality, int amount) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] backtome = {ITEM_ID, ITEM_TOOL};
		String where = ITEM_NAME + "=\"" + what + "\"";
		Cursor cursor = db.query(ITEMTABLE, backtome, 
				where, null,
				null, null, null);
		cursor.moveToNext();
		int type = cursor.getInt(cursor.getColumnIndex(ITEM_ID));
		String tool = cursor.getString(cursor.getColumnIndex(ITEM_TOOL));
		addInv(type, quality, amount);
		
		String howmany = "SELECT COUNT(*) FROM " + CRAFTTOOLTABLE + " WHERE " + 
		CRAFTTOOL_NAME + "=\"" + what + "\"";
		long count = (db.compileStatement(howmany)).simpleQueryForLong();

		// THIS IS WRONG!!!
		if (count == 0) {
			toolCheck(c, tool, type, what);
		}
	}

	public void takeInv(int type, int quality, int amount ){
		String where = INV_ID + "=" + type + " AND " + 
		INV_QUALITY + "=" + quality;
		SQLiteDatabase db = this.getReadableDatabase();
		if (amount > 1){
			ContentValues values = new ContentValues();
			values.put(INV_AMOUNT, "" + (amount-1));
			db.update(INVTABLE, values, where, null);
		}else{
			db.delete(INVTABLE, where, null);
		}
		db.close();
	}

	public void takeInv(String name){
		SQLiteDatabase db = this.getReadableDatabase();
		String howmany = "SELECT " + ITEM_ID + " FROM " + ITEMTABLE + " WHERE " + 
		ITEM_NAME + "=\"" + name + "\";";
		long id = (db.compileStatement(howmany)).simpleQueryForLong();
		howmany = "SELECT " + INV_AMOUNT + " FROM " + INVTABLE + " WHERE " + 
		INV_ID + "=" + id + ";";
		long amount = (db.compileStatement(howmany)).simpleQueryForLong();
		db.close();
		takeInv((int)id, QUALITY_DEFAULT, (int)amount);
	}

	/**
	 * All about craftings
	 */

	public boolean addCraft(Activity c, int type, int quality) {
		boolean worked = true;
		SQLiteDatabase db = this.getReadableDatabase();
		String howmany = "SELECT COUNT(*) FROM " + CRAFTTABLE + ";";
		long count = (db.compileStatement(howmany)).simpleQueryForLong();
		if (count < CRAFT_SIZE) {
			ContentValues values = new ContentValues();
			values.put(CRAFT_ID, "" + type);
			values.put(CRAFT_QUALITY, "" + quality);
			db.insertOrThrow(CRAFTTABLE, null, values);
		} else {
			Toast.makeText(c, "Table is full", Toast.LENGTH_SHORT).show();	
			worked = false;
		}
		db.close();		
		return worked;
	}

	public void takeCraft(int type, int quality){
		SQLiteDatabase db = this.getReadableDatabase();
		String where = CRAFT_ID + "=" + type + " AND " + 
		CRAFT_QUALITY + "=" + quality;			
		Cursor cursor = db.query(CRAFTTABLE, null, where, null,
				null, null, null);
		if (cursor.moveToNext()) {
			String where2 = BaseColumns._ID + "=" + cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));;			
			db.delete(CRAFTTABLE, where2, null);
		}
		cursor.close();
		db.close();
	}

	public String possibleCraft() {

		String tool = "none";
		// get key from craft table
		String key = "\"";
		SQLiteDatabase db = this.getReadableDatabase();
		String[] backtome = {ITEM_NAME};
		Cursor cursor = db.query(CRAFTTABLE + " LEFT OUTER JOIN " + 
				ITEMTABLE + " ON (" +
				CRAFTTABLE + "." + CRAFT_ID + " = " +
				ITEMTABLE + "." + ITEM_ID + 
				")", backtome, 
				null, null,
				null, null, ITEM_NAME);
		while (cursor.moveToNext()) {
			key += cursor.getString(cursor.getColumnIndex(ITEM_NAME)) + ",";
		}
		cursor.close();
		if (key.length() != 1) {
			key = key.substring(0, key.length() - 1);
		}
		key += "\"";

		Log.d("checking stuff", "key = " + key);

		if (key.length() != 2) {
			Log.d("checking stuff", "things are on the table");

			// look up key in recipe table
			String[] backtome2 = {REC_TOOL, ITEM_NAME, ITEM_DISCOVERED, CRAFTTOOL_NAME};
			String where = REC_KEY + "=" + key + " AND " + CRAFTTOOL_TYPE + "=\"craft\"";
			cursor = db.query("(" + RECIPETABLE + " LEFT OUTER JOIN " + 
					ITEMTABLE + " ON (" +
					RECIPETABLE + "." + REC_OUTPUT_ID + " = " +
					ITEMTABLE + "." + ITEM_NAME + 
					")) JOIN " + 
					CRAFTTOOLTABLE + " ON (" + REC_TOOL + " = " +
					CRAFTTOOLTABLE + "." + CRAFTTOOL_NAME + ")"
					, backtome2, 
					where, null,
					null, null, null);

			// if key found

			Log.d("Seeing stuff", "Query = " + "(" + RECIPETABLE + " LEFT OUTER JOIN " + 
					ITEMTABLE + " ON (" +
					RECIPETABLE + "." + REC_OUTPUT_ID + " = " +
					ITEMTABLE + "." + ITEM_NAME + 
					")) JOIN " + 
					CRAFTTOOLTABLE + " ON (" + REC_TOOL + " = " +
					CRAFTTOOLTABLE + "." + CRAFTTOOL_NAME + ")");
			if (cursor.moveToNext()) {
				Log.d("Seeing stuff", "tool =" + cursor.getString(cursor.getColumnIndex(REC_TOOL)) + " crafttool = " + 
						cursor.getString(cursor.getColumnIndex(CRAFTTOOL_NAME)));
				tool =  cursor.getString(cursor.getColumnIndex(REC_TOOL)) + "|" + 
				(cursor.getInt(cursor.getColumnIndex(ITEM_DISCOVERED)) == 1 ?
						cursor.getString(cursor.getColumnIndex(ITEM_NAME)) : "q");
			}
		}
		return tool;
	}

	public void makeCraft(FragmentActivity a, String tool) {
		// get key from craft table
		String key = "\"";
		SQLiteDatabase db = this.getReadableDatabase();
		String[] backtome = {ITEM_NAME};
		Cursor cursor = db.query(CRAFTTABLE + " LEFT OUTER JOIN " + 
				ITEMTABLE + " ON (" +
				CRAFTTABLE + "." + CRAFT_ID + " = " +
				ITEMTABLE + "." + ITEM_ID + 
				")", backtome, 
				null, null,
				null, null, ITEM_NAME);
		while (cursor.moveToNext()) {
			key += cursor.getString(cursor.getColumnIndex(ITEM_NAME)) + ",";
		}
		cursor.close();
		db.close();
		if (key.length() != 1) {
			key = key.substring(0, key.length() - 1);
		}
		key += "\"";

		Log.d("Makin stuff", "key = " + key);

		if (key.length() != 2) {
			Log.d("Makin stuff", "things are on the table");
			int itemTotal = itemCount();

			// look up key in recipe table
			String[] backtome2 = {ITEM_ID, 
					REC_AMOUNT,
					ITEM_LEVEL,
					ITEM_FULLNAME,
					ITEM_NAME,
					ITEM_TYPE,
					ITEM_SKILL,
					ITEM_COUNT,
					ITEM_TOOL,
					ITEM_ID,
					ITEM_DESC,
					REC_TOOL};
			String where = REC_KEY + "=" + key
			+ " AND " + REC_TOOL + "=\"" + tool + "\"";
			db = getReadableDatabase();
			cursor = db.query(RECIPETABLE + " LEFT OUTER JOIN " + 
					ITEMTABLE + " ON (" +
					RECIPETABLE + "." + REC_OUTPUT_ID + " = " +
					ITEMTABLE + "." + ITEM_NAME + 
					")", backtome2, 
					where, null,
					null, null, null);

			// if key found

			if (cursor.moveToNext()) {
				Log.d("Makin stuff", "tool =" + cursor.getString(cursor.getColumnIndex(REC_TOOL)));
				Log.d("Makin stuff", "Found a match!");

				// TODO Check to see if you ever made this before
				String what = cursor.getString(cursor.getColumnIndex(ITEM_NAME));
				String desc = cursor.getString(cursor.getColumnIndex(ITEM_DESC));
				String fullName = cursor.getString(cursor.getColumnIndex(ITEM_FULLNAME));
				String itemToolType = cursor.getString(cursor.getColumnIndex(ITEM_TOOL));
				int itemCount = cursor.getInt(cursor.getColumnIndex(ITEM_COUNT));
				int itemid = cursor.getInt(cursor.getColumnIndex(ITEM_ID));
				int itemLevel = cursor.getInt(cursor.getColumnIndex(ITEM_LEVEL));
				int quantity = cursor.getInt(cursor.getColumnIndex(REC_AMOUNT));

				// remove items from craft table
				db.delete(CRAFTTABLE, null, null);

				// add item to inv
				int id = cursor.getInt(cursor.getColumnIndex(ITEM_ID));

				// Give experience based on type
				db.close();

				String skill = cursor.getString(cursor.getColumnIndex(ITEM_SKILL));
				skillUpdate(skill, itemLevel, itemCount);

				// increment the item count
				itemIncrement(what);

				// change the title in case level changed
				((CogmalityActivity)a).updateTitle();

				for (int i = 0; i < quantity; i++) {
					addInv(id, 2, 1);
				}

				// TODO add Notification when these happen
				if (itemTotal + 1 == 10) {
					unlockAchievement("TENCRAFT");
				} else if (itemTotal + 1 == 25) {
					unlockAchievement("TWENTYFIVECRAFT");
				} else if (itemTotal + 1 == 50) {
					unlockAchievement("FIFTYCRAFT");
				} else if (itemTotal + 1 == 100) {
					unlockAchievement("ONEHCRAFT");
				} else if (itemTotal + 1 == 200) {
					unlockAchievement("TWOHCRAFT");
				}


				// TODO add back the sounds. Use SoundPool instead of the below code
				//MediaPlayer mediaPlayer = MediaPlayer.create(a, R.raw.crank1);
				//mediaPlayer.start(); // no need to call prepare(); create() does that for you

				((CogmalityActivity)a).playSound(CogmalityActivity.CRANK);

				if (itemCount == 0) {

					toolCheck(a, itemToolType, itemid, what);

					// mark as discovered
					String whereitem = ITEM_ID + "=" + id;
					ContentValues values = new ContentValues();
					values.put(ITEM_DISCOVERED, "" + 1);
					db = this.getReadableDatabase();

					db.update(ITEMTABLE, values, whereitem, null);

					((CogmalityActivity)a).playSound(CogmalityActivity.TADA);

					journalEntry(what, desc);

					//		               FragmentManager fm = a.getSupportFragmentManager();
					//		                RecipeDialog ceDialog = new RecipeDialog(fullName, key.substring(1, key.length() - 1), tool, "You learned to make a " + fullName + "!");
					//		                ceDialog.show(fm, "fragment_recipe");        
					//		 

					Builder adb = new AlertDialog.Builder(a);
					LayoutInflater inflater = (LayoutInflater)a.getSystemService
					(Context.LAYOUT_INFLATER_SERVICE);
					View recview = inflater.inflate(R.layout.recipe_layout, null);
					ImageView toolimg = (ImageView)recview.findViewById(R.id.toolimage);
					toolimg.setImageDrawable(a.getResources().getDrawable(CogmalitySQLHelper.getResByName(a, tool)));
					ImageView[] images = {(ImageView)recview.findViewById(R.id.image1),
							(ImageView)recview.findViewById(R.id.image2),
							(ImageView)recview.findViewById(R.id.image3),
							(ImageView)recview.findViewById(R.id.image4)};
					TextView[] pluses = {(TextView)recview.findViewById(R.id.text1),
							(TextView)recview.findViewById(R.id.text2),
							(TextView)recview.findViewById(R.id.text3)};
					String how = key.substring(1, key.length() - 1);
					StringTokenizer st = new StringTokenizer(how, ",");
					int size = 0;
					while (st.hasMoreTokens()) {
						images[size].setImageDrawable(a.getResources().getDrawable(CogmalitySQLHelper.getResByName(a, st.nextToken())));
						size++;
					}
					for (; size < images.length; size++) {
						images[size].setVisibility(View.GONE);
						pluses[size-1].setVisibility(View.GONE);
					}
					adb.setView(recview);
					adb.setTitle("You learned to make a " + fullName + "!");
					adb.setIcon(CogmalitySQLHelper.getResByName(a, what));

					adb.setPositiveButton("Ok", null);
					adb.show();
				} else {
					Toast.makeText(a, "You made " + quantity + " " + fullName + "!", Toast.LENGTH_SHORT).show();
				}
			}

			cursor.close();
			db.close();
		}		
	}

	public void toolCheck(Context c, String itemToolType, int itemid, String what) {
		if (itemToolType.startsWith("pickup")) {
			StringTokenizer pickup = new StringTokenizer(itemToolType.substring(6, itemToolType.length()), "|");
			while (pickup.hasMoreTokens()) {
				String mypick = pickup.nextToken();
				Log.d("picking", "pickup = " + mypick);
				pickupIncrement(mypick);					
			}
			toolAddition(what, "pickup");
			takeInv(itemid, QUALITY_DEFAULT, 1);
			Toast.makeText(c, "Tool placed on exploring shelf for use.", Toast.LENGTH_SHORT).show();
		} else if (itemToolType.startsWith("tool")) {
			toolAddition(what, "craft");
			takeInv(itemid, QUALITY_DEFAULT, 1);
			Toast.makeText(c, "Tool placed on crafting shelf for use.", Toast.LENGTH_SHORT).show();
//		} else if (itemToolType.startsWith("store")) {
//			statIncrement(CogmalitySQLHelper.STAT_STORE);
//			takeInv(itemid, QUALITY_DEFAULT, 1);
//			Toast.makeText(c, "You can now place a store when exploring.", Toast.LENGTH_SHORT).show();
		}
	}


//	public void rejectOffers(String item) {
//		String me = getUsername();
//		SQLiteDatabase db = getReadableDatabase();
//
//		String[] backtome = {MAIL_ITEMTYPE, MAIL_OFFER, MAIL_FROM, BaseColumns._ID};
//		String where = MAIL_ITEM + "=\"" + item + "\"";
//		Cursor cursor = db.query(MAILTABLE, backtome, 
//				where, null,
//				null, null, null);
//		while(cursor.moveToNext()) {
//			try {
//				String from = cursor.getString(cursor.getColumnIndex(MAIL_FROM));
//				String itemType = cursor.getString(cursor.getColumnIndex(MAIL_ITEMTYPE));
//				String offer = cursor.getString(cursor.getColumnIndex(MAIL_OFFER));
//				int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
//				db.delete(CogmalitySQLHelper.MAILTABLE, BaseColumns._ID + "=" + id, null);
//				ParsePush push = new ParsePush();
//				push.setChannel(from);
//				//push.setExpirationTimeInterval(86400);
//
//				push.setData(new JSONObject("{\"action\":\"edu.centenary.cogmality.TRADE_ANSWER\","+
//						"\"mailmessage\":\"" + "I respectfully decline your offer, thank you. Here are your items returned in good condition." + "\","+
//						"\"itemtype\":\"" + itemType + "\","+
//						"\"mailtype\":\"" + "REJECT" + "\","+
//						"\"maildata\":\"" + offer + "\","+
//						"\"userfrom\":\"" + me + "\"}"));
//				push.sendInBackground();
//			} catch (JSONException e) {
//				Log.d("StoreMakeOffer", "JSONException: " + e.getMessage());
//			}
//		}
//		db.close();
//	}
}
