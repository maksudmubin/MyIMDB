package com.mubin.domain.usecase

import com.mubin.common.utils.logger.MyImdbLogger
import com.mubin.common.utils.network.NetworkResult
import com.mubin.domain.repo.MovieRepository

/**
 * Use case for synchronizing movies and genres from the remote API if local data is empty.
 *
 * Encapsulates the sync logic, providing a clean interface for initiating data refresh.
 *
 * @property repository The [MovieRepository] to perform synchronization.
 */
class SyncMoviesIfNeededUseCase(
    private val repository: MovieRepository
) {

    /**
     * Invokes the use case to synchronize movies if needed.
     *
     * @return [NetworkResult.Success] if sync succeeded or not needed,
     *         [NetworkResult.Error] if sync failed,
     *         or [NetworkResult.Loading] during sync.
     */
    suspend operator fun invoke(): NetworkResult<Unit> {
        MyImdbLogger.d("SyncMoviesIfNeededUseCase", "Starting syncMoviesIfNeeded.")
        return repository.syncMoviesIfNeeded()
    }
}