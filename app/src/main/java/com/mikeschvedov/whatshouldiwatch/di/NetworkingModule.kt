package com.mikeschvedov.whatshouldiwatch.di

import com.mikeschvedov.whatshouldiwatch.BuildConfig
import com.mikeschvedov.whatshouldiwatch.networking.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// hilt is dagger(java) for android
//This is a module
@Module
// where should it be available


@InstallIn(SingletonComponent::class)
// SingletonComponent - available to all application
//created at app start, dies at end
object NetworkingModule {

    private const val API_KEY_NAME = "api_key"
    private const val API_KEY_VALUE = BuildConfig.TMDB_API_KEY
    private const val BASE_URL = BuildConfig.TMDB_BASE_URL

    @Provides
    fun provideGsonFactory(): Converter.Factory = GsonConverterFactory.create()

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY //full log
    }

    //provides an object for injection
    // בנייה של אובייקטים שלא אנחנו אלה שבנו להם את הבנאי
    @Provides
    fun provideAuthorizationInterceptor() = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {

            val originalRequest = chain.request()
            // if we dont have the api key return the original request
            if (API_KEY_VALUE.isBlank()) return chain.proceed(originalRequest)
            // PASS KEY AS QUERY
            val url =
                originalRequest.url.newBuilder().addQueryParameter(API_KEY_NAME, API_KEY_VALUE)
                    .build()
            val newRequest = originalRequest.newBuilder().url(url).build()
            return chain.proceed(newRequest)
        }
    }

    @Provides
    fun provideOKHTTPClient(
        authInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor).build()

    @Provides
    fun provideRetrofit(
        httpClient: OkHttpClient,
        gsonConverterFactory: Converter.Factory
    ): Retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl(BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .build()

    @Provides
    fun provideMovieService(retrofit: Retrofit): MovieApi = retrofit.create(MovieApi::class.java)
    @Provides
    fun provideTVService(retrofit: Retrofit): TvApi = retrofit.create(TvApi::class.java)
    @Provides
    fun providePersonService(retrofit: Retrofit): PersonApi = retrofit.create(PersonApi::class.java)


/*

    @Provides
    fun provideRemoteApi(movieApi: MovieApi, tvApi: TvApi, personApi: PersonApi): RemoteApi =
        RemoteApiImpl(movieApi, tvApi, personApi)


*/


}