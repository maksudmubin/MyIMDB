package com.mubin.presentation.ui.screen

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.domain.model.Movie
import com.mubin.presentation.R
import com.mubin.presentation.ui.HomeViewModel

/**
 * Screen displaying the user's wishlist of movies.
 *
 * Shows the total count of wishlist items, a list of wishlist movies,
 * or an empty state message if the wishlist is empty.
 *
 * @param viewmodel The [HomeViewModel] managing UI state and actions
 * @param onNavigateToDetails Callback to navigate to movie details screen with movie ID
 * @param onBackClick Callback triggered when back navigation is requested
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    viewmodel: HomeViewModel,
    onNavigateToDetails: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val density = LocalDensity.current
    val uiState by viewmodel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Wishlist",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = with(density) { 16.sp / fontScale },
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        MyImdbLogger.d("WishlistScreen", "Back navigation clicked")
                        onBackClick()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Show total wishlist count if not empty
            if (uiState.wishlist.isNotEmpty()) {
                Text(
                    text = "Total Wishlist: ${uiState.wishlist.size}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Show empty state UI if wishlist is empty and not loading
            if (uiState.wishlist.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Your wishlist is empty.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            } else {
                // Show list of wishlist items
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.wishlist) { movie ->
                        WishlistItem(
                            movie = movie,
                            onClick = {
                                MyImdbLogger.d("WishlistScreen", "Wishlist item clicked: ${movie.title}")
                                onNavigateToDetails(movie.id)
                            },
                            isInWishlist = true,
                            onToggleWishlist = { status ->
                                MyImdbLogger.d("WishlistScreen", "Wishlist toggle for ${movie.title}, status: $status")
                                viewmodel.onWishlistToggle(movie.id, status)
                            }
                        )
                    }

                    // Show loading indicator if loading
                    if (uiState.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
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

/**
 * Composable displaying a single movie item in the wishlist.
 *
 * Shows movie poster, title, year, runtime, and a delete button
 * to remove the movie from the wishlist.
 *
 * @param movie The [Movie] data to display
 * @param onClick Callback invoked when the whole item is clicked
 * @param isInWishlist Boolean indicating if movie is currently in wishlist (always true here)
 * @param onToggleWishlist Callback to toggle wishlist status, triggered by delete icon button
 */
@Composable
fun WishlistItem(
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
                MyImdbLogger.d("WishlistItem", "Movie clicked: ${movie.title}")
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(8.dp)) {
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
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                    )
                }
            }

            // Delete button on top-right corner
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        shape = CircleShape
                    ),
                onClick = {
                    MyImdbLogger.d("WishlistItem", "Delete clicked for movie: ${movie.title}")
                    onToggleWishlist(!isInWishlist)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Remove from Wishlist"
                )
            }
        }
    }
}