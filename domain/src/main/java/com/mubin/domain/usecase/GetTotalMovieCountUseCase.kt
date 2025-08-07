package com.mubin.domain.usecase

import com.mubin.domain.repo.MovieRepository

class GetTotalMovieCountUseCase (
    private val repository: MovieRepository
) {
    suspend operator fun invoke(): Int {
        return repository.getTotalMovieCount()
    }
}