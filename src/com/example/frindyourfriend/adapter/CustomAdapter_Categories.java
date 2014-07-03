package com.example.frindyourfriend.adapter;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frindyourfriend.R;
import com.example.frindyourfriend.model.Category;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class CustomAdapter_Categories extends ArrayAdapter<Category>{
	int LayoutID;
	Context ctx;
	List<Category> Data;
	 ImageLoader imageLoader;
	 DisplayImageOptions options;

	public CustomAdapter_Categories(Context context, int resource, List<Category> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.LayoutID=resource;
		this.ctx=context;
		Data=objects;
		 
		 options = new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
		 imageLoader=ImageLoader.getInstance();
		File cacheDir = StorageUtils.getCacheDirectory(context);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		        .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
		        .discCacheExtraOptions(480, 800, null)
		        .taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
		        .taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR)
		        .threadPoolSize(3) // default
		        .threadPriority(Thread.NORM_PRIORITY - 1) // default
		        .tasksProcessingOrder(QueueProcessingType.FIFO) // default
		        .denyCacheImageMultipleSizesInMemory()
		        .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // default
		        .memoryCacheSize(2 * 1024 * 1024)
		        .discCache(new UnlimitedDiscCache(cacheDir)) // default
		        .discCacheSize(50 * 1024 * 1024)
		        .discCacheFileCount(100)
		        .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
		        .imageDownloader(new BaseImageDownloader(context)) // default
		        .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
		        .build();
		imageLoader.init(config);
		           
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater=((Activity)ctx).getLayoutInflater();
		convertView=inflater.inflate(LayoutID,null);
		// khoi tao
		Item_Categories item=new Item_Categories();
		item.ItemName=(TextView)convertView.findViewById(R.id.TextView_Categories_item_ItemName);
		item.title=(TextView)convertView.findViewById(R.id.TextView_Categories_Title);
		item.number=(TextView)convertView.findViewById(R.id.TextView_Categories_item_Number);
		item.icon=(ImageView)convertView.findViewById(R.id.ImageView_Categories_item_icon);
		item.headerLayout=(LinearLayout)convertView.findViewById(R.id.HeaderLayout);
		item.itemLayout=(LinearLayout)convertView.findViewById(R.id.ItemLayout);
		// lay phan tu 
		Category cate=Data.get(position);
		
		if(cate.getTitle()!=null){
			item.itemLayout.setVisibility(LinearLayout.GONE);
			item.title.setText(cate.getTitle());
		}
		if(cate.getName()!=null){
			item.headerLayout.setVisibility(LinearLayout.GONE);
			item.ItemName.setText(cate.getName());
			imageLoader.displayImage(cate.getImgResID(), item.icon, options);
			if(cate.getNews()!=0){
				item.number.setText(String.valueOf(cate.getNews()));
				//Toast.makeText(ctx, String.valueOf(cate.getNews()), Toast.LENGTH_LONG).show();
			}
			else
			{
				item.number.setVisibility(TextView.GONE);
			}

		}
		
		return convertView;
		
	}
	private  class Item_Categories {
		TextView ItemName, title,number;
		ImageView icon;
		LinearLayout headerLayout, itemLayout;
	}
}
