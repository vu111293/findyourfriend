package com.sgu.findyourfriend.screen;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;

import com.sgu.findyourfriend.R;

public class OptionsDialog extends Dialog {

	private String[] opts = { "Đến bản đổ", "Gọi", "Nhắn tin",
			"Yêu cầu chia sẻ" };

	private Context context;
	private TabHost mTabHostControl;

	private ListView lvOpt;
	private ArrayAdapter<String> adapter;
	private int itemId;

	public OptionsDialog(Context context, TabHost mTabHost, int itemId) {
		super(context);
		this.context = context;
		this.mTabHostControl = mTabHost;
		this.itemId = itemId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.option_list_dialog);
		// getWindow().setLayout(LayoutParams.FILL_PARENT,
		// LayoutParams.FILL_PARENT);

		setTitle("Tùy chọn");
		
		lvOpt = (ListView) findViewById(R.id.lvOptions);
		adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_1, android.R.id.text1, opts);
		lvOpt.setAdapter(adapter);

		
		lvOpt.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long arg3) {

				switch (pos) {
				case 0:
					// go to map
					mTabHostControl.setCurrentTabByTag(MainActivity.MAP_TAG);
					
					
					break;
				case 1:
					break;
					
					
				default:
					break;
				}
			}
		});
		
		((Button) findViewById(R.id.btnCancel))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dismiss();
					}
				});

	}

}
