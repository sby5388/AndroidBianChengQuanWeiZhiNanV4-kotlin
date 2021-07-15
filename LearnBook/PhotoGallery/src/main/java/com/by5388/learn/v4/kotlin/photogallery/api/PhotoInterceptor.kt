package com.by5388.learn.v4.kotlin.photogallery.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * 拦截
 */
private const val API_KEY = "d80e0fbab7551485a80a02a521f228eb"

class PhotoInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        //获取原始请求
        val originalRequest: Request = chain.request()
        //增加查询参数
        val newUrl: HttpUrl = originalRequest.url().newBuilder()
            //密钥
            .addQueryParameter("api_key", API_KEY)
            //返回格式
            .addQueryParameter("format", "json")
            //TODO
            .addQueryParameter("nojsoncallback", "1")
            //TODO
            .addQueryParameter("extras", "url_s")
            //1:启用拦截不适宜的内容
            .addQueryParameter("safesearch", "1")
            .build()
        //构建新的网络请求
        val newRequest: Request = originalRequest.newBuilder()
            .url(newUrl)
            .build()
        //产生网络响应消息
        return chain.proceed(newRequest)
    }
}