package com.mubin.common.di;

import com.mubin.database.dao.GenreDao;
import com.mubin.database.dao.MovieDao;
import com.mubin.domain.repo.MovieRepository;
import com.mubin.network.service.MovieApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class RepositoryModule_ProvideMovieRepositoryFactory implements Factory<MovieRepository> {
  private final Provider<MovieApiService> movieApiProvider;

  private final Provider<MovieDao> movieDaoProvider;

  private final Provider<GenreDao> genreDaoProvider;

  private RepositoryModule_ProvideMovieRepositoryFactory(Provider<MovieApiService> movieApiProvider,
      Provider<MovieDao> movieDaoProvider, Provider<GenreDao> genreDaoProvider) {
    this.movieApiProvider = movieApiProvider;
    this.movieDaoProvider = movieDaoProvider;
    this.genreDaoProvider = genreDaoProvider;
  }

  @Override
  public MovieRepository get() {
    return provideMovieRepository(movieApiProvider.get(), movieDaoProvider.get(), genreDaoProvider.get());
  }

  public static RepositoryModule_ProvideMovieRepositoryFactory create(
      Provider<MovieApiService> movieApiProvider, Provider<MovieDao> movieDaoProvider,
      Provider<GenreDao> genreDaoProvider) {
    return new RepositoryModule_ProvideMovieRepositoryFactory(movieApiProvider, movieDaoProvider, genreDaoProvider);
  }

  public static MovieRepository provideMovieRepository(MovieApiService movieApi, MovieDao movieDao,
      GenreDao genreDao) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideMovieRepository(movieApi, movieDao, genreDao));
  }
}
