package edu.sdu.fitsport.adapter;


import edu.sdu.fitsport.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

	String[] spinnerDeger;
	int[] spinnerResim;
	
	public CustomSpinnerAdapter(Context context, int textViewResourceId, String[] objects, int[] spinnerResim) {
		super(context, textViewResourceId, objects);
		this.spinnerDeger = objects;
		this.spinnerResim = spinnerResim;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return getCustomView(position, convertView, parent);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return getCustomView(position, convertView, parent);
	}
	
	public View getCustomView(int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View customSpinner = inflater.inflate(R.layout.custom_spinner, parent, false);
		
		TextView sporAd = (TextView) customSpinner.findViewById(R.id.sp_text);
		sporAd.setText(spinnerDeger[position]);
		
		ImageView spImage = (ImageView) customSpinner.findViewById(R.id.sp_resim);
		spImage.setImageResource(spinnerResim[position]);
				
		return customSpinner;
		
	}

	

}
