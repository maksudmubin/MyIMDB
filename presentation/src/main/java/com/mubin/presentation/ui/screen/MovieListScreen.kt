package com.mubin.presentation.ui.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    onNavigateToDetails: (Movie) -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()
    val listState = rememberLazyListState()
    var isGridView by rememberSaveable { mutableStateOf(true) }
    var lastVisibleIndex by rememberSaveable { mutableIntStateOf(0) }
    var lastVisibleOffset by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { index ->
                if (index != null && index >= uiState.movieList.size - 1) {
                    viewModel.loadNextMovies()
                }
            }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { index ->
                if (index != null && index >= uiState.movieList.size - 1) {
                    viewModel.loadNextMovies()
                }
            }
    }

    LaunchedEffect(isGridView) {
        if (isGridView) {
            gridState.scrollToItem(lastVisibleIndex, lastVisibleOffset)
        } else {
            listState.scrollToItem(lastVisibleIndex, lastVisibleOffset)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My IMDB", fontWeight = FontWeight.SemiBold, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                    ) {
                        IconButton(onClick = onNavigateToWishlist) {
                            Icon(Icons.Default.Favorite, contentDescription = "Wishlist")
                        }
                        if (uiState.wishlist.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp, end = 4.dp)
                                    .size(18.dp)
                                    .background(Color.Red, CircleShape)
                                    .align(Alignment.TopEnd)
                            ) {
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = uiState.wishlist.size.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(
                                        platformStyle = PlatformTextStyle(
                                            includeFontPadding = false
                                        )
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    DropdownMenuGenreFilter(
                        selectedGenre = uiState.selectedGenre.orEmpty(),
                        genres = uiState.genres,
                        onGenreSelected = { genre ->
                            viewModel.onGenreSelected(genre.ifBlank { null })
                        }
                    )
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .height(48.dp) // match height
                        .clip(RoundedCornerShape(8.dp)) // match radius
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onToggleTheme() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_dark_mode),
                        contentDescription = "Dark Mode",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.height(IntrinsicSize.Min)
                    ) {
                        Text(
                            text = "Dark Mode",
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (isDarkTheme) "ON" else "OFF",
                            fontSize = 10.sp,
                            lineHeight = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDarkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                CustomSearchBar(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged
                )

                Row(
                    modifier = Modifier
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            if (isGridView) {
                                lastVisibleIndex = gridState.firstVisibleItemIndex
                                lastVisibleOffset = gridState.firstVisibleItemScrollOffset
                            } else {
                                lastVisibleIndex = listState.firstVisibleItemIndex
                                lastVisibleOffset = listState.firstVisibleItemScrollOffset
                            }
                            isGridView = !isGridView
                        }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(if (isGridView) R.drawable.ic_list else R.drawable.ic_grid),
                        contentDescription = "Toggle View",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.height(IntrinsicSize.Min)
                    ) {
                        Text(
                            text = "View Mode",
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (isGridView) "Grid" else "List",
                            fontSize = 10.sp,
                            lineHeight = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = gridState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.movieList) { movie ->
                        MovieGridItem(
                            movie = movie,
                            onClick = { onNavigateToDetails(movie) },
                            isInWishlist = uiState.wishlist.any { it.id == movie.id },
                            onToggleWishlist = { status ->
                                viewModel.onWishlistToggle(movie.id, status)
                            }
                        )
                    }

                    if (uiState.isLoading) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.movieList) { movie ->
                        MovieItem(
                            movie = movie,
                            onClick = { onNavigateToDetails(movie) },
                            isInWishlist = uiState.wishlist.any { it.id == movie.id },
                            onToggleWishlist = { status ->
                                viewModel.onWishlistToggle(movie.id, status)
                            }
                        )
                    }

                    if (uiState.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuGenreFilter(
    selectedGenre: String,
    genres: List<String>,
    onGenreSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = Modifier
            .padding(bottom = 8.dp),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                .widthIn(min = 120.dp)
                .height(56.dp), // match search bar height
            value = selectedGenre.ifBlank { "All Genres" },
            onValueChange = {},
            readOnly = true,
            label = { Text("Genre") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            shape = RoundedCornerShape(8.dp), // match search bar corner
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            ),
            textStyle = MaterialTheme.typography.bodyLarge
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("All Genres") },
                onClick = {
                    expanded = false
                    onGenreSelected("")
                }
            )
            genres.forEach { genre ->
                DropdownMenuItem(
                    text = { Text(genre) },
                    onClick = {
                        expanded = false
                        onGenreSelected(genre)
                    }
                )
            }
        }
    }
}

@Composable
fun CustomSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search movies...",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        textStyle = MaterialTheme.typography.bodyLarge,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
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
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.loading_image_placeholder),
                    error = painterResource(R.drawable.no_image_placeholder),
                    modifier = Modifier
                        .width(120.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(8.dp))
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = movie.title,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(
                        modifier = Modifier
                            .size(8.dp)
                    )
                    Text(
                        text = "📅 Year: ${movie.year}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(
                        modifier = Modifier
                            .size(8.dp)
                    )
                    Text(
                        text = "⏱️ Runtime: ${movie.runtime} mins",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Wishlist button at top-right corner
            AnimatedWishlistButton(
                isInWishlist = isInWishlist,
                onToggle = onToggleWishlist,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            )
        }
    }
}

@Composable
fun MovieGridItem(
    movie: Movie,
    onClick: () -> Unit,
    isInWishlist: Boolean,
    onToggleWishlist: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.loading_image_placeholder),
                error = painterResource(R.drawable.no_image_placeholder),
                modifier = Modifier.fillMaxSize()
            )

            // Wishlist icon top-right
            AnimatedWishlistButton(
                isInWishlist = isInWishlist,
                onToggle = onToggleWishlist,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            )

            // Bottom info with gradient
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xCC000000)
                            )
                        )
                    )
                    .padding(horizontal = 12.dp)
                    .padding(top = 40.dp)
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = movie.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.75f),
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(
                    modifier = Modifier
                        .size(4.dp)
                )
                Text(
                    text = "📅 Year: ${movie.year}",
                    color = Color.White,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.75f),
                            offset = Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    )
                )
                Spacer(
                    modifier = Modifier
                        .size(4.dp)
                )
                Text(
                    text = "⏱️ Runtime: ${movie.runtime} mins",
                    color = Color.White,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.75f),
                            offset = Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    )
                )
            }
        }
    }
}

@Composable
fun AnimatedWishlistButton(
    isInWishlist: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isInWishlist) 0f else -360f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "WishlistRotation"
    )

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = CircleShape
            )
            .clickable { onToggle(!isInWishlist) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Wishlist",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.graphicsLayer {
                rotationZ = rotation
            }
        )
    }
}
