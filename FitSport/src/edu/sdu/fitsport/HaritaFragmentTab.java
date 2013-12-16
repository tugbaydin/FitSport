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

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
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
			
			// Enable MyLocation Button in the Map
			googleMap.setMyLocationEnabled(true);		
			
			// Setting onclick event listener for the map
			googleMap.setOnMapClickListener(new OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng point) {
					
					// Already two locations				
					if(markerPoints.size()>1){
						markerPoints.clear();
						googleMap.clear();					
					}
					
					// Adding new item to the ArrayList
					markerPoints.add(point);				
					
					// Creating MarkerOptions
					MarkerOptions options = new MarkerOptions();
					
					// Setting the position of the marker
					options.position(point);
					
					/** 
					 * For the start location, the color of marker is GREEN and
					 * for the end location, the color of marker is RED.
					 */
					if(markerPoints.size()==1){
						options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
					}else if(markerPoints.size()==2){
						options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
					}
								
					
					// Add new marker to the Google Map Android API V2
					googleMap.addMarker(options);
					
					// Checks, whether start and end locations are captured
					if(markerPoints.size() >= 2){					
						LatLng origin = markerPoints.get(0);
						LatLng dest = markerPoints.get(1);
						
						// Getting URL to the Google Directions API
						String url = getDirectionsUrl(origin, dest);				
						
						DownloadTask downloadTask = new DownloadTask();
						
						// Start downloading json data from Google Directions API
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
		
		// Origin of route
		String str_origin = "origin="+origin.latitude+","+origin.longitude;
		
		// Destination of route
		String str_dest = "destination="+dest.latitude+","+dest.longitude;		
		
					
		// Sensor enabled
		String sensor = "sensor=false";			
					
		// Building the parameters to the web service
		String parameters = str_origin+"&"+str_dest+"&"+sensor;
					
		// Output format
		String output = "json";
		
		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
		
		
		return url;
	}
	
	/** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url 
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url 
                urlConnection.connect();

                // Reading data from url 
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
                Log.d("Exception while downloading url", e.toString());
        }finally{
                iStream.close();
                urlConnection.disconnect();
        }
        return data;
     }

	
	
	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String>{			
				
		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {
				
			// For storing data from web service
			String data = "";
					
			try{
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			}catch(Exception e){
				Log.d("Background Task",e.toString());
			}
			return data;		
		}
		
		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);			
			
			ParserTask parserTask = new ParserTask();
			
			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
				
		}		
	}
	
	/** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
    	
    	// Parsing the data in non-ui thread    	
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
			
			JSONObject jObject;	
			List<List<HashMap<String, String>>> routes = null;			           
            
            try{
            	jObject = new JSONObject(jsonData[0]);
            	JSONParser parser = new JSONParser();
            	
            	// Starts parsing data
            	routes = parser.parse(jObject);    
            }catch(Exception e){
            	e.printStackTrace();
            }
            return routes;
		}
		
		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();
			
			// Traversing through all the routes
			for(int i=0;i<result.size();i++){
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();
				
				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);
				
				// Fetching all the points in i-th route
				for(int j=0;j<path.size();j++){
					HashMap<String,String> point = path.get(j);					
					
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);	
					
					points.add(position);						
				}
				
				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(2);
				lineOptions.color(Color.RED);	
				
			}
			
			// Drawing polyline in the Google Map for the i-th route
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
