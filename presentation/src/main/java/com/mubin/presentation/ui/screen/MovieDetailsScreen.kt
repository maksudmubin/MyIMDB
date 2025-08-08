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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalWindowInfo
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    id: Int,
    viewmodel: HomeViewModel,
    onNavigateToWishlist: () -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewmodel.uiState.collectAsState()
    var movie by remember { mutableStateOf<Movie?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(id) {
        viewmodel.getMovieById(id) { movieData ->
            movie = movieData
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = movie?.title ?: "Movie Details",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
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
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> {
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
                MovieDetailsContent(
                    movie = movie!!,
                    isInWishlist = uiState.wishlist.any { it.id == movie?.id },
                    onToggleWishlist = { status ->
                        movie?.let {
                            viewmodel.onWishlistToggle(it.id, status)
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

}

@Composable
fun MovieDetailsContent(
    modifier: Modifier = Modifier,
    movie: Movie,
    isInWishlist: Boolean,
    onToggleWishlist: (Boolean) -> Unit,
) {
    val imageWidth = LocalWindowInfo.current.containerSize.width * .4
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .border(
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                AsyncImage(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .width(imageWidth.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(24.dp)),
                    model = movie.posterUrl,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(R.drawable.loading_image_placeholder),
                    error = painterResource(R.drawable.no_image_placeholder)
                )

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
            Surface(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        movie.title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("📅 Year: ${movie.year} | ⏱️ Runtime: ${movie.runtime} mins", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("🎭 Genres:", style = MaterialTheme.typography.titleMedium)
                    FlowRow {
                        movie.genres.forEach {
                            AssistChip(
                                onClick = {},
                                label = { Text(it) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("🎬 Director: ${movie.director}")
                    Text("🎭 Cast: ${movie.actors}")
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("📖 Plot", style = MaterialTheme.typography.titleMedium)
                    Text(movie.plot, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
