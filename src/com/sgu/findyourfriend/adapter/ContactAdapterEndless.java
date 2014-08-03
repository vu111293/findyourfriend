//package com.sgu.findyourfriend.adapter;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.os.AsyncTask;
//import android.os.SystemClock;
//import android.provider.ContactsContract;
//import android.text.Html;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.commonsware.cwac.endless.EndlessAdapter;
//import com.sgu.findyourfriend.R;
//import com.sgu.findyourfriend.mgr.MyProfileManager;
//import com.sgu.findyourfriend.model.ContactBean;
//import com.sgu.findyourfriend.net.PostData;
//
//public class ContactAdapterEndless extends EndlessAdapter {
//	private List<ContactBean> data;
//	private List<ContactBean> fullContacts;
//	private List<ContactBean> temptData;
//	private Context ctx;
//
//	private static final int NUMBER_CACHE = 20;
//	private int idCacheCurrent = 0;
//
//	public ContactAdapterEndless(final Context ctx, final int rowLayout,
//			final ArrayList<ContactBean> list) {
//		super(ctx, new ArrayAdapter<ContactBean>(ctx, rowLayout, list) {
//
//			class ViewHolder {
//				public TextView tvname, tvPhoneNo;
//				public ImageView used;
//
//			}
//
//			@Override
//			public View getView(int position, View convertView, ViewGroup parent) {
//				View view = convertView;
//				ViewHolder holder;
//				ContactBean objBean;
//
//				if (view == null) {
//					LayoutInflater inflater = (LayoutInflater) ctx
//							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//					view = inflater.inflate(rowLayout, null);
//
//					holder = new ViewHolder();
//					view.setTag(holder);
//				} else {
//					holder = (ViewHolder) view.getTag();
//				}
//
//				if ((list == null) || ((position + 1) > list.size()))
//					return view;
//
//				objBean = list.get(position);
//
//				holder.tvname = (TextView) view.findViewById(R.id.txtName);
//				holder.tvPhoneNo = (TextView) view.findViewById(R.id.txtPhoneNumber);
//				holder.used = (ImageView) view
//						.findViewById(R.id.ImageView_AddFriendFromContact_Used);
//				if (holder.tvname != null && null != objBean.getName()
//						&& objBean.getName().trim().length() > 0) {
//					holder.tvname.setText(Html.fromHtml(objBean.getName()));
//				}
//				if (holder.tvPhoneNo != null && null != objBean.getPhoneNo()
//						&& objBean.getPhoneNo().trim().length() > 0) {
//					holder.tvPhoneNo
//							.setText(Html.fromHtml(objBean.getPhoneNo()));
//				}
//				if (!objBean.isUsed()) {
//					holder.used.setVisibility(View.GONE);
//				}
//				return view;
//			}
//
//		}, R.layout.pending);
//
//		this.ctx = ctx;
//		this.data = list;
//
//		// init fullContacts
//		fullContacts = new ArrayList<ContactBean>();
//		Cursor phones = ctx.getContentResolver().query(
//				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
//				null, null);
//		while (phones.moveToNext()) {
//			String name = phones
//					.getString(phones
//							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//
//			String phoneNumber = phones
//					.getString(phones
//							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//			fullContacts.add(new ContactBean(name, phoneNumber, false));
//		}
//		phones.close();
//
//	}
//
//	@Override
//	protected boolean cacheInBackground() {
//
//		SystemClock.sleep(10000); // pretend to do work
//
//		temptData = new ArrayList<ContactBean>();
//		(new AsyncTask<Void, Void, HashMap<String, Integer>>() {
//
//			private ArrayList<String> numContactsList = new ArrayList<String>();
//			private ArrayList<String> nameContactsList = new ArrayList<String>();
//
//			@Override
//			protected void onPreExecute() {
//				int maxCache = Math.min(fullContacts.size(), idCacheCurrent
//						+ NUMBER_CACHE);
//				for (int i = idCacheCurrent; i < maxCache; ++i) {
//					numContactsList.add(fullContacts.get(i).getPhoneNo());
//					nameContactsList.add(fullContacts.get(i).getName());
//				}
//
//				idCacheCurrent = maxCache;
//			}
//
//			@Override
//			protected HashMap<String, Integer> doInBackground(Void... arg0) {
//
//				return PostData.userGetUserListWithoutFriend(ctx,
//						MyProfileManager.getInstance().mine.getId(),
//						numContactsList);
//			}
//
//			@Override
//			protected void onPostExecute(HashMap<String, Integer> result) {
//				boolean isUsed;
//				
//				for (String key : result.keySet()) {
//					Log.i("@@@@@@@", key + " # ok");
//				}
//				
//				for (int i = 0; i < numContactsList.size(); ++i) {
//					isUsed = result.containsKey(numContactsList.get(i));
//					temptData.add(new ContactBean(nameContactsList.get(i), numContactsList.get(i), isUsed));
//					
//					Log.i("GET LIST", nameContactsList.get(i) + " " + numContactsList.get(i) + " " + isUsed);
//				}
//				Log.i("GETLIST", "appendcache data! size " + temptData.size());
//				ArrayAdapter<ContactBean> a = (ArrayAdapter<ContactBean>) getWrappedAdapter();
//				for (ContactBean cb : temptData) {
//					a.add(cb);
//				}
//			}
//
//		}).execute();
//
//		return true;
//
//	}
//
//	@Override
//	protected void appendCachedData() {
//		Log.i("GETLIST", "appendcache data!");
////		if (null != temptData) {
////			Log.i("GETLIST", "appendcache data! size " + temptData.size());
////			
////			
////
////			for (ContactBean cb : temptData) {
////				a.add(cb);
////			}
////		}
//		
////		if (getWrappedAdapter().getCount() < 75) {
////			@SuppressWarnings("unchecked")
////			ArrayAdapter<ContactBean> a = (ArrayAdapter<ContactBean>) getWrappedAdapter();
////
////			for (int i = 0; i < 25; i++) {
////				a.add(new ContactBean(i + "", i + "", false));
////			}
////		}
//	}
//
//}
