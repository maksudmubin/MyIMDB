package com.mubin.presentation.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.mubin.presentation.ui.HomeViewModel

/**
 * Screen composable showing detailed information about a movie identified by [id].
 *
 * Fetches the movie asynchronously from the [viewmodel], shows a loading indicator while fetching,
 * displays an error message if movie is not found, or shows [MovieDetailsContent] on success.
 *
 * Also provides a top app bar with back navigation and wishlist navigation buttons.
 *
 * @param id The ID of the movie to display
 * @param viewmodel The [HomeViewModel] providing UI state and movie fetching
 * @param onNavigateToWishlist Callback invoked when wishlist icon in the app bar is clicked
 * @param onBackClick Callback invoked when back navigation icon is clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    id: Int,
    viewmodel: HomeViewModel,
    onNavigateToWishlist: () -> Unit,
    onBackClick: () -> Unit
) {
    val density = LocalDensity.current
    val uiState by viewmodel.uiState.collectAsState()
    var movie by remember { mutableStateOf<Movie?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Load movie by id asynchronously on first composition and when id changes
    LaunchedEffect(id) {
        viewmodel.getMovieById(id) { movieData ->
            movie = movieData
            isLoading = false
            MyImdbLogger.d("MovieDetailsScreen", "Loaded movie with id $id: $movieData")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = movie?.title ?: "Movie Details",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = with(density) { 16.sp / fontScale },
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        MyImdbLogger.d("MovieDetailsScreen", "Back button clicked")
                        onBackClick()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                    ) {
                        IconButton(onClick = {
                            MyImdbLogger.d("MovieDetailsScreen", "Navigate to Wishlist clicked")
                            onNavigateToWishlist()
                        }) {
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
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                // Show loading spinner while fetching movie data
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            movie == null -> {
                // Show error text if movie not found
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Movie not found", color = MaterialTheme.colorScheme.error)
                }
            }

            else -> {
                // Show movie details content with wishlist toggle
                MovieDetailsContent(
                    movie = movie!!,
                    isInWishlist = uiState.wishlist.any { it.id == movie?.id },
                    onToggleWishlist = { status ->
                        movie?.let {
                            MyImdbLogger.d("MovieDetailsScreen", "Wishlist toggle for movie id ${it.id}: $status")
                            viewmodel.onWishlistToggle(it.id, status)
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}


/**
 * Composable displaying detailed information about a movie.
 *
 * Shows a blurred background poster, a focused poster image with wishlist toggle,
 * and detailed movie info such as title, year, runtime, genres, director, cast, and plot.
 *
 * @param modifier Optional [Modifier] for styling and layout
 * @param movie The [Movie] data to display
 * @param isInWishlist Whether the movie is currently in the wishlist
 * @param onToggleWishlist Callback invoked when wishlist toggle button is pressed,
 *                         with the new wishlist status (true if added, false if removed)
 */
@Composable
fun MovieDetailsContent(
    modifier: Modifier = Modifier,
    movie: Movie,
    isInWishlist: Boolean,
    onToggleWishlist: (Boolean) -> Unit,
) {
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Blurred full-screen background image of the movie poster
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp),
            model = movie.posterUrl,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.loading_image_placeholder),
            error = painterResource(R.drawable.no_image_placeholder)
        )

        // Scrollable column with main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Poster with border and wishlist toggle button overlay
            Box(
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .border(
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                AsyncImage(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(24.dp)),
                    model = movie.posterUrl,
                    contentDescription = movie.title,
                    contentScale = ContentScale.FillBounds,
                    placeholder = painterResource(R.drawable.loading_image_placeholder),
                    error = painterResource(R.drawable.no_image_placeholder)
                )

                // Animated wishlist toggle button aligned top-end with padding
                AnimatedWishlistButton(
                    isInWishlist = isInWishlist,
                    onToggle = onToggleWishlist,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 20.dp)
                        .padding(end = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Surface container for textual movie details with some transparency and elevation
            Surface(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Movie title, bold and scaled
                    Text(
                        text = movie.title,
                        fontSize = with(density) { 16.sp / fontScale },
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Year and runtime information
                    Text(
                        text = "📅 Year: ${movie.year} | ⏱️ Runtime: ${movie.runtime} mins",
                        fontSize = with(density) { 12.sp / fontScale },
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Genres label
                    Text(
                        text = "🎭 Genres:",
                        fontSize = 14.sp,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        )
                    )

                    // Genres displayed as chips in a FlowRow
                    FlowRow {
                        movie.genres.forEach {
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        text = it,
                                        fontSize = with(density) { 12.sp / fontScale },
                                        style = TextStyle(
                                            fontWeight = FontWeight.Normal,
                                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Director
                    Text(
                        text = "🎬 Director: ${movie.director}",
                        fontSize = 14.sp,
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Cast
                    Text(
                        text = "🎭 Cast: ${movie.actors}",
                        fontSize = 14.sp,
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Plot header
                    Text(
                        text = "📖 Plot",
                        fontSize = 14.sp,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        )
                    )

                    // Plot text
                    Text(
                        text = movie.plot,
                        fontSize = with(density) { 12.sp / fontScale },
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
