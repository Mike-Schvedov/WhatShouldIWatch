package com.mikeschvedov.whatshouldiwatch.data.local.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mikeschvedov.whatshouldiwatch.models.response.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {

    // ------------ Movies ---------------- //

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovie(movie: Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovies(movies: List<Movie>)

    @Query("SELECT * FROM Movie")
    fun getMovies(): LiveData<List<MovieWithCategory>>

    @Query("DELETE FROM Movie")
    fun deleteAllMovies()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend  fun addMovieCategoryCrossRef(movieCategoryCrossRef: MovieCategoryCrossRef)

    @Transaction
    suspend fun addMovieWithCategory(movie: Movie, category: Category) {
        addMovie(movie)
        addCategory(category)
        addMovieCategoryCrossRef(MovieCategoryCrossRef(movie.id, category.categoryId))
    }

    @Query("SELECT * FROM category WHERE categoryId = :categoryId")
    fun getMoviesByCategory(categoryId:Long) : CategoryWithMovies

    // ------------ Category ---------------- //

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCategory(category: Category)

    @Query("SELECT * FROM category")
    fun getCategories(): LiveData<List<Category>>

    @Transaction
    @Query("SELECT * FROM category")
    fun getCategoryWithMovies(): List<CategoryWithMovies>

   /* // Option A - not working
    @Transaction
    @Query("SELECT * FROM category")
    fun getCategoryWithTvShows(): Flow<List<CategoryWithTvShows>>
*/
    // Option B - working
        @Transaction
        @Query("SELECT * FROM category")
        fun getCategoryWithTvShows(): List<CategoryWithTvShows>


    // ------------ Tv Shows ---------------- //

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTvShow(tvShow: TVShow)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTvShows(tvShows: List<TVShow>)

    @Query("SELECT * FROM TVShow")
    fun getTvShows(): LiveData<List<TvShowWithCategory>>

    @Query("DELETE FROM TVShow")
    fun deleteAllTvShows()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTvShowCategoryCrossRef(tvShowCategoryCrossRef: TvShowCategoryCrossRef)

    @Transaction
    suspend fun addTvShowWithCategory(tvShow: TVShow, category: Category) {
        addTvShow(tvShow)
        addCategory(category)
        addTvShowCategoryCrossRef(TvShowCategoryCrossRef(tvShow.id, category.categoryId))
    }
}