package com.by5388.learn.v4.kotlin.photogallery

import com.by5388.learn.v4.kotlin.photogallery.api.PhotoResponse
import com.google.gson.*
import java.lang.reflect.Type

/**
 * 自定义Gson反序列化器：手动解析Json
 * 默认的反序列化器会把所有的JSON数据映射到我们的数据模型，多少有性能消耗
 * 这里是为了去掉返回结果中最外面的一层封装类：FlickrResponse
 *
 */
class PhotoDeserializer : JsonDeserializer<PhotoResponse?> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): PhotoResponse? {
        if (json?.isJsonObject == true) {
            if (json is JsonObject) {
                val photosObject: JsonObject? = json.getAsJsonObject("photos")
                if (photosObject != null) {
                    val response = PhotoResponse()
                    response.page = photosObject.get("page").asInt
                    response.pages = photosObject.get("pages").asInt
                    response.perpage = photosObject.get("perpage").asInt
                    response.total = photosObject.get("total").asInt
                    val galleryList: MutableList<GalleryItem> = mutableListOf()
                    val photoArray: JsonArray = photosObject.getAsJsonArray("photo")
                    //todo 这里要用的是 until;
                    for (i in 0 until photoArray.size()) {
                        val jsonElement: JsonElement = photoArray[i]
                        val galleryItem = GalleryItem()
                        val jsonObject = jsonElement as JsonObject
                        galleryItem.title = jsonObject.get("title").asString
                        galleryItem.id = jsonObject.get("id").asString
                        galleryItem.url = jsonObject.get("url_s").asString
                        galleryList.add(galleryItem)
                    }

                    response.galleryItems = galleryList
                    return response
                }
            }
        }

        return null
    }
}