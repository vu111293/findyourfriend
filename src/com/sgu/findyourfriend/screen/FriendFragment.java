package com.sgu.findyourfriend.screen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.sgu.findyourfriend.Config;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mem.FriendJSONParser;
import com.sgu.findyourfriend.mem.FriendListAdapter;
import com.sgu.findyourfriend.model.Friend;

public class FriendFragment extends Fragment {

	public static String TAG = "Friend List Acvivity";

	private Context context;
	private Activity activity;
	private List<Friend> mFriendList;
	private ListView mListView;
	private FriendListAdapter mAdapter;

	private List<Friend> friendsList = new ArrayList<Friend>();

	private enum TASK_TYPE {
		PHONE_CALL, GO_MAP, SMS, SHARE
	}

	private String[] taskString = { "Phone call", "Go to map", "Message",
			"Share" };

	public FriendFragment() {
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_friend, container,
				false);

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity.getApplicationContext();
		this.activity = activity;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		DownloadTask downloadTask = new DownloadTask();
		downloadTask.execute(Config.GET_FRIENDS_SERVER_URL);
		mListView = (ListView) view.findViewById(R.id.friendListView);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("Choose task");
				builder.setItems(taskString, dialogClickListener);
				builder.setNegativeButton("Cancel", dialogClickListener).show();
			}
		});
	}

	DialogInterface.OnClickListener dialogClickListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			MainActivity mac = ((MainActivity)getActivity());
			
			// Intent i;
			switch (which) {
			case 0:
				Intent i = new Intent(Intent.ACTION_CALL);
				i.setData(Uri.parse("tel:"
						+ friendsList.get(0).getPhoneNumber()));
				startActivity(i);
				break;

			case 1:
				/* i = new Intent(context, LocationActivity.class);
				Log.i("Map: ", friendsList.get(0).getRegId());
				i.putExtra("gcmId", friendsList.get(0).getRegId());
				startActivity(i);*/
				mac.displayView(MainActivity.FRAGMENT_SCREEN.MAP.getValue());
				
				break;

			case 2:
				/* i = new Intent(context, MessageActivity.class);
				Log.i("Message: ", "");
				i.putExtra("receiver", friendsList.get(0).getRegId());
				startActivity(i);*/
				mac.displayView(MainActivity.FRAGMENT_SCREEN.MESSAGE.getValue());
				break;

			case 3:
				Log.i(TAG, "Share task");
				break;

			default:
				break;
			}
		}
	};

	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
		}

		return data;
	}

	private class DownloadTask extends AsyncTask<String, Integer, String> {
		String data = null;

		@Override
		protected String doInBackground(String... url) {
			try {
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {

			// The parsing of the xml data is done in a non-ui thread
			ListViewLoaderTask listViewLoaderTask = new ListViewLoaderTask();

			// Start parsing xml data
			listViewLoaderTask.execute(result);
		}
	}

	/** AsyncTask to parse json data and load ListView */
	private class ListViewLoaderTask extends
			AsyncTask<String, Void, SimpleAdapter> {

		JSONObject jObject;

		// Doing the parsing of xml data in a non-ui thread
		@Override
		protected SimpleAdapter doInBackground(String... strJson) {
			try {
				jObject = new JSONObject(strJson[0]);
				FriendJSONParser friendJsonParser = new FriendJSONParser();
				friendJsonParser.parse(jObject);
			} catch (Exception e) {
				Log.d("JSON Exception1", e.toString());
			}

			// Instantiating json parser class
			FriendJSONParser friendJsonParser = new FriendJSONParser();

			// A list object to store the parsed countries list
			List<HashMap<String, Object>> friends = null;

			try {
				// Getting the parsed data as a List construct
				friends = friendJsonParser.parse(jObject);
			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}

			/*
			 * remove avartar profile // Keys used in Hashmap String[] from = {
			 * "name", "avatarLink", "phoneNumber" };
			 * 
			 * // Ids of views in listview_layout int[] to = {
			 * R.id.txtMemberName, R.id.imgProfile, R.id.txtPhoneNumber };
			 */

			// Keys used in Hashmap
			String[] from = { "name", "phoneNumber" };

			// Ids of views in listview_layout
			int[] to = { R.id.txtMemberName, R.id.txtPhoneNumber };

			// Instantiating an adapter to store each items
			// R.layout.listview_layout defines the layout of each item
			SimpleAdapter adapter = new SimpleAdapter(context,
					friends, R.layout.item_friend, from, to);

			List<Friend> friendsLs = new ArrayList<Friend>();
			// fetch data
			for (HashMap<String, Object> i : friends) {
				friendsLs.add(new Friend(i.get("name").toString(), i.get(
						"email").toString(), i.get("phoneNumber").toString(), i
						.get("avatarLink").toString(), i.get("gcmId")
						.toString(), i.get("isAvailable").toString()
						.equals("true")));
			}

			friendsList = friendsLs;

			return adapter;
		}

		/** Invoked by the Android on "doInBackground" is executed */
		@Override
		protected void onPostExecute(SimpleAdapter adapter) {

			// Setting adapter for the listview
			mListView.setAdapter(adapter);

			for (int i = 0; i < adapter.getCount(); i++) {
				HashMap<String, Object> hm = (HashMap<String, Object>) adapter
						.getItem(i);
				// String imgUrl = "http://" + (String) hm.get("avatarLink");
				String imgUrl = "http://wikidpad.sourceforge.net/help/html/demo/files/demo/bubbles64.png";
				ImageLoaderTask imageLoaderTask = new ImageLoaderTask();

				HashMap<String, Object> hmDownload = new HashMap<String, Object>();
				hm.put("avatarLink", imgUrl);
				hm.put("position", i);

				// Starting ImageLoaderTask to download and populate image in
				// the listview
				// imageLoaderTask.execute(hm);
			}
		}
	}

	/** AsyncTask to download and load an image in ListView */
	private class ImageLoaderTask extends
			AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {

		@Override
		protected HashMap<String, Object> doInBackground(
				HashMap<String, Object>... hm) {

			InputStream iStream = null;
			String imgUrl = (String) hm[0].get("avatarLink");
			int position = (Integer) hm[0].get("position");

			URL url;
			try {
				url = new URL(imgUrl);

				// Creating an http connection to communicate with url
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();

				// Connecting to url
				urlConnection.connect();

				// Reading data from url
				iStream = urlConnection.getInputStream();

				// Getting Caching directory
				// File cacheDirectory = getBaseContext().getCacheDir();

				// Temporary file to store the downloaded image
				// File tmpFile = new File(cacheDirectory.getPath() + "/wpta_"
				// + position + ".png");

				// Log.i("DL", tmpFile.getPath());

				// -----------------------------------------------------------------------------------------------------------------------------------------------
				// -------------------------- can't load image from http : error
				// esolveUri failed on bad bitmap uri:
				// http://wikidpad.sou...-----------------------
				// -----------------------------------------------------------------------------------------------------------------------------------------------

				File tmpFile = null;
				// cache image
				InputStream inp = url.openStream();
				try {
					File cacheDirectory = context.getCacheDir();
					tmpFile = new File(cacheDirectory, "/wpta_" + position
							+ ".png");
					OutputStream out = new FileOutputStream(tmpFile);
					try {
						byte[] buffer = new byte[1024];
						int bytesRead = 0;
						while ((bytesRead = inp.read(buffer, 0, buffer.length)) >= 0) {
							out.write(buffer, 0, bytesRead);
						}

					} finally {
						out.close();
					}
				} finally {
					inp.close();

				}

				Log.i("DL", tmpFile.getPath());

				// The FileOutputStream to the temporary file
				// FileOutputStream fOutStream = new FileOutputStream(tmpFile);

				// Creating a bitmap from the downloaded inputstream
				// Bitmap b = BitmapFactory.decodeStream(iStream);

				// Writing the bitmap to the temporary file as png file
				// b.compress(Bitmap.CompressFormat.PNG, 100, fOutStream);

				// Flush the FileOutputStream
				// fOutStream.flush();

				// Close the FileOutputStream
				// fOutStream.close();

				// Create a hashmap object to store image path and its position
				// in the listview
				HashMap<String, Object> hmBitmap = new HashMap<String, Object>();

				// Storing the path to the temporary image file
				hmBitmap.put("avatar", tmpFile.getPath());

				// Storing the position of the image in the listview
				hmBitmap.put("position", position);

				// Returning the HashMap object containing the image path and
				// position
				return hmBitmap;

			} catch (Exception e) {
				Log.i(TAG, "Message " + e.getMessage());

				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			// Getting the path to the downloaded image
			String path = (String) result.get("avatar");

			// Getting the position of the downloaded image
			int position = (Integer) result.get("position");

			// Getting adapter of the listview
			SimpleAdapter adapter = (SimpleAdapter) mListView.getAdapter();

			// Getting the hashmap object at the specified position of the
			// listview
			HashMap<String, Object> hm = (HashMap<String, Object>) adapter
					.getItem(position);

			// Overwriting the existing path in the adapter
			hm.put("avatar", path);

			// Noticing listview about the dataset changes
			adapter.notifyDataSetChanged();
		}
	}
}
