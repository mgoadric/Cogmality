package edu.centenary.cogmality;

import java.util.StringTokenizer;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.view.Window;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RecipeDialog extends SherlockDialogFragment {

	private String what;
	private String how;
	private String tool;
	private String title;

    public RecipeDialog() {
        // Empty constructor required for DialogFragment
}

    public RecipeDialog(String what, String how, String tool, String title) {
    	this.what = what;
    	this.how = how;
    	this.tool = tool;
    	this.title = title;
    	//setStyle(SherlockDialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            
		View recview = inflater.inflate(R.layout.recipe_layout, null);
		Dialog dialog = getDialog();
		//dialog.requestWindowFeature((int)Window.FEATURE_NO_TITLE);


		//getDialog().setIcon(2);
		ImageView toolimg = (ImageView)recview.findViewById(R.id.toolimage);
		toolimg.setImageDrawable(getActivity().getResources().getDrawable(CogmalitySQLHelper.getResByName(getActivity(), tool)));
		ImageView[] images = {(ImageView)recview.findViewById(R.id.image1),
				(ImageView)recview.findViewById(R.id.image2),
				(ImageView)recview.findViewById(R.id.image3),
				(ImageView)recview.findViewById(R.id.image4)};
		TextView[] pluses = {(TextView)recview.findViewById(R.id.text1),
				(TextView)recview.findViewById(R.id.text2),
				(TextView)recview.findViewById(R.id.text3)};
		StringTokenizer st = new StringTokenizer(how, ",");
		int size = 0;
		while (st.hasMoreTokens()) {
			images[size].setImageDrawable(getActivity().getResources().getDrawable(CogmalitySQLHelper.getResByName(getActivity(), st.nextToken())));
			size++;
		}
		for (; size < images.length; size++) {
			images[size].setVisibility(View.GONE);
			pluses[size-1].setVisibility(View.GONE);
		}
		
        return recview;
    }    
}
