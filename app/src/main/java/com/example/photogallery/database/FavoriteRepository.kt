package com.sample.photogallery.database

class FavoriteRepository(private val dao: FavoritePhotoDao) {
    suspend fun addToFavorites(photo: FavoritePhoto) = dao.insert(photo)
    suspend fun getAllFavorites() = dao.getAll()
    suspend fun clearAll() = dao.deleteAll()
}