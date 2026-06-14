package com.sample.photogallery.data

import android.util.Log

class PhotoRepository(private val api: FlickrApi) {
    suspend fun fetchInterestingPhotos(apiKey: String): List<Photo> {
        return try {
            val response = api.getInterestingPhotos(apiKey = apiKey)
            response.photos.photos
        } catch (e: Exception) {
            Log.e("PhotoRepo", "Error fetching interesting photos", e)
            emptyList()
        }
    }

    suspend fun searchPhotos(apiKey: String, query: String): List<Photo> {
        return try {
            val response = api.searchPhotos(apiKey = apiKey, searchText = query)
            response.photos.photos
        } catch (e: Exception) {
            Log.e("PhotoRepo", "Error searching photos", e)
            emptyList()
        }
    }
}