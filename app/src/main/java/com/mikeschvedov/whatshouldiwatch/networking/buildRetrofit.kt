package com.mikeschvedov.whatshouldiwatch.networking

import com.mikeschvedov.whatshouldiwatch.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val AUTHORIZATION_HEADER = "Authorization"
private const val BASE_URL = BuildConfig.TMDB_BASE_URL
private const val TMDB_API_KEY = BuildConfig.TMDB_API_KEY

fun buildAuthorizationInterceptor() = object : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()
        // if we dont have the api key return the original request
        if (TMDB_API_KEY.isBlank()) return chain.proceed(originalRequest)


        // PASS KEY AS QUERY
        val url =
            originalRequest.url.newBuilder().addQueryParameter("api_key", TMDB_API_KEY).build()
        val newRequest = originalRequest.newBuilder().url(url).build()
        return chain.proceed(newRequest)

     /*   // PASS KEY AS HEADER
         val newRequest = originalRequest.newBuilder().addHeader(AUTHORIZATION_HEADER, TMDB_API_KEY).build()
         return chain.proceed(newRequest)*/
    }
}

fun buildOKHTTPClient(): OkHttpClient =
    OkHttpClient().newBuilder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor(buildAuthorizationInterceptor()).build()

fun buildRetrofit(): Retrofit = Retrofit.Builder()
    .client(buildOKHTTPClient())
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

fun buildMovieService(): MovieApi = buildRetrofit().create(MovieApi::class.java)
fun buildTVService(): TvApi = buildRetrofit().create(TvApi::class.java)
fun buildPersonService(): PersonApi = buildRetrofit().create(PersonApi::class.java)

fun buildRemoteApi(): RemoteApi =
    RemoteApiImpl(buildMovieService(), buildTVService(), buildPersonService())


