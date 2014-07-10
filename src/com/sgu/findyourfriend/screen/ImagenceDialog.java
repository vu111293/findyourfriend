package com.sgu.findyourfriend.screen;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.sgu.findyourfriend.R;

public class ImagenceDialog extends Dialog {

	private TextView txtTicker;
	private Button btnCancel;
	private int counter;
	private Vibrator vibrator;

	public ImagenceDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.alert_dialog_custom);
		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);

		txtTicker = (TextView) findViewById(R.id.txtTicker);
		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});

		// new TickerAsyntask().execute();

		counter = 10;

		final Handler handler = new Handler();
		final Runnable r = new Runnable() {
			public void run() {
				if (counter >= 0) {
					txtTicker.setText(counter + "");

					if (counter > 0)
						vibrator.vibrate(300);
					else
						vibrator.vibrate(800);
					counter--;
					handler.postDelayed(this, 1000);
				} else {
					// excute

					dismiss();
				}

			}
		};

		handler.postDelayed(r, 0);

	}
	private class TickerAsyntask extends AsyncTask<Void, Void, Void> {

		private int counter = 10;

		@Override
		protected void onPreExecute() {

			txtTicker.setText(counter + "");
		}

		@Override
		protected Void doInBackground(Void... params) {

			while (counter > 0) {

				try {
					this.wait(1000);
					counter--;
					publishProgress();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			txtTicker.setText(counter + "");

		}

		@Override
		protected void onPostExecute(Void result) {
			dismiss();
		}
	}

}
