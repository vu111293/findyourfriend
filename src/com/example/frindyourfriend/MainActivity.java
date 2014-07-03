package com.example.frindyourfriend;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity  {

	

	
	 TextView   TitleTextView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    ActionBar();
		LoadTabHost();
		
		
	}
	  private void displayView(int position) {
	        // update the main content by replacing fragments
	        Fragment fragment = null;
	        switch (position) {
	        case 0:
	            fragment = new MapActivity();
	            break;
	        case 1:
	            fragment = new MessageActivity();
	            break;
	        case 2:
	            fragment = new HistoryActivity();
	            break;
	        case 3:
	            fragment = new FriendRequestsActivity();
	            break;
	        case 4:
	            fragment = new CategoriesActivity();
	            break;
	        default:
	            break;
	        }
	 
	        if (fragment != null) {
	            android.app.FragmentManager fragmentManager = getFragmentManager();
	            fragmentManager.beginTransaction()
	                    .replace(android.R.id.tabcontent, fragment).commit();
	 
	            
	        } else {
	            // error in creating fragment
	            Log.e("MainActivity", "Error in creating fragment");
	        }
	    }
	public void ActionBar(){
		 ActionBar mActionBar = getActionBar();
	        mActionBar.setDisplayShowHomeEnabled(false);
	        mActionBar.setDisplayShowTitleEnabled(false);
	        LayoutInflater mInflater = LayoutInflater.from(this);
	 
	        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
	         TitleTextView = (TextView) mCustomView.findViewById(R.id.TextView_Title);
	        TitleTextView.setText("Map");
	        mActionBar.setCustomView(mCustomView);
	        mActionBar.setDisplayShowCustomEnabled(true);
	}
	// cau hinh TABHOST
	public void LoadTabHost(){
		 
        
        
		final TabHost tab=(TabHost)findViewById(android.R.id.tabhost);
		tab.setup();
		TabHost.TabSpec spec;
		// tab Map
		
		spec=tab.newTabSpec("Map");
		spec.setContent(R.id.tab1);
		spec.setIndicator("",getResources().getDrawable(R.drawable.ic_action_about));
		tab.addTab(spec);
		
		
		// tab Mess
		tab.invalidate();
		spec=tab.newTabSpec("Mess");
		spec.setContent(R.id.tab2);
		spec.setIndicator("",getResources().getDrawable(R.drawable.ic_action_camera));
		tab.addTab(spec);
		
		
		// tab his
		tab.invalidate();
		spec=tab.newTabSpec("History");

		spec.setContent(R.id.tab3);
		spec.setIndicator("",getResources().getDrawable(R.drawable.ic_action_cloud));
		tab.addTab(spec);
		
		
		// tab Contact
		tab.invalidate();
		spec=tab.newTabSpec("Contact");

		spec.setContent(R.id.tab4);
		spec.setIndicator("",getResources().getDrawable(R.drawable.ic_action_email));
		tab.addTab(spec);
	
		
		// danh muc categories
		spec=tab.newTabSpec("Categories");
		spec.setContent(R.id.tab5);
		spec.setIndicator("",getResources().getDrawable(R.drawable.ic_action_settings));
		tab.addTab(spec);
		
		
		
		
		tab.setCurrentTab(0);
		
		

		tab.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				int i = tab.getCurrentTab();

				 if(i==0) {
				        //destroy earth
					 TitleTextView.setText("Map");
					 displayView(0);
				    }
				 if(i==1) {
				        //destroy earth
					 TitleTextView.setText("Message");
					 displayView(1);
				    }
				 if(i==2) {
				        //destroy earth
					 TitleTextView.setText("History");
					 displayView(2);
				    }
				 if(i==3) {
				        //destroy earth
					 TitleTextView.setText("Friend Requests");
					 displayView(3);
				    }
				 if(i==4) {
				        //destroy earth
					 TitleTextView.setText("More");
					 displayView(4);
				    }
				  
			}
		});
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
