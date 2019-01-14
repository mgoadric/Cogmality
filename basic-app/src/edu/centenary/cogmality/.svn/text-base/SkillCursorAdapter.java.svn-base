package edu.centenary.cogmality;

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

public class SkillCursorAdapter extends CursorAdapter {
	private LayoutInflater mInflater;
	private int mName;
	private int mPoints;

	public SkillCursorAdapter(Context context, Cursor c) {
		super(context, c);

		mName = c.getColumnIndex(CogmalitySQLHelper.SKILL_NAME);
		mPoints = c.getColumnIndex(CogmalitySQLHelper.SKILL_POINTS);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context arg1, Cursor c) {
		TextView amount = (TextView) view.findViewById(R.id.toptext);
		TextView quality = (TextView) view.findViewById(R.id.bottomtext);
		String what = c.getString(mName);
		amount.setText(what);
		quality.setText(c.getString(mPoints));
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		return mInflater.inflate(R.layout.two_column_list_row, null);
	}

}
