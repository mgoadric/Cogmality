package edu.centenary.cogmality;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class CraftGridAdapter extends CursorAdapter {

	public CraftGridAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImageView img = (ImageView) view;
		String name = cursor.getString(cursor.getColumnIndex(CogmalitySQLHelper.ITEM_NAME));
		img.setImageDrawable(context.getResources().getDrawable(CogmalitySQLHelper.getResByName(context, name)));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		ImageView v = new ImageView(context);
		int w = context.getResources().getDisplayMetrics().widthPixels;
		int h = context.getResources().getDisplayMetrics().heightPixels;
		if (h < w) {
			w = h;
		}
        v.setLayoutParams(new GridView.LayoutParams(w/6, w/6));
        v.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        bindView(v, context, cursor);
        return v;
	}

}
