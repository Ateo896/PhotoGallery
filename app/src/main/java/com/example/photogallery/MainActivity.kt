package com.sample.photogallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.sample.photogallery.database.FavoritePhoto
import com.sample.photogallery.database.FavoriteRepository
import com.sample.photogallery.database.PhotoDatabase
import com.sample.photogallery.ui.theme.PhotoGalleryTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoGalleryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PhotoGalleryScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGalleryScreen() {
    val viewModel: PhotoGalleryViewModel = viewModel()
    val photos by viewModel.photos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showSearchDialog by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = remember { PhotoDatabase.getInstance(context) }
    val favoriteRepo = remember { FavoriteRepository(database.favoritePhotoDao()) }
    val scope = rememberCoroutineScope()

    // Загружаем интересные фотографии при старте
    LaunchedEffect(Unit) {
        viewModel.loadInterestingPhotos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Photo Gallery") },
                actions = {
                    IconButton(onClick = { showSearchDialog = true }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Избранные фотографии") },
                            onClick = {
                                showMenu = false
                                scope.launch {
                                    val favorites = favoriteRepo.getAllFavorites()
                                    // Показываем диалог со списком избранного
                                    showFavoritesDialog(context, favorites)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Очистить БД") },
                            onClick = {
                                showMenu = false
                                scope.launch {
                                    favoriteRepo.clearAll()
                                }
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.DarkGray)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn {
                    items(photos) { photo ->
                        PhotoCard(
                            photo = photo,
                            onCardClick = {
                                scope.launch {
                                    favoriteRepo.addToFavorites(
                                        FavoritePhoto(
                                            id = photo.id,
                                            title = photo.title,
                                            url_s = photo.url_s,
                                            owner = photo.owner
                                        )
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showSearchDialog) {
        AlertDialog(
            onDismissRequest = { showSearchDialog = false },
            title = { Text("Поиск фотографий") },
            text = {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Ключевое слово") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (searchText.isNotBlank()) {
                            viewModel.searchPhotos(searchText)
                        }
                        showSearchDialog = false
                    }
                ) {
                    Text("Искать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSearchDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun PhotoCard(photo: com.sample.photogallery.data.Photo, onCardClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            AsyncImage(
                model = photo.url_s,
                contentDescription = photo.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Text(
                text = photo.title,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Функция для отображения диалога со списком избранного
fun showFavoritesDialog(context: android.content.Context, favorites: List<FavoritePhoto>) {
    // Простая реализация через AlertDialog с LazyColumn
    // Для простоты можно показать в новом окне, но здесь будет диалог
    androidx.appcompat.app.AlertDialog.Builder(context)
        .setTitle("Избранные фотографии")
        .setMessage(favorites.joinToString("\n") { it.title })
        .setPositiveButton("OK", null)
        .show()
}