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

public class CraftCursorAdapter extends CursorAdapter {
	private LayoutInflater mInflater;
	private int mName;
	private int mFullName;
	private int mAmount;

	public CraftCursorAdapter(Context context, Cursor c) {
		super(context, c);

		mName = c.getColumnIndex(CogmalitySQLHelper.ITEM_NAME);
		mFullName = c.getColumnIndex(CogmalitySQLHelper.ITEM_FULLNAME);
		mAmount = c.getColumnIndex(CogmalitySQLHelper.INV_AMOUNT);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context arg1, Cursor c) {
		TextView amount = (TextView) view.findViewById(R.id.toptext);
		TextView quantity = (TextView) view.findViewById(R.id.bottomtext);
		ImageView img = (ImageView) view.findViewById(R.id.icon);

		img.setImageResource(CogmalitySQLHelper.getResByName(arg1, c.getString(mName)));
		String what = c.getString(mFullName);
		amount.setText(what);
		quantity.setText(c.getString(mAmount));
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		return mInflater.inflate(R.layout.image_two_column_list_row, null);
	}
}