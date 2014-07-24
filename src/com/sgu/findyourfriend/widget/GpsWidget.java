package com.sgu.findyourfriend.widget;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;



public class GpsWidget {

	private Context context;
	
	public GpsWidget(Context context) {
		this.context = context;
	}
	
	public String getNow() {
		return System.currentTimeMillis() + "";
	}

	public String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
		String strAdd = "";
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		try {
			List<Address> addresses = geocoder.getFromLocation(LATITUDE,
					LONGITUDE, 1);
			if (addresses != null) {
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder("");

				for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
					strReturnedAddress
							.append(returnedAddress.getAddressLine(i)).append(
									"\n");
				}
				strAdd = strReturnedAddress.toString();
				Log.w("My Current loction address",
						"" + strReturnedAddress.toString());
			} else {
				Log.w("My Current loction address", "No Address returned!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.w("My Current loction address", "Canont get Address!");
		}
		return strAdd;
	}

}
