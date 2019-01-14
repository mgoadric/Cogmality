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

public class InventoryCursorAdapter extends CursorAdapter {
	private LayoutInflater mInflater;
	private int mPrice;
	private int mName;
	private int mFullName;
	private int mType;
	private int mAmount;
	private int mId;

	public InventoryCursorAdapter(Context context, Cursor c) {
		super(context, c);

		mPrice = c.getColumnIndex(CogmalitySQLHelper.ITEM_SELLPRICE);
		mName = c.getColumnIndex(CogmalitySQLHelper.ITEM_NAME);
		mFullName = c.getColumnIndex(CogmalitySQLHelper.ITEM_FULLNAME);
		mType = c.getColumnIndex(CogmalitySQLHelper.ITEM_TYPE);
		mAmount = c.getColumnIndex(CogmalitySQLHelper.INV_AMOUNT);
		mId = c.getColumnIndex(CogmalitySQLHelper.INV_ID);	
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context arg1, Cursor c) {
		TextView amount = (TextView) view.findViewById(R.id.toptext);
		TextView quality = (TextView) view.findViewById(R.id.bottomtext);
		ImageView img = (ImageView) view.findViewById(R.id.icon);
		int type = c.getInt(mId);

		img.setImageDrawable(writeOnDrawable(arg1, CogmalitySQLHelper.getResByName(arg1, c.getString(mName)), c.getString(mAmount)));
		String what = c.getString(mFullName);
		amount.setText(what);
		quality.setText("$" + c.getString(mPrice));
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		return mInflater.inflate(R.layout.row, null);
	}

//http://stackoverflow.com/questions/6691818/how-to-create-drawable-containing-image-as-background-and-text-over-it
	public BitmapDrawable writeOnDrawable(Context c, int drawableId, String text){

        Bitmap bm = BitmapFactory.decodeResource(c.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint(); 
        paint.setStyle(Style.FILL);  
        paint.setColor(Color.WHITE); 
        paint.setTextSize(50);
        if (Integer.parseInt(text) > 99) {
        	paint.setTextSize(38);
        }
        if (Integer.parseInt(text) > 990) {
        	paint.setTextSize(25);
        }
        paint.setFakeBoldText(true);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        
        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, 0, bm.getHeight(), paint);
        paint.setStyle(Style.STROKE);  
        paint.setColor(Color.BLACK); 
        canvas.drawText(text, 0, bm.getHeight(), paint);

        return new BitmapDrawable(bm);
    }
}
