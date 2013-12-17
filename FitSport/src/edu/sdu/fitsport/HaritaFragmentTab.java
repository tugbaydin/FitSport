package edu.sdu.fitsport;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.sdu.fitsport.json.JSONParser;

public class HaritaFragmentTab extends SherlockFragment implements LocationListener{

	private GoogleMap googleMap;
	ArrayList<LatLng> markerPoints;

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
		markerPoints = new ArrayList<LatLng>();
		if(googleMap!=null){
			
			// Kendi yerini bulma aktif
			googleMap.setMyLocationEnabled(true);		
			
			// Harita üzerinde click eventý
			googleMap.setOnMapClickListener(new OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng point) {
					
					//Önceki noktalarý sil				
					if(markerPoints.size()>1){
						markerPoints.clear();
						googleMap.clear();					
					}
					
					// Týklanan nokta listede
					markerPoints.add(point);				
					MarkerOptions options = new MarkerOptions();					
					// Nokta üzerine pozisyon alma
					options.position(point);

					if(markerPoints.size()==1){
						options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
					}else if(markerPoints.size()==2){
						options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
					}

					googleMap.addMarker(options);
					
					// Baþlangýç ve bitiþ noktalarýný alma
					if(markerPoints.size() >= 2){					
						LatLng origin = markerPoints.get(0);
						LatLng dest = markerPoints.get(1);
						
						//Google Directions APIden URL çekme
						String url = getDirectionsUrl(origin, dest);				
						
						DownloadTask downloadTask = new DownloadTask();
						
						//Google Directions APIden indirmeye baþla
						downloadTask.execute(url);
					}
					
				}
			});
		}
		/*LocationManager lcManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		if (lcManager == null) {
			Log.i("Hata", "LocationManager null");
		}
		Criteria criteria = new Criteria();

		String provider = lcManager.getBestProvider(criteria, false);
		Location yer = lcManager.getLastKnownLocation(provider);
		
		if (yer != null) {
			onLocationChanged(yer);
		}*/

		
	}
	
	private String getDirectionsUrl(LatLng origin,LatLng dest){
		
		// Baþlangýç noktasý
		String baslangicNokta = "origin="+origin.latitude+","+origin.longitude;		
		// Bitiþ noktasý
		String bitisNokta = "destination="+dest.latitude+","+dest.longitude;		

		String sensor = "sensor=false";			
					
		// Web servis için parametreler
		String parameters = baslangicNokta+"&"+bitisNokta+"&"+sensor;
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
		
		return url;
	}
	
	/** JSON Veri Çekme */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
                URL url = new URL(strUrl);

                // HTTP Connection üzerinde Url
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                // URL Okuma
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                        sb.append(line);
                }
                
                data = sb.toString();

                br.close();

        }catch(Exception e){
                Log.d("Veri çekerken problem oluþtu - ", e.toString());
        }finally{
                iStream.close();
                urlConnection.disconnect();
        }
        return data;
     }

	
	
	// URLden Data Çekme
	private class DownloadTask extends AsyncTask<String, Void, String>{			
				
		// Non-ui thread ile veri indirme
		@Override
		protected String doInBackground(String... url) {
			String data = "";
					
			try{
				// Web Servisten Veri Çekme
				data = downloadUrl(url[0]);
			}catch(Exception e){
				Log.d("Background Task",e.toString());
			}
			return data;		
		}
		
		/*
		 * UI Thread ile veri çekilmesi doInBackgrounddan sonra
		 *
		 */
		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);			
			
			ParserTask parserTask = new ParserTask();
			
			// Veri çekilmesinden sonra thread çaðýrma
			parserTask.execute(result);
				
		}		
	}
	
	/** Google Places Verisini JSON ile Çekme */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
    	
    	// Non-Ui Threade Veri Geçme   	
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
			
			JSONObject jObject;	
			List<List<HashMap<String, String>>> routes = null;			           
            
            try{
            	jObject = new JSONObject(jsonData[0]);
            	JSONParser parser = new JSONParser();
            	
            	// JSON Verisi Parçalama
            	routes = parser.parse(jObject);    
            }catch(Exception e){
            	e.printStackTrace();
            }
            return routes;
		}
		
		// Parçalamadan sonra UI Thread Çalýþmasý
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();
			
			// Yolun Üzerinde Geçme
			for(int i=0;i<result.size();i++){
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();
				
				// Yolu Alma
				List<HashMap<String, String>> path = result.get(i);
				
				for(int j=0;j<path.size();j++){
					HashMap<String,String> point = path.get(j);					
					
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);	
					
					points.add(position);						
				}
				
				// LineOptionsa Yolu Ekleme
				lineOptions.addAll(points);
				lineOptions.width(2);
				lineOptions.color(Color.RED);	
				
			}
			
			// GoogleMaps Üzerinde Ýþaretleme
			googleMap.addPolyline(lineOptions);							
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
