package com.app.studiodemo.twitterdemo;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TwitterApplication extends Application {
	private static final String TAG="WightApplication";
	private Map<String, String> name = new HashMap<String, String>();
	@Override
	public void onCreate() {
		super.onCreate();
		initImageLoder(this);
	}
	/**
	 * 初始化ImageLoader
	 * @param context
	 */
	private void initImageLoder(Context context) {
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				context);
		builder.diskCache(new UnlimitedDiscCache(StorageUtils
				.getCacheDirectory(context), null, new FileNameGenerator() {
			@Override
			public String generate(String imageUri) {
				if (!name.containsKey(imageUri)) {
					name.put(imageUri, UUID.randomUUID().toString());
				}
				return name.get(imageUri);
			}
		}));
		builder.threadPriority(Thread.NORM_PRIORITY - 2);
		builder.tasksProcessingOrder(QueueProcessingType.LIFO);
		builder.discCacheFileNameGenerator(new Md5FileNameGenerator());
		builder.denyCacheImageMultipleSizesInMemory();
		DisplayImageOptions.Builder dopts = new DisplayImageOptions.Builder();
		dopts.showImageForEmptyUri(R.drawable.sns_twitter); // 设置图片Uri为空或是错误的时候显示的图片
		dopts.showImageOnFail(R.drawable.sns_twitter);
		dopts.showImageOnLoading(R.drawable.sns_twitter);// 设置图片下载期间显示的图片
		dopts.cacheInMemory(true); // 设置下载的图片是否缓存在内存中
		dopts.cacheOnDisk(true); // 设置下载的图片是否缓存在SD卡中
		builder.defaultDisplayImageOptions(dopts.build());
		ImageLoader.getInstance().init(builder.build());
		ImageLoader.getInstance().clearMemoryCache();
		ImageLoader.getInstance().clearDiskCache();
	}
}
