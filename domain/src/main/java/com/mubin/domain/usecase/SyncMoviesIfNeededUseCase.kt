package com.mubin.domain.usecase

import com.mubin.common.utils.network.NetworkResult
import com.mubin.domain.repo.MovieRepository

class SyncMoviesIfNeededUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(): NetworkResult<Unit> {
        return repository.syncMoviesIfNeeded()
    }
}