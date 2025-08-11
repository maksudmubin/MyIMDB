package com.mubin.presentation.ui.screen.movie_list

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.domain.model.Movie
import com.mubin.presentation.R
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Displays the main movie list screen with support for:
 * - Toggleable grid or list view for movies
 * - Infinite pagination loading on scroll
 * - Search by query
 * - Filter by genre
 * - Wishlist management with animated toggles
 * - Theme toggle button
 * - Navigation to movie details and wishlist
 *
 * @param viewModel The [MovieListViewModel] providing UI state and actions
 * @param onNavigateToWishlist Callback when wishlist icon is clicked
 * @param onNavigateToDetails Callback when a movie item is clicked, passes selected [Movie]
 * @param isDarkTheme Current theme mode flag
 * @param onToggleTheme Callback to toggle between light and dark themes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    viewModel: MovieListViewModel,
    onNavigateToWishlist: () -> Unit,
    onNavigateToDetails: (Movie) -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val gridState = rememberLazyGridState()
    val listState = rememberLazyListState()
    var lastVisibleIndex by rememberSaveable { mutableIntStateOf(0) }
    var lastVisibleOffset by rememberSaveable { mutableIntStateOf(0) }

    var isGridView by rememberSaveable { mutableStateOf(true) }

    // Load next page when scrolled near bottom - for grid
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { index ->
                if (index != null && index >= uiState.movies.size - 1) {
                    viewModel.handleIntent(MovieListIntent.LoadNextPage)
                }
            }
    }

    // Load next page when scrolled near bottom - for list
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { index ->
                if (index != null && index >= uiState.movies.size - 1) {
                    viewModel.handleIntent(MovieListIntent.LoadNextPage)
                }
            }
    }

    // Track scroll position for grid
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex to gridState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                if (isGridView) {
                    lastVisibleIndex = index
                    lastVisibleOffset = offset
                }
            }
    }

    // Track scroll position for list
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                if (!isGridView) {
                    lastVisibleIndex = index
                    lastVisibleOffset = offset
                }
            }
    }

    // Restore scroll position when toggling view
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
                title = {
                    Text(
                        text = "My IMDB",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    // Wishlist icon with badge count
                    Box(
                        modifier = Modifier.padding(end = 8.dp)
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

                    // Genre dropdown filter
                    DropdownMenuGenreFilter(
                        selectedGenre = uiState.selectedGenre.orEmpty(),
                        genres = uiState.genres,
                        onGenreSelected = { genre ->
                            viewModel.handleIntent(
                                MovieListIntent.SelectGenre(
                                    genre.ifBlank { null }
                                )
                            )
                        }
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // Controls row: theme toggle, search bar, view toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Theme toggle button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            onToggleTheme()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = if (isDarkTheme) painterResource(R.drawable.ic_dark_mode)
                        else painterResource(R.drawable.ic_light_mode),
                        contentDescription = "Toggle Theme",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Search bar
                CustomSearchBar(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    query = uiState.searchQuery,
                    onQueryChange = {
                        viewModel.handleIntent(MovieListIntent.SearchQueryChanged(it))
                    }
                )

                // View toggle button (grid/list)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            isGridView = !isGridView
                            viewModel.handleIntent(MovieListIntent.ToggleViewType)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(if (isGridView) R.drawable.ic_list else R.drawable.ic_grid),
                        contentDescription = "Toggle View",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Movie list/grid
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
                    items(uiState.movies, key = { it.id }) { movie ->
                        MovieGridItem(
                            movie = movie,
                            onClick = { onNavigateToDetails(movie) },
                            isInWishlist = uiState.wishlist.any { it.id == movie.id },
                            onToggleWishlist = { status ->
                                viewModel.handleIntent(
                                    MovieListIntent.ToggleWishlist(
                                        movieId = movie.id,
                                        status = status
                                    )
                                )
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
                    items(uiState.movies, key = { it.id }) { movie ->
                        MovieItem(
                            movie = movie,
                            onClick = { onNavigateToDetails(movie) },
                            isInWishlist = uiState.wishlist.any { it.id == movie.id },
                            onToggleWishlist = { status ->
                                viewModel.handleIntent(
                                    MovieListIntent.ToggleWishlist(
                                        movieId = movie.id,
                                        status = status
                                    )
                                )
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
/**
 * Dropdown menu composable to select a movie genre filter.
 *
 * Displays an outlined text field that opens a dropdown menu with all available genres
 * plus an "All Genres" option. Selecting a genre triggers the [onGenreSelected] callback.
 *
 * @param selectedGenre The currently selected genre. Empty string indicates "All Genres".
 * @param genres List of available genre strings.
 * @param onGenreSelected Callback invoked with the genre selected by the user.
 */
@Composable
fun DropdownMenuGenreFilter(
    selectedGenre: String,
    genres: List<String>,
    onGenreSelected: (String) -> Unit
) {
    val density = LocalDensity.current
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
                .widthIn(min = 120.dp),
            value = selectedGenre.ifBlank { "All Genres" },
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            maxLines = 1,
            label = {
                Text(
                    text = "Genre",
                    fontSize = with(density) { 10.sp / fontScale },
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            shape = RoundedCornerShape(8.dp),
            textStyle = TextStyle(
                fontSize = with(density) { 14.sp / fontScale },
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "All Genres",
                        fontSize = with(density) { 14.sp / fontScale },
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        )
                    )
                },
                onClick = {
                    MyImdbLogger.d("DropdownMenuGenreFilter", "Selected genre: All Genres")
                    expanded = false
                    onGenreSelected("")
                }
            )
            genres.forEach { genre ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = genre,
                            fontSize = with(density) { 14.sp / fontScale },
                            style = TextStyle(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            )
                        )
                    },
                    onClick = {
                        MyImdbLogger.d("DropdownMenuGenreFilter", "Selected genre: $genre")
                        expanded = false
                        onGenreSelected(genre)
                    }
                )
            }
        }
    }
}

/**
 * Custom search bar composable for searching movies.
 *
 * Displays an outlined text field with a search icon, placeholder, and
 * custom styling matching the app theme.
 *
 * @param modifier Modifier to be applied to the search bar container.
 * @param query Current search query text.
 * @param onQueryChange Lambda to be invoked when the query text changes.
 */
@Composable
fun CustomSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    val density = LocalDensity.current

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
        value = query,
        onValueChange = { newQuery ->
            MyImdbLogger.d("CustomSearchBar", "Search query changed to: $newQuery")
            onQueryChange(newQuery)
        },
        placeholder = {
            Text(
                text = "Search movies...",
                style = TextStyle(
                    fontSize = with(density) { 14.sp / fontScale },
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        shape = RoundedCornerShape(8.dp),
        textStyle = TextStyle(
            fontSize = with(density) { 14.sp / fontScale },
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            )
        ),
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


/**
 * Composable representing a single movie item card.
 *
 * Displays movie poster, title, year, runtime and a wishlist toggle button.
 *
 * @param movie The [Movie] data to display.
 * @param onClick Callback invoked when the card is clicked.
 * @param isInWishlist Whether the movie is currently in the wishlist.
 * @param onToggleWishlist Callback invoked when wishlist toggle is clicked with new status.
 */
@Composable
fun MovieItem(
    movie: Movie,
    onClick: () -> Unit,
    isInWishlist: Boolean,
    onToggleWishlist: (Boolean) -> Unit
) {
    val density = LocalDensity.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                MyImdbLogger.d("MovieItem", "Clicked movie: ${movie.title} (ID: ${movie.id})")
                onClick()
            },
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
                        fontSize = with(density) { 16.sp / fontScale },
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "📅 Year: ${movie.year}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = with(density) { 14.sp / fontScale },
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "⏱️ Runtime: ${movie.runtime} mins",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = with(density) { 14.sp / fontScale },
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                    )
                }
            }

            // Wishlist toggle button at top-right corner with animation
            AnimatedWishlistButton(
                isInWishlist = isInWishlist,
                onToggle = {
                    MyImdbLogger.d("MovieItem", "Wishlist toggled for movie ${movie.id}: $it")
                    onToggleWishlist(it)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            )
        }
    }
}

/**
 * Composable representing a movie item in grid layout.
 *
 * Displays movie poster with a gradient overlay at the bottom containing
 * title, year, and runtime. Shows an animated wishlist toggle at top-right.
 *
 * @param movie The [Movie] to display.
 * @param onClick Callback invoked when the card is clicked.
 * @param isInWishlist Whether the movie is currently in the wishlist.
 * @param onToggleWishlist Callback invoked with new wishlist status when toggled.
 */
@Composable
fun MovieGridItem(
    movie: Movie,
    onClick: () -> Unit,
    isInWishlist: Boolean,
    onToggleWishlist: (Boolean) -> Unit
) {
    val density = LocalDensity.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
            .clickable {
                MyImdbLogger.d("MovieGridItem", "Clicked movie: ${movie.title} (ID: ${movie.id})")
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Poster image fills entire card area
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.loading_image_placeholder),
                error = painterResource(R.drawable.no_image_placeholder),
                modifier = Modifier.fillMaxSize()
            )

            // Wishlist toggle button at top-right corner with animation
            AnimatedWishlistButton(
                isInWishlist = isInWishlist,
                onToggle = {
                    MyImdbLogger.d("MovieGridItem", "Wishlist toggled for movie ${movie.id}: $it")
                    onToggleWishlist(it)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            )

            // Bottom gradient overlay with movie info text
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xCC000000) // Semi-transparent black gradient
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
                    fontSize = with(density) { 14.sp / fontScale },
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.75f),
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "📅 Year: ${movie.year}",
                    color = Color.White,
                    fontSize = with(density) { 12.sp / fontScale },
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.75f),
                            offset = Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    )
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "⏱️ Runtime: ${movie.runtime} mins",
                    color = Color.White,
                    fontSize = with(density) { 12.sp / fontScale },
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
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

/**
 * Animated wishlist button with a rotating heart icon.
 *
 * Shows a filled heart if [isInWishlist] is true, otherwise an outlined heart.
 * Clicking toggles the wishlist status with a smooth rotation animation.
 *
 * @param isInWishlist Current wishlist status.
 * @param onToggle Callback invoked with the new wishlist status on click.
 * @param modifier Modifier to apply to the root Box.
 */
@Composable
fun AnimatedWishlistButton(
    isInWishlist: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Animate rotation: 0° when in wishlist, -360° when removed for full spin effect
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
            .clickable {
                val newStatus = !isInWishlist
                MyImdbLogger.d("AnimatedWishlistButton", "Wishlist toggled: $newStatus")
                onToggle(newStatus)
            },
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
