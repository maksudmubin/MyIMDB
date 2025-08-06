package com.mubin.domain.usecase

import com.mubin.domain.repo.MovieRepository

class GetGenres(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(): List<String> {
        return repository.getAllGenres()
    }
}