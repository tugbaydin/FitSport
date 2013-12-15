package edu.sdu.fitsport;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HaritaFragmentTab extends SherlockFragment implements LocationListener{

	private GoogleMap googleMap;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.harita_fragment_tab, container,
				false);
		try {
			haritaYukle();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return view;
	}

	private void haritaYukle() {
		googleMap = ((SupportMapFragment) getSherlockActivity().getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		googleMap.setMyLocationEnabled(true);
		LocationManager lcManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		if (lcManager == null) {
			Log.i("Hata", "LocationManager null");
		}
		Criteria criteria = new Criteria();

		String provider = lcManager.getBestProvider(criteria, false);
		Location yer = lcManager.getLastKnownLocation(provider);
		
		if (yer != null) {
			onLocationChanged(yer);
		}

		
	}

	@Override
	public void onLocationChanged(Location location) {
		double enlem = location.getLatitude();
		double boylam = location.getLongitude();
		TextView enlemTv = (TextView) getView().findViewById(R.id.textView1);
		TextView boylamTv = (TextView) getView().findViewById(R.id.textView2);
		enlemTv.setText(String.valueOf(enlem));
		boylamTv.setText(String.valueOf(boylam));

		LatLng konum = new LatLng(enlem, boylam);

		googleMap.moveCamera(CameraUpdateFactory.newLatLng(konum));
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(20));
		googleMap.addMarker(new MarkerOptions().position(konum).title(
				"Baþlangýç"));
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
