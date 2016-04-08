package com.wilee8.coyotereader2.retrofitservices;

import com.wilee8.coyotereader2.gson.UnreadCounts;

import retrofit2.Call;
import retrofit2.http.GET;

public interface InoreaderGsonService {
	@GET("/reader/api/0/unread-count")
	Call<UnreadCounts> unreadCountsObject();
}
