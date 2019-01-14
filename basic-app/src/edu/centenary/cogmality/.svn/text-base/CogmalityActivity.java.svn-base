package edu.centenary.cogmality;

import java.util.HashMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseIntArray;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;
//import com.parse.PushService;

public class CogmalityActivity extends SherlockFragmentActivity implements ActionBar.TabListener{
	
	private CogmalitySQLHelper steamdb;
	private CharSequence selected;
	
	private SoundPool soundPool;
    private SparseIntArray soundsMap;
    private Fragment newFragment;
    private ActionBar.Tab mailTab;
    public static int CRANK=1;
    public static int TADA=2;
    public static int NOCRAFT=3; 
 

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
   
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundsMap = new SparseIntArray();
        soundsMap.put(CRANK, soundPool.load(this, R.raw.crank1, 1));
        soundsMap.put(TADA, soundPool.load(this, R.raw.tada, 1));
        soundsMap.put(NOCRAFT, soundPool.load(this, R.raw.nocraft, 1));
   
        
        steamdb = new CogmalitySQLHelper(this);
//		PushService.subscribe(this, steamdb.getUsername(), CogmalityActivity.class);
        
        updateTitle();
        
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
            ActionBar.Tab tab = getSupportActionBar().newTab();
            tab.setText("Journal");
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);
            
            tab = getSupportActionBar().newTab();
            tab.setText("Knowledge");
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);
    
//            tab = getSupportActionBar().newTab();
//            tab.setText("Mail");
//            tab.setTabListener(this);
//            mailTab = tab;
//            getSupportActionBar().addTab(tab);
    
//            tab = getSupportActionBar().newTab();
//            tab.setText("Inventory");
//            tab.setTabListener(this);
//            getSupportActionBar().addTab(tab);

            tab = getSupportActionBar().newTab();
            tab.setText("Craft");
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);

            tab = getSupportActionBar().newTab();
            tab.setText("Tools");
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);

            tab = getSupportActionBar().newTab();
            tab.setText("Skills");
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);

//            tab = getSupportActionBar().newTab();
//            tab.setText("Achievements");
//            tab.setTabListener(this);
//            getSupportActionBar().addTab(tab);

            if( savedInstanceState != null ){
                getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt("tabState"));
            }
    		Bundle bundle = this.getIntent().getExtras();
    		if (bundle != null) {
                getSupportActionBar().selectTab(mailTab);   			
    		}

    }

    public void updateTitle() {
        String title = steamdb.getUsername();
        String subtitle = steamdb.currentType() + " : " + steamdb.currentLevel();
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(subtitle);   	
    }
    
    public void onResume() {
    	super.onResume();
    	updateTitle();
    }
    
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
        selected = tab.getText();
        if (selected.equals("Craft")) {
        	newFragment = new CraftFragment();
        } else if (selected.equals("Knowledge")) {
        	newFragment = new InventoryFragment();
        } else if (selected.equals("Achievements")) {
        	newFragment = new AchievementsFragment();
        } else if (selected.equals("Inventory")) {
        	newFragment = new SellFragment();
        } else if (selected.equals("Journal")) {
        	newFragment = new JournalFragment();
        } else if (selected.equals("Skills")) {
        	newFragment = new SkillsFragment();
        } else if (selected.equals("Mail")) {
        	newFragment = new MailFragment();
        } else {
            	newFragment = new StatsFragment();
        } 
        FragmentManager fragMgr = this.getSupportFragmentManager();
        FragmentTransaction ft2 = fragMgr.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        if (ft2 != null) {
        	ft2.replace(R.id.fragment_container_main, newFragment);
        }
        ft2.commit();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putInt("tabState", getSupportActionBar().getSelectedNavigationIndex());
	}
	
    public void playSound(int sound) {
        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;  
 
        soundPool.play(soundsMap.get(sound), volume, volume, 1, 0, 1);
       }
 

	
	public void explore() {
    	// Don't let them play without a GPS connection
	    LocationManager locMan = (LocationManager) CogmalityActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (!locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
    		Builder adb = new AlertDialog.Builder(CogmalityActivity.this);
    		adb.setIcon(R.drawable.location_map);
        	adb.setTitle("GPS Required");
        	adb.setMessage("The map requires you to have" +
        			" your GPS activated. Click OK to go to " +
        			"the screen for activation, otherwise exit" +
        			" with Nevermind.");
        	adb.setPositiveButton("OK", new DialogInterface.OnClickListener() { 

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
			        Intent gpsOptionsIntent = new Intent(  
			                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
			        CogmalityActivity.this.startActivity(gpsOptionsIntent);  
				}});
        	adb.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() { 

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					
				}});
        	adb.show();
        } else {
        	Intent i = new Intent(CogmalityActivity.this, CogmalityMapActivity.class);
        	CogmalityActivity.this.startActivity(i);
        }

	}

	public void about() {
    		Builder adb = new AlertDialog.Builder(CogmalityActivity.this);
    		adb.setIcon(R.drawable.ic_menu_about);
        	adb.setTitle("About Cogmality");
        	adb.setMessage("Programmed by the Catahoula Coders of Centenary College.\n\nSome icons inspired and modified from images created by Connor Cesa, George T Hayes, Luke Firth, Fabio Meroni, Randall Barriga, Adhara Garcia, Jakob Vogel, Mihir Deshpande at thenounproject.com.\n\nSounds courtesy of soundjay.com");
        	adb.setPositiveButton("OK", new DialogInterface.OnClickListener() { 

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}});
        	adb.show();

	}

	/**
     * Creates the option menu from the XML
     */
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Show the selected option from the menu. 
     */
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
       	case R.id.explore:
       		explore();
    		break;
       	case R.id.about:
       		about();
    		break;
    	case R.id.wipe:
    		steamdb.wipe();
    		Intent i = new Intent(CogmalityActivity.this, LoginActivity.class);
    		startActivity(i);						
			finish();
			break;
    	case R.id.feedback:
    		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/spreadsheet/viewform?pli=1&hl=en_US&formkey=dDljSHdlY0liUWZBYTUzUTVlR2lBbkE6MQ#gid=0"));
    		startActivity(browserIntent);
    		break;
    	}
    	return true;
    	
    }
    
    /**
     * Create the menu for the Map
     */
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.findItem(R.id.explore).setTitle(R.string.settings);
    	menu.findItem(R.id.explore).setIcon(R.drawable.location_map);
    	menu.findItem(R.id.wipe).setTitle(R.string.wipe);
    	menu.findItem(R.id.wipe).setIcon(R.drawable.ic_menu_delete);
    	menu.findItem(R.id.about).setTitle(R.string.about);
    	menu.findItem(R.id.about).setIcon(R.drawable.ic_menu_about);
    	menu.findItem(R.id.feedback).setTitle(R.string.feedback);
    	menu.findItem(R.id.feedback).setIcon(R.drawable.ic_menu_shout);
    	return true;
    }
    
}