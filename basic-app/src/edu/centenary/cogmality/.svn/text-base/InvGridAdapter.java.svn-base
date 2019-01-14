package edu.centenary.cogmality;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class InvGridAdapter extends CursorAdapter {
	String cName;
	
	public InvGridAdapter(Context context, Cursor c, String name) {
		super(context, c);
		cName = name;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImageView img = (ImageView) view;
		String name = cursor.getString(cursor.getColumnIndex(cName));
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
        v.setPadding(2, 2, 2, 2);
        bindView(v, context, cursor);
        return v;
	}
}
