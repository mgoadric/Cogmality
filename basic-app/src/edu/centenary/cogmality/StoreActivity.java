package edu.centenary.cogmality;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class StoreActivity extends SherlockFragmentActivity {

	private String owner;
	private String current;
    private String item;
    private String type;
    private String fullName;
    private String name;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_main_layout);

		Bundle bundle = this.getIntent().getExtras();
		owner = bundle.getString("username");
		if (owner != null) {
			getSupportActionBar().setTitle(owner + "'s store");
		} else {
			owner = savedInstanceState.getString("username");
			if (owner != null) {
				getSupportActionBar().setTitle(owner + "'s store");				
			} else {
				finish();
			}
		}
		Fragment newFragment = new StoreFrontFragment();
		current = "STOREFRONT";
		FragmentManager fragMgr = this.getSupportFragmentManager();
		FragmentTransaction ft2 = fragMgr.beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack so the user can navigate back
		if (ft2 != null) {
			ft2.replace(R.id.fragment_container_store, newFragment, "STOREFRONT");
		}
		ft2.commit();
		
		if (savedInstanceState != null) {
			String which = savedInstanceState.getString("tabState");
			Log.d("Store", "which is " + which);
			if(which != null && which.equals("MAKEOFFER")) {
				makeOffer(savedInstanceState.getString("item"), 
						savedInstanceState.getString("type"), 
						savedInstanceState.getString("fullName"), 
						savedInstanceState.getString("name"));
			}
		}
	}
	
	public void makeOffer(String item, String type, String fullName, String name) {
		Fragment newFragment = new MakeOfferFragment();
        Bundle args = new Bundle();
        args.putString("item", item);
        args.putString("type", type);
        args.putString("fullName", fullName);
        args.putString("name", name);
        newFragment.setArguments(args);
        this.item = item;
        this.type = type;
        this.fullName = fullName;
        this.name = name;

		current = "MAKEOFFER";
		FragmentManager fragMgr = this.getSupportFragmentManager();
		FragmentTransaction ft2 = fragMgr.beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack so the user can navigate back
		if (ft2 != null) {
		    ft2.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			ft2.replace(R.id.fragment_container_store, newFragment).addToBackStack(null);

		}
		ft2.commit();
	}
	
	public void returnFront() {
		Fragment newFragment = new StoreFrontFragment();
		FragmentManager fragMgr = this.getSupportFragmentManager();

	    fragMgr.popBackStackImmediate();

//		FragmentTransaction ft2 = fragMgr.beginTransaction();
//
//		// Replace whatever is in the fragment_container view with this fragment,
//		// and add the transaction to the back stack so the user can navigate back
//		if (ft2 != null) {
//			ft2.replace(R.id.fragment_container_store, newFragment, "STOREFRONT");
//		}
//		ft2.commit();


	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		FragmentManager fragMgr = this.getSupportFragmentManager();
	    fragMgr.popBackStackImmediate();
	    outState.putString("tabState", current);
	    outState.putString("username", owner);
	    outState.putString("item", item);
	    outState.putString("type", type);
	    outState.putString("fullName", fullName);
	    outState.putString("name", name);
	    Log.d("Store", "saving current as " + current);
	    super.onSaveInstanceState(outState);
	}
	
	public void onDestroy() {
		super.onDestroy();
	}
}