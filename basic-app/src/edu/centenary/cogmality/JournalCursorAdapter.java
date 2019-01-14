package edu.centenary.cogmality;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class JournalCursorAdapter extends CursorAdapter {
	private LayoutInflater mInflater;
	private int mName;
	private int mText;
	private int mTime;
	private int mStyle;

	public JournalCursorAdapter(Context context, Cursor c) {
		super(context, c);

		mName = c.getColumnIndex(CogmalitySQLHelper.JOURNAL_IMAGE);
		mText = c.getColumnIndex(CogmalitySQLHelper.JOURNAL_TEXT);
		mTime = c.getColumnIndex(CogmalitySQLHelper.JOURNAL_TIME);
		mStyle = c.getColumnIndex(CogmalitySQLHelper.JOURNAL_STYLE);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context arg1, Cursor c) {
		TextView amount = (TextView) view.findViewById(R.id.bottomtext);
		TextView quantity = (TextView) view.findViewById(R.id.toptext);
		ImageView img = (ImageView) view.findViewById(R.id.icon);
		Date date = new Date(c.getLong(mTime));
		String style = c.getString(mStyle);
		// Make this dependent on having hourglass and watch
		DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy");
		if (style.equals("hourglass"))
			formatter = new SimpleDateFormat("EEE, d MMM yyyy hh aa");
		else if (style.equals("watch")) {
			formatter = new SimpleDateFormat("EEE, d MMM yyyy hh:mm aa");
		}
		String dateFormatted = formatter.format(date);
		img.setImageResource(CogmalitySQLHelper.getResByName(arg1, c.getString(mName)));
		String what = c.getString(mText);
		amount.setText(what);
		quantity.setText(dateFormatted);
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		return mInflater.inflate(R.layout.journal_row, null);
	}
}