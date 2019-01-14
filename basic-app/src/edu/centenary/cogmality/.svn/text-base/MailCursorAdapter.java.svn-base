package edu.centenary.cogmality;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseObject;
import com.parse.ParsePush;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MailCursorAdapter extends CursorAdapter {
	private LayoutInflater mInflater;
	private int mFrom;
	private int mOffer;
	private int mMessage;
	private int mStatus;
	private int mItem;
	private int mItemType;
	private int mItemFullname;
	private int mType;
	private int mTime;
	private Context context;
	private MailFragment a;

	public MailCursorAdapter(Context context, Cursor c, MailFragment a) {
		super(context, c);
		this.context = context;
		this.a = a;
		mFrom = c.getColumnIndex(CogmalitySQLHelper.MAIL_FROM);
		mOffer = c.getColumnIndex(CogmalitySQLHelper.MAIL_OFFER);
		mMessage = c.getColumnIndex(CogmalitySQLHelper.MAIL_MESSAGE);
		mStatus = c.getColumnIndex(CogmalitySQLHelper.MAIL_STATUS);
		mItem = c.getColumnIndex(CogmalitySQLHelper.MAIL_ITEM);
		mItemType = c.getColumnIndex(CogmalitySQLHelper.MAIL_ITEMTYPE);
		mItemFullname = c.getColumnIndex(CogmalitySQLHelper.MAIL_ITEMFULLNAME);
		mType = c.getColumnIndex(CogmalitySQLHelper.MAIL_TYPE);
		mTime = c.getColumnIndex(CogmalitySQLHelper.MAIL_TIME);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context arg1, Cursor c) {
		TextView mess = (TextView) view.findViewById(R.id.bottomtext);
		TextView time = (TextView) view.findViewById(R.id.timestamp);
		TextView who = (TextView) view.findViewById(R.id.toptext);
		ImageView img = (ImageView) view.findViewById(R.id.icon);

		Date date = new Date(c.getLong(mTime));
		DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
		String dateFormatted = formatter.format(date);
		time.setText(dateFormatted);

		img.setImageResource(CogmalitySQLHelper.getResByName(arg1, c.getString(mItemType)));

		String what = c.getString(mMessage);
		mess.setText(what);

		ImageView[] offerview = {(ImageView) view.findViewById(R.id.imageView1),
				(ImageView) view.findViewById(R.id.imageView2),
				(ImageView) view.findViewById(R.id.imageView3),
				(ImageView) view.findViewById(R.id.imageView4),
				(ImageView) view.findViewById(R.id.imageView5)};
		StringTokenizer pickup = new StringTokenizer(c.getString(mOffer), "|");

		Log.d("MailOffer", c.getString(mOffer));
		if (c.getString(mType) != null && c.getString(mType).equals("ACCEPT")) {
			for (ImageView iv: offerview) {
				iv.setVisibility(View.GONE);
			}
		} else {
			for (ImageView iv: offerview) {
				if (pickup.hasMoreTokens()) {
					iv.setImageResource(CogmalitySQLHelper.getResByName(arg1, pickup.nextToken()));
					iv.setVisibility(View.VISIBLE);
				} else {
					iv.setVisibility(View.INVISIBLE);
				}
			}
		}
		Button accept = (Button)view.findViewById(R.id.accept);
		Button reject = (Button)view.findViewById(R.id.reject);
		if (c.getString(mType) != null && !c.getString(mType).equals("OFFER")) {
			accept.setVisibility(View.GONE);
			reject.setVisibility(View.GONE);
			mess.setVisibility(View.VISIBLE);
			who.setText("Reply from " + c.getString(mFrom));
		} else {
			accept.setVisibility(View.VISIBLE);
			reject.setVisibility(View.VISIBLE);
			mess.setVisibility(View.GONE);
			who.setText("Offer from " + c.getString(mFrom));
		}
		accept.setOnClickListener(new AcceptOnClickListener(a, c.getInt(c.getColumnIndex(BaseColumns._ID)),
				c.getString(mFrom), c.getString(mOffer), c.getString(mItem), c.getString(mItemType)));

		reject.setOnClickListener(new RejectOnClickListener(a, c.getInt(c.getColumnIndex(BaseColumns._ID)),
				c.getString(mFrom), c.getString(mOffer), c.getString(mItem), c.getString(mItemType)));

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		return mInflater.inflate(R.layout.mail_row, null);
	}

	private class RejectOnClickListener implements OnClickListener {

		private int id;
		private String from;
		private String offer;
		private String item;
		private String itemType;
		private MailFragment a;

		public RejectOnClickListener(MailFragment a, int id, String from, String offer, String item, String itemType) {
			this.id = id;
			this.from = from;
			this.offer = offer;
			this.item = item;
			this.itemType = itemType;
			this.a = a;
		}

		@Override
		public void onClick(View v) {
			CogmalitySQLHelper steamdb = new CogmalitySQLHelper(context);

			SQLiteDatabase db = steamdb.getReadableDatabase();
			db.delete(CogmalitySQLHelper.MAILTABLE, BaseColumns._ID + "=" + id, null);
			db.close();


			try {
				ParsePush push = new ParsePush();
				push.setChannel(from);
				//push.setExpirationTimeInterval(86400);

				push.setData(new JSONObject("{\"action\":\"edu.centenary.cogmality.TRADE_ANSWER\","+
						"\"mailmessage\":\"" + "I respectfully decline your offer, thank you. Here are your items returned in good condition." + "\","+
						"\"itemtype\":\"" + itemType + "\","+
						"\"mailtype\":\"" + "REJECT" + "\","+
						"\"maildata\":\"" + offer + "\","+
						"\"userfrom\":\"" + steamdb.getUsername() + "\"}"));
				push.sendInBackground();
			} catch (JSONException e) {
				Log.d("StoreMakeOffer", "JSONException: " + e.getMessage());
			}
			a.updateViews();
		}
	}

	private class AcceptOnClickListener implements OnClickListener {

		private int id;
		private String from;
		private String offer;
		private String item;
		private String itemType;
		private MailFragment a;

		public AcceptOnClickListener(MailFragment a, int id, String from, String offer, String item, String itemType) {
			this.id = id;
			this.from = from;
			this.offer = offer;
			this.item = item;
			this.itemType = itemType;
			this.a = a;
		}

		@Override
		public void onClick(View v) {
			CogmalitySQLHelper steamdb = new CogmalitySQLHelper(context);
			StringTokenizer pickup = new StringTokenizer(offer, "|");
			while (pickup.hasMoreTokens()) {
				steamdb.addInvName(a.getActivity(), pickup.nextToken(), 2, 1);
			}

			SQLiteDatabase db = steamdb.getReadableDatabase();
			db.delete(CogmalitySQLHelper.MAILTABLE, BaseColumns._ID + "=" + id, null);
			db.close();

			// TODO REMOVE FROM STORE
			ParseObject p = new ParseObject("StoreItems");
			p.setObjectId(item);
			p.deleteInBackground();

			// REMOVE OTHER MAIL OFFERS
//			steamdb.rejectOffers(item);
			
			try {
				ParsePush push = new ParsePush();
				push.setChannel(from);
				//push.setExpirationTimeInterval(86400);

				StringBuffer myoffer = new StringBuffer();
				String myOffer = itemType;
				push.setData(new JSONObject("{\"action\":\"edu.centenary.cogmality.TRADE_ANSWER\","+
						"\"mailmessage\":\"" + "I accept your gracious offer! Thanks for the trade." + "\","+
						"\"itemtype\":\"" + itemType + "\","+
						"\"mailtype\":\"" + "ACCEPT" + "\","+
						"\"maildata\":\"" + myOffer + "\","+
						"\"userfrom\":\"" + steamdb.getUsername() + "\"}"));
				push.sendInBackground();
			} catch (JSONException e) {
				Log.d("StoreMakeOffer", "JSONException: " + e.getMessage());
			}
			a.updateViews();
		}

	}
}
