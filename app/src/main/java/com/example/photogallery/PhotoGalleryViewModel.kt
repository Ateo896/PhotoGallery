package com.sample.photogallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.photogallery.data.Photo
import com.sample.photogallery.data.PhotoRepository
import com.sample.photogallery.data.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhotoGalleryViewModel : ViewModel() {
    private val repository = PhotoRepository(RetrofitInstance.api)
    private val apiKey = "YOUR_API_KEY" // Замените на реальный ключ

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var currentQuery: String = "interesting"

    fun loadInterestingPhotos() {
        currentQuery = "interesting"
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.fetchInterestingPhotos(apiKey)
            _photos.value = result
            _isLoading.value = false
        }
    }

    fun searchPhotos(query: String) {
        if (query.isBlank()) return
        currentQuery = query
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.searchPhotos(apiKey, query)
            _photos.value = result
            _isLoading.value = false
        }
    }
}