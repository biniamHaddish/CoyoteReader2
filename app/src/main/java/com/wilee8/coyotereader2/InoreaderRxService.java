package com.wilee8.coyotereader2;

import com.squareup.okhttp.ResponseBody;

import java.util.Map;

import retrofit.http.POST;
import retrofit.http.QueryMap;
import rx.Observable;

public interface InoreaderRxService {
	@POST("/accounts/ClientLogin")
	Observable<ResponseBody> clientLogin(@QueryMap Map<String, String> options);

	@POST("/reader/api/0/edit-tag")
	Observable<ResponseBody> editTag(@QueryMap Map<String, String> options);

	@POST("/reader/api/0/mark-all-as-read")
	Observable<ResponseBody> markAllAsRead(@QueryMap Map<String, String> options);
}