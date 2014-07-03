package com.example.frindyourfriend;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MapActivity extends Fragment {
	  private GoogleMap googleMap;
	  View view;
	  public MapActivity(){
		  
		  try {
	            // Loading map
	          initilizeMap();
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	  }
	     
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	    	
	    	if (view != null) {
	    	    ViewGroup parent = (ViewGroup) view.getParent();
	    	    if (parent != null)
	    	        parent.removeView(view);
	    	}
	    	try {
	    	    view = inflater.inflate(R.layout.activity_map, container, false);
	    	    try {
		            // Loading map
		          initilizeMap();
		 
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
	    	} catch (InflateException e) {
	    	    /* map is already there, just return view as it is */
	    	}
	    	
	        return view;
	    }
	
	private void initilizeMap() throws IOException {
        if (googleMap == null) {
        	
        	googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        	// Changing map type
        				googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        			
        				// Showing / hiding your current location
        				googleMap.setMyLocationEnabled(true);

        				// Enable / Disable zooming controls
        				googleMap.getUiSettings().setZoomControlsEnabled(false);

        				// Enable / Disable my location button
        				googleMap.getUiSettings().setMyLocationButtonEnabled(true);     
        				
        				CameraPosition cameraPosition = new CameraPosition.Builder().target(
        						new LatLng(17.385044, 78.486671)).zoom(12).build();

        		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        			

 
           
        }
    }
	public  double[] getGPS() throws IOException {
		 LocationManager lm = (LocationManager)view.getContext().getSystemService(Context.LOCATION_SERVICE);
		 List<String> providers = lm.getProviders(true);

		 Location l = null;

		 for (int i=providers.size()-1; i>=0; i--) {
		  l = lm.getLastKnownLocation(providers.get(i));
		  if (l != null) break;
		 }

		 double[] gps = new double[2];
		 if (l != null) {
		  gps[0] = l.getLatitude();
		  gps[1] = l.getLongitude();
		 }
		 //GetAd(  gps[0],  gps[1]);
		 return gps;
		}
}
