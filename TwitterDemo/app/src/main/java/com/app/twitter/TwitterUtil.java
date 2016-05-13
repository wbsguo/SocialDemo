package com.app.twitter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import java.util.ArrayList;

import twitter4j.IDs;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterUtil {
	private static final String TAG="TwitterUtil";
	private static final int SUCCESS = 100;
	private static final int FAILED = -100;
	private static final int FRIEND_SUCCESS = 200;
	private static final int ID_SUCCESS = 300;
	private static final int NAME_SUCCESS = 400;
	private SharedPreferences preferences;
	private static TwitterUtil twitterUtil = null;
	private Twitter twitter;
	private RequestToken requestToken;
	private AccessToken accessToken;
	
	private Handler getHandler(final OnTwitterCallBackListener listener){
		Handler handler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case SUCCESS:
					listener.onSuccess();
					break;

				case FAILED:
					listener.onError();
					break;
				case FRIEND_SUCCESS:
					ArrayList<String> id_list = (ArrayList<String>) msg.obj;
					listener.onFriends(id_list);
					break;
				case ID_SUCCESS:
					Long id = (Long) msg.obj;
					listener.onSelfId(id);
					break;
				case NAME_SUCCESS:
					break;
				}
			};
		};
		return handler;
	}

	private TwitterUtil(Context ctx) {
		preferences = ctx.getSharedPreferences("twitter", Context.MODE_PRIVATE);
	}

	public static TwitterUtil getInstance(Context ctx) {
		if (twitterUtil == null) {
			twitterUtil = new TwitterUtil(ctx);
		}

		return twitterUtil;
	}

	/**
	 * 初始化twitter
	 * 
	 * @param twitter_consumer_key
	 * @param twitter_consumer_secret
	 */
	public void InitializeTwitter(String twitter_consumer_key,
			String twitter_consumer_secret) {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(twitter_consumer_key); // consumer_key
		builder.setOAuthConsumerSecret(twitter_consumer_secret); // consumer_secret
		Configuration conf = builder.build();
		twitter = new TwitterFactory(conf).getInstance();
		twitter.setOAuthAccessToken(null);
	}

	/**
	 * 获取Twitter requestToken
	 */
	public void getRequestToken(final OnTwitterCallBackListener listener) {
		InitializeTwitter(TwitterConstant.TWITTER_CONSUMER_KEY, TwitterConstant.TWITTER_CONSUMER_SECRET);
		twitter.setOAuthAccessToken(null);
		final Handler handler = getHandler(listener);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					requestToken = twitter.getOAuthRequestToken("www.baidu.com");
					handler.sendEmptyMessage(SUCCESS);
				} catch (TwitterException e) {
					// 获取requestToken失败
					Log.v(TAG, "获取requestToken失败" + e.getMessage());
					listener.onError();
					handler.sendEmptyMessage(FAILED);
					e.printStackTrace();
				}

			}
		}).start();

	}

	public void goToAuthorization(Activity activity, int requestCode) {
		Log.e(TAG, "登录twitter"+requestToken.getAuthorizationURL());
		Intent i = new Intent(activity, TwitterWebsiteActivity.class);
		i.putExtra("url", requestToken.getAuthorizationURL());
		activity.startActivityForResult(i, requestCode);
	}

	/**
	 * 获取Twitter AccessToken
	 */
	public void getAccessToken(final String oauth_verifier,final OnTwitterCallBackListener listener) {
		final Handler handler = getHandler(listener);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					accessToken = twitter.getOAuthAccessToken(requestToken,
							oauth_verifier);
					twitter.setOAuthAccessToken(accessToken);
					preferences
							.edit()
							.putString(TwitterConstant.ACCESS_TOKEN_KEY, accessToken.getToken())
							.putString(TwitterConstant.ACCESS_TOKEN_SECRET,
									accessToken.getTokenSecret()).commit();
					handler.sendEmptyMessage(SUCCESS);
				} catch (TwitterException e) {
					handler.sendEmptyMessage(FAILED);
					e.printStackTrace();
				}
				
			}
		}).start();
	}
	
	/**
	 * 获取用户昵称
	 */
	public void getScreenName(final OnTwitterCallBackListener listener){
		String accessToken = preferences.getString(TwitterConstant.ACCESS_TOKEN_KEY, "");
		String accessTokenSecret = preferences.getString(TwitterConstant.ACCESS_TOKEN_SECRET,
				"");
		if (twitter == null) {
			InitializeTwitter(TwitterConstant.TWITTER_CONSUMER_KEY, TwitterConstant.TWITTER_CONSUMER_SECRET);
		}
		twitter.setOAuthAccessToken(new AccessToken(accessToken,
				accessTokenSecret));
		final Handler handler = getHandler(listener);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					String display_name = twitter.getScreenName();
					String imageUrl=twitter.showUser(twitter.getId()).getProfileImageURL().toString();
					Log.v(TAG, "头像:"+imageUrl);
					preferences.edit().putString(TwitterConstant.DISPLAY_NAME, display_name).commit();
					preferences.edit().putString(TwitterConstant.DISPLAY_IMAG, imageUrl).commit();
					handler.sendEmptyMessage(SUCCESS);
				} catch (TwitterException e) {
					handler.sendEmptyMessage(FAILED);
					e.printStackTrace();
				}
				
			}
		}).start();
		
	}

	/**
	 * 获取好友id
	 */
	public void getFriends(final OnTwitterCallBackListener listener) {
		String accessToken = preferences.getString(TwitterConstant.ACCESS_TOKEN_KEY, "");
		String accessTokenSecret = preferences.getString(TwitterConstant.ACCESS_TOKEN_SECRET,
				"");
		if (twitter == null) {
			InitializeTwitter(TwitterConstant.TWITTER_CONSUMER_KEY, TwitterConstant.TWITTER_CONSUMER_SECRET);
		}
		twitter.setOAuthAccessToken(new AccessToken(accessToken,
				accessTokenSecret));
		final ArrayList<String> id_list = new ArrayList<String>();
		final Handler handler = getHandler(listener);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					IDs ids = twitter.getFriendsIDs(-1);
					long[] friendIDs = ids.getIDs();
					for (int i = 0; i < friendIDs.length; i++) {
						id_list.add(friendIDs[i] + "");
					}
					Message msg = handler.obtainMessage();
					msg.what = FRIEND_SUCCESS; 
					msg.obj = id_list;
					handler.sendMessage(msg);
				} catch (TwitterException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(FAILED);
				}
				
			}
		}).start();
	}

	/**
	 * 获取自己的twitter ID
	 */
	public void getSelfID(final OnTwitterCallBackListener listener) {
		String accessToken = preferences.getString(TwitterConstant.ACCESS_TOKEN_KEY, "");
		String accessTokenSecret = preferences.getString(TwitterConstant.ACCESS_TOKEN_SECRET,
				"");
		if (twitter == null) {
			InitializeTwitter(TwitterConstant.TWITTER_CONSUMER_KEY, TwitterConstant.TWITTER_CONSUMER_SECRET);
		}
		twitter.setOAuthAccessToken(new AccessToken(accessToken,
				accessTokenSecret));
		final Handler handler = getHandler(listener);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Long id = null;
				try {
					id = twitter.getId();
					Message msg = handler.obtainMessage();
					msg.what = ID_SUCCESS;
					msg.obj = id;
					handler.sendMessage(msg);
				} catch (Exception e) {
					handler.sendEmptyMessage(FAILED);
					e.printStackTrace();
				}
				
			}
		}).start();
	}
	
	public void shareMSG(final String msg, OnTwitterCallBackListener listener){
		
		String accessToken = preferences.getString(TwitterConstant.ACCESS_TOKEN_KEY, "");
		String accessTokenSecret = preferences.getString(TwitterConstant.ACCESS_TOKEN_SECRET,
				"");
		if (twitter == null) {
			InitializeTwitter(TwitterConstant.TWITTER_CONSUMER_KEY, TwitterConstant.TWITTER_CONSUMER_SECRET);
		}
		twitter.setOAuthAccessToken(new AccessToken(accessToken,
				accessTokenSecret));
		final Handler handler = getHandler(listener);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
				StatusUpdate statusUpdate = new StatusUpdate(msg);
				//statusUpdate.setMedia(new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/Download/mm.jpg"));
				Status status = twitter.updateStatus(statusUpdate);
				handler.sendEmptyMessage(SUCCESS);
				} catch (TwitterException e) {
					handler.sendEmptyMessage(FAILED);
					e.printStackTrace();
				}
				
			}
		}).start();
	}

	public boolean isAuthorization() {
		String accessToken = preferences.getString(TwitterConstant.ACCESS_TOKEN_KEY,
				"");
		String accessTokenSecret = preferences.getString(
				TwitterConstant.ACCESS_TOKEN_SECRET, "");
		String displayName = preferences.getString(TwitterConstant.DISPLAY_NAME, "");
		if (accessToken.equals("") || accessTokenSecret.equals("") || displayName.equals("")) {
			return false;
		}
		return true;
	}
	
	public String getNameFromPreferences(){
		return preferences.getString(TwitterConstant.DISPLAY_NAME, "");
	}
	public String getImagFromPreferences(){
		return preferences.getString(TwitterConstant.DISPLAY_IMAG, "");
	}

	public void unBind() {
		preferences.edit().clear().commit();
		if (twitter != null) {
			twitter = null;
		}
	}
	/**
	 * 分享
	 * @param context
	 * @param message
	 */
	public void sendMessageByTwitter(Context context, String message) {
		Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("https://twitter.com/intent/tweet?text=" + message);
        intent.setData(content_url);
        context.startActivity(intent);
	}
}
