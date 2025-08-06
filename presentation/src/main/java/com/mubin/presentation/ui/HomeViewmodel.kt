package com.mubin.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mubin.domain.usecase.InitializeMovies
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewmodel @Inject constructor (
    private val initializeMovies: InitializeMovies
) : ViewModel() {

    suspend fun initializeMovies() = initializeMovies.invoke()

}