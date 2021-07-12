package com.by5388.learn.v4.kotlin.photogallery.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface FlickrApi {

    @GET("/")
    fun fetchContents(): Call<String>

    @GET("services/rest/?method=flickr.interestingness.getList&api_key=d80e0fbab7551485a80a02a521f228eb&format=json&nojsoncallback=1&extras=url_s")
    fun fetchPhotosOld(): Call<String>

    @GET("services/rest/?method=flickr.interestingness.getList&api_key=d80e0fbab7551485a80a02a521f228eb&format=json&nojsoncallback=1&extras=url_s")
    fun fetchPhotosOld2(): Call<FlickrResponse>

    @GET("services/rest/?method=flickr.interestingness.getList&api_key=d80e0fbab7551485a80a02a521f228eb&format=json&nojsoncallback=1&extras=url_s")
    fun fetchPhotos(): Call<PhotoResponse>

    /**
     * 获取图片
     */
    @GET
    fun fetchUrlBytes(@Url url: String): Call<ResponseBody>;

}