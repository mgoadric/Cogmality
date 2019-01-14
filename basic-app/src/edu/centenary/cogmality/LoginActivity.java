package edu.centenary.cogmality;

import com.actionbarsherlock.app.SherlockFragmentActivity;
//import com.parse.ParseException;
//import com.parse.ParseUser;
//import com.parse.SignUpCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends SherlockFragmentActivity {

	public static final int SUCCESS = 0;
	public static final int USERNAME_TAKEN = 1;
	public static final int BAD_PASSWORD = 2;
	public static final int EMPTY_USERNAME = 3;
	public static final int EMPTY_PASSWORD = 4;
	public static final int EMPTY_EMAIL = 5;
	
	private CogmalitySQLHelper steamdb;
    private ProgressDialog pd;
    private String mUname;
    private String mPword;
    private String mEmail;
    private EditText uname;
	private TextView warnings;
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
        steamdb = new CogmalitySQLHelper(this);
		uname = (EditText)findViewById(R.id.username);
		warnings = (TextView)findViewById(R.id.warnings);
		
		if (steamdb.ready()) {
//			new RefreshItemTask().execute();
    		Intent i = new Intent(LoginActivity.this, CogmalityActivity.class);
    		startActivity(i);						
			finish();
		} 
		
		Button b = (Button)findViewById(R.id.loginbutton);
    	b.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg1) {
				
				// TODO is this out of sequence? If crashes in the next part, we still get to the app, shouldn't happen
				mUname = uname.getText().toString();
				mPword = "x";
				mEmail = "x";
				
				int code = SUCCESS;

				if (mUname.equals("")) {
					code = EMPTY_USERNAME;
				} else if (mPword.equals("")) {
					code = EMPTY_PASSWORD;
				} else if (mEmail.equals("")) {
					code = EMPTY_PASSWORD;
				} else {
		 			pd = ProgressDialog.show(LoginActivity.this,"Attempting Login","Searching for duplicates...",true,false,null);

//					ParseUser user = new ParseUser();
//					user.setUsername(mUname);
//					user.setPassword(mPword);
//					user.setEmail(mEmail);
//	
//					user.signUpInBackground(new SignUpCallback() {
//					  public void done(ParseException e) {
//						pd.dismiss();
//					    if (e == null) {
//					      // Hooray! Let them use the app now.
				
//					    } else {
//					      // Sign up didn't succeed. Look at the ParseException
//					      // to figure out what went wrong
//					    	warnings.setVisibility(View.VISIBLE);
//					    	warnings.setText("TRY AGAIN!");
//					    }
//					  }
//					});

				}
				switch (code) {
					case SUCCESS: {
				    	initialUser();
						break;
					} 
					case EMPTY_PASSWORD: {
				    	warnings.setVisibility(View.VISIBLE);
				    	warnings.setText("You need a password.");
				    	break;
					}
					case EMPTY_USERNAME: {
				    	warnings.setVisibility(View.VISIBLE);
				    	warnings.setText("You need a username.");
				    	break;
					}
					case EMPTY_EMAIL: {
				    	warnings.setVisibility(View.VISIBLE);
				    	warnings.setText("You need an email.");
				    	break;
					}
				}
			}
		});

	}
	
	private void initialUser() {
		steamdb.newUser(mUname, mPword, mEmail);
		new RefreshItemTask().execute();
	}
	
	private class RefreshItemTask extends AsyncTask<String, Void, Void> {
		protected void onPreExecute() {
  			pd = ProgressDialog.show(LoginActivity.this,"Initializing Knowledge","Flux Capacitor Enabled...",true,false,null);
		}
		
	     protected Void doInBackground(String... urls) {
	         return steamdb.addItems(LoginActivity.this);
	     }

	     protected void onPostExecute(Void v) {
	    	 pd.dismiss();
			new RefreshAchTask().execute();

	     }
	}	
	private class RefreshAchTask extends AsyncTask<String, Void, Void> {
		protected void onPreExecute() {
  			pd = ProgressDialog.show(LoginActivity.this,"Loading History","Get Ready For Adventure...",true,false,null);
		}
		
	     protected Void doInBackground(String... urls) {
	         return steamdb.addAchievements(LoginActivity.this);
	     }

	     protected void onPostExecute(Void v) {
	    	 pd.dismiss();
			new RefreshRecipeTask().execute();

	     }
	}
	
	private class RefreshRecipeTask extends AsyncTask<String, Void, Void> {
		protected void onPreExecute() {
  			pd = ProgressDialog.show(LoginActivity.this,"Setting Up Connection","Reticulating Splines...",true,false,null);
		}
		
	     protected Void doInBackground(String... urls) {
	    	 steamdb.addSkills(LoginActivity.this);
	         return steamdb.addRecipes(LoginActivity.this);

	     }

	     protected void onPostExecute(Void v) {
	    	 pd.dismiss();
	    	 Intent i = new Intent(LoginActivity.this, CogmalityActivity.class);
	     	 startActivity(i);						
			 finish();
	     }
	}
}
