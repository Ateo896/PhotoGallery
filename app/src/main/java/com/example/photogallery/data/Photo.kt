package com.sample.photogallery.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FlickrResponse(
    @Json(name = "photos") val photos: PhotosResponse,
    @Json(name = "stat") val stat: String
)

@JsonClass(generateAdapter = true)
data class PhotosResponse(
    @Json(name = "page") val page: Int,
    @Json(name = "pages") val pages: Int,
    @Json(name = "perpage") val perpage: Int,
    @Json(name = "total") val total: Int,
    @Json(name = "photo") val photos: List<Photo>
)

@JsonClass(generateAdapter = true)
data class Photo(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "url_s") val url_s: String, // маленькое изображение
    @Json(name = "owner") val owner: String,
    @Json(name = "secret") val secret: String
)