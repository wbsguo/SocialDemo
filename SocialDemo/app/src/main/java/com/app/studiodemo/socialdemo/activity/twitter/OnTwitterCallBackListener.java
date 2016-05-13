package com.app.studiodemo.socialdemo.activity.twitter;

import java.util.ArrayList;

public interface OnTwitterCallBackListener {

	void onSuccess();
	
	void onError();
	
	void onSelfId(Long id);
	
	void onFriends(ArrayList<String> list);
}
