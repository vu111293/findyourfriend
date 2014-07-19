package com.sgu.findyourfriend.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.model.FriendRequest;

public class CustomAdapterFriendRequests extends ArrayAdapter<FriendRequest> {

	private Context context;
	private List<FriendRequest> Data = new ArrayList<FriendRequest>();
	private int LayoutResID;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	@SuppressWarnings("deprecation")
	public CustomAdapterFriendRequests(Context context, int resource,
			ArrayList<FriendRequest> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.LayoutResID = resource;
		this.Data = objects;

		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		imageLoader = ImageLoader.getInstance();
//		File cacheDir = StorageUtils.getCacheDirectory(context);
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
	public View getView(int position, View view, ViewGroup parent) {

		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		final View convertView = inflater.inflate(LayoutResID, null);
		final FriendRequest fr = Data.get(position);

		imageLoader.displayImage(fr.getImgProfile(),
				(ImageView) convertView.findViewById(R.id.imgProfile), options);

		((TextView) convertView.findViewById(R.id.txtName)).setText(fr
				.getName());

		if (fr.isNotNow()) {
			((ImageView) convertView.findViewById(R.id.imgNotNow))
					.setVisibility(View.VISIBLE);
			((Button) convertView.findViewById(R.id.btnNotNow))
					.setVisibility(View.GONE);
		} else {
			((ImageView) convertView.findViewById(R.id.imgNotNow))
					.setVisibility(View.GONE);
			((Button) convertView.findViewById(R.id.btnNotNow))
					.setVisibility(View.VISIBLE);
		}

		((Button) convertView.findViewById(R.id.btnNotNow))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// request to server
						fr.setNotNow(true);
						((ImageView) convertView.findViewById(R.id.imgNotNow))
								.setVisibility(View.VISIBLE);
						((Button) convertView.findViewById(R.id.btnNotNow))
								.setVisibility(View.GONE);
					}
				});

		((Button) convertView.findViewById(R.id.btnConfirm))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// request to server
						Data.remove(fr);
						notifyDataSetChanged();

						Toast.makeText(context, "remove item ",
								Toast.LENGTH_SHORT).show();
					}
				});

		return convertView;

	}

}
