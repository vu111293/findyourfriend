package com.sgu.findyourfriend.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import android.content.Context;
import android.util.Log;

public class PostManager {
	
	private static final String TAG = PostManager.class.getName();

	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();
	
	public static String tryPostWithJsonResult(Context context, String serverUrl,
			Map<String, String> params) {
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		String jsonResponse = "";
		// Once GCM returns a registration id, we need to register on our server
		// As the server might be down, we will retry it a couple times.
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(TAG, "Attempt #" + i + " to register");
			try {
				jsonResponse = post_response(serverUrl, params);
				break;
			} catch (IOException e) {
				Log.e(TAG, "Failed to send on attempt " + i + ":" + e);
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {

					Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
					Thread.sleep(backoff);

				} catch (InterruptedException e1) {
					Log.d(TAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return "";
				}
				backoff *= 2;
			}
		}

		return jsonResponse;
	}

	private static String post_response(String endpoint,
			Map<String, String> params) throws IOException {

		URL url;
		try {

			url = new URL(endpoint);

		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}

		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();

		// constructs the POST body using the parameters
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=')
					.append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}

		String body = bodyBuilder.toString();
		String parsedString = "";

		Log.v("", "Posting '" + body + "' to " + url);

		byte[] bytes = body.getBytes();

		HttpURLConnection conn = null;

		try {

			Log.e("URL", "> " + url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setAllowUserInteraction(false);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestMethod("POST");
			conn.connect();

			Log.v(TAG, "Connect OK");

			// post the request
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			out.close();

			// post response
			InputStream is = conn.getInputStream();
			parsedString = convertinputStreamToString(is);

			Log.v(TAG, "Get Stream OK");

			// handle the response
			int status = conn.getResponseCode();

			// If response is not success
			if (status != 200) {

				throw new IOException("Post failed with error code " + status);
			}
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		// Log.v(TAG, parsedString);
		return parsedString;
	}

	public static String convertinputStreamToString(InputStream ists)
			throws IOException {
		if (ists != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader r1 = new BufferedReader(new InputStreamReader(
						ists, "UTF-8"));
				while ((line = r1.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				ists.close();
			}
			return sb.toString();
		} else {
			return "";
		}
	}
}
