package edu.sdu.fitsport;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;

import edu.sdu.fitsport.adapter.CustomSpinnerAdapter;


public class AktiviteFragmentTab extends SherlockFragment {

	String[] spinnerDeger = {"Koþma", "Yüzme", "Basketbol"};
	int[] spinnerResim = {R.drawable.running, R.drawable.swimming, R.drawable.basketball};
	Spinner spSecim;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.aktivite_fragment_tab, container, false);	
		spSecim = (Spinner) view.findViewById(R.id.secim_spinner);
		spSecim.setAdapter(new CustomSpinnerAdapter(getActivity(), R.layout.custom_spinner, spinnerDeger, spinnerResim));
		
		
		return view;
	}
	
	
}
