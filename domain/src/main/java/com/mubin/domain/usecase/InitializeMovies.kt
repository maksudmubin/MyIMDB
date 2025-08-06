package com.mubin.domain.usecase

import com.mubin.domain.repo.MovieRepository
import com.mubin.network.util.NetworkResult

class InitializeMovies (
    private val repository: MovieRepository
) {
    suspend operator fun invoke(): NetworkResult<Unit> {
        return repository.syncMoviesIfNeeded()
    }

}