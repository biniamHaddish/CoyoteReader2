package com.wilee8.coyotereader2.retrofitservices;

import com.wilee8.coyotereader2.gson.StreamContents;
import com.wilee8.coyotereader2.gson.StreamPrefs;
import com.wilee8.coyotereader2.gson.SubscriptionList;
import com.wilee8.coyotereader2.gson.TagList;
import com.wilee8.coyotereader2.gson.UnreadCounts;
import com.wilee8.coyotereader2.gson.UserInfo;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import rx.Observable;

public interface InoreaderRxGsonService {
	@GET("/reader/api/0/unread-count")
	Observable<UnreadCounts> unreadCounts();

	@GET("/reader/api/0/tag/list")
	Observable<TagList> tagList();

	@GET("/reader/api/0/subscription/list")
	Observable<SubscriptionList> subscriptionList();

	@GET("/reader/api/0/user-info")
	Observable<UserInfo> userInfo();

	@GET("/reader/api/0/preference/stream/list")
	Observable<StreamPrefs> streamPrefs();

	@POST("/reader/api/0/stream/contents/{feedId}")
	Observable<StreamContents> streamContents(@Path("feedId") String feedId, @QueryMap Map<String, String> options);
}