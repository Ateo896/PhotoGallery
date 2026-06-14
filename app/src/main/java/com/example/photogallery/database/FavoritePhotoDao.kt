package com.sample.photogallery.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoritePhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: FavoritePhoto)

    @Query("SELECT * FROM favorite_photos")
    suspend fun getAll(): List<FavoritePhoto>

    @Query("DELETE FROM favorite_photos")
    suspend fun deleteAll()
}