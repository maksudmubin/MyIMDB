package com.mubin.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mubin.network.model.Movie
import com.mubin.presentation.R
import com.mubin.presentation.ui.HomeViewModel
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    viewModel: HomeViewModel,
    onNavigateToWishlist: () -> Unit,
    onNavigateToDetails: (Movie) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleItemIndex ->
                val totalItems = uiState.filteredMovies.size
                if (lastVisibleItemIndex != null && lastVisibleItemIndex >= totalItems - 1) {
                    viewModel.loadNextMovies()
                }
            }
    }

    // UI layout
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("MyIMDB", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            actions = {
                Box {
                    IconButton(onClick = onNavigateToWishlist) {
                        Icon(Icons.Default.Favorite, contentDescription = "Wishlist")
                    }
                    if (uiState.wishlist.isNotEmpty()) {
                        Text(
                            text = uiState.wishlist.size.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .offset(x = (-8).dp, y = 8.dp)
                                .background(Color.Red, CircleShape)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(onClick = { /*viewModel.toggleTheme()*/ }) {
                    Icon(
                        imageVector = if (isSystemInDarkTheme()) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle Theme"
                    )
                }

                DropdownMenuGenreFilter(
                    selectedGenre = uiState.selectedGenre.orEmpty(),
                    genres = uiState.genres,
                    onGenreSelected = { genre ->
                        viewModel.onGenreSelected(genre.ifBlank { null })
                    }
                )
            }
        )

        // 🎬 Movie List
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(uiState.filteredMovies) { movie ->
                MovieItem(
                    movie = movie,
                    onClick = { onNavigateToDetails(movie) },
                    isInWishlist = uiState.wishlist.any { it.id == movie.id },
                    onToggleWishlist = { status ->
                        viewModel.onWishlistToggle(movie.id, status)
                    }
                )
            }

            // ⏳ Pagination Loading
            if (uiState.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuGenreFilter(
    selectedGenre: String,
    genres: List<String>,
    onGenreSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(text = selectedGenre.ifBlank { "All Genres" })
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Genre")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(text = { Text("All Genres") }, onClick = {
                expanded = false
                onGenreSelected("")
            })
            genres.forEach { genre ->
                DropdownMenuItem(text = { Text(genre) }, onClick = {
                    expanded = false
                    onGenreSelected(genre)
                })
            }
        }
    }
}

@Composable
fun MovieItem(
    movie: Movie,
    onClick: () -> Unit,
    isInWishlist: Boolean,
    onToggleWishlist: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation()
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier.size(80.dp),
                placeholder = painterResource(R.drawable.loading_image_placeholder),
                error = painterResource(R.drawable.no_image_placeholder)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(movie.title, fontWeight = FontWeight.Bold)
                Text("Year: ${movie.year}", style = MaterialTheme.typography.bodySmall)
                Text("Runtime: ${movie.runtime}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { onToggleWishlist(!isInWishlist) }) {
                Icon(
                    imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Wishlist"
                )
            }
        }
    }
}