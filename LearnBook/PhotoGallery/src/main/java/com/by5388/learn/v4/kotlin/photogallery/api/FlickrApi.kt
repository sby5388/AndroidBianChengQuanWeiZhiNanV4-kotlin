package com.by5388.learn.v4.kotlin.photogallery.api

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface FlickrApi {

    @GET("/")
    fun fetchContents(): Call<String>

    @GET("services/rest/?method=flickr.interestingness.getList&api_key=d80e0fbab7551485a80a02a521f228eb&format=json&nojsoncallback=1&extras=url_s")
    fun fetchPhotosOld(): Call<String>

    @GET("services/rest/?method=flickr.interestingness.getList&api_key=d80e0fbab7551485a80a02a521f228eb&format=json&nojsoncallback=1&extras=url_s")
    fun fetchPhotosOld2(): Call<FlickrResponse>


    /**
     * 获取图片列表
     */
    //@GET("services/rest/?method=flickr.interestingness.getList&api_key=d80e0fbab7551485a80a02a521f228eb&format=json&nojsoncallback=1&extras=url_s")
    @GET("services/rest/?method=flickr.interestingness.getList")
    fun fetchPhotos(): Call<PhotoResponse>

    /**
     * 根据关键字搜索图片
     * 使用原始的retrofit
     */
    @GET("services/rest/?method=flickr.photos.search")
    fun searchPhotos(@Query("text") query: String): Call<PhotoResponse>

    /**
     * 根据关键字搜索图片
     * 使用rxjava
     */
    @GET("services/rest/?method=flickr.photos.search")
    fun searchPhotosRx(@Query("text") query: String): Single<PhotoResponse>

    /**
     * 根据关键字搜索图片
     * 使用协程
     */
    @GET("services/rest/?method=flickr.photos.search")
    suspend fun searchPhotosSuspend(@Query("text") query: String): PhotoResponse


    /**
     * 获取图片
     */
    @GET
    fun fetchUrlBytes(@Url url: String): Call<ResponseBody>;

}