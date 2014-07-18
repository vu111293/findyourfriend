package com.sgu.findyourfriend.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.model.Friend;

public class CustomAdapter_FriendStatus extends ArrayAdapter<Friend> {
	int LayoutResID;
	Context ctx;
	List<Friend> DataList = new ArrayList<Friend>();
	ImageLoader imageLoader;
	DisplayImageOptions options;

	public CustomAdapter_FriendStatus(Context context, int resource,
			List<Friend> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		ctx = context;
		LayoutResID = resource;
		DataList = objects;

		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		imageLoader = ImageLoader.getInstance();
		File cacheDir = StorageUtils.getCacheDirectory(context);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.memoryCacheExtraOptions(480, 800)
				// default = device screen dimensions

				.taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
				.taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR)
				.threadPoolSize(3)
				// default
				.threadPriority(Thread.NORM_PRIORITY - 1)
				// default
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				// default
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				// default
				.memoryCacheSize(2 * 1024 * 1024)
				.imageDownloader(new BaseImageDownloader(context)) // default
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
				.build();
		imageLoader.init(config);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
		convertView = inflater.inflate(LayoutResID, null);
		TextView name = (TextView) convertView
				.findViewById(R.id.txtName);
		TextView phoneNumber = (TextView) convertView
				.findViewById(R.id.txtPhoneNumber);
		ImageView imgProfile = (ImageView) convertView
				.findViewById(R.id.imgProfile);
		ImageView imgStatusBar = (ImageView) convertView
				.findViewById(R.id.imgStatusBar);
		TextView txtStatusText = (TextView) convertView.findViewById(R.id.txtStatusText);
		ImageView imgNoneAccept = (ImageView) convertView.findViewById(R.id.imgNoneAccept);
		
		
		Friend fr = DataList.get(position);
		
		
		name.setText(fr.getUserInfo().getName());
		phoneNumber.setText(fr.getNumberLogin().get(0));
		imageLoader.displayImage(fr.getUserInfo().getAvatar(), imgProfile, options);
		
		// set img
		if (fr.isAccepted()) {
			imgNoneAccept.setVisibility(View.GONE);
			if (fr.isAvailable()) {
				if (fr.isShare()) {
					imgStatusBar.setBackgroundColor(ctx.getResources().getColor(R.color.share));
					txtStatusText.setText("chia sẻ");
				} else {
					imgStatusBar.setBackgroundColor(ctx.getResources().getColor(R.color.online));
					txtStatusText.setText("trực tuyến");
				}
			} else {
				imgStatusBar.setBackgroundColor(ctx.getResources().getColor(R.color.offine));
				txtStatusText.setText("đang ẩn");
			}
			
		} else {
			imgNoneAccept.setVisibility(View.VISIBLE);
			imgStatusBar.setBackgroundColor(ctx.getResources().getColor(R.color.none_accept));
			txtStatusText.setText("chờ chấp nhận");
		}
		return convertView;
	}

}
