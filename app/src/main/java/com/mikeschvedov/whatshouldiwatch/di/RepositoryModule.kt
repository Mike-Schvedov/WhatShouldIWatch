package com.mikeschvedov.whatshouldiwatch.di

import com.mikeschvedov.whatshouldiwatch.data.remote.networking.RemoteApi
import com.mikeschvedov.whatshouldiwatch.data.remote.networking.RemoteApiImpl
import com.mikeschvedov.whatshouldiwatch.data.repository.ContentMediator
import com.mikeschvedov.whatshouldiwatch.data.repository.ContentMediatorImpl
import com.mikeschvedov.whatshouldiwatch.data.repository.MediaRepository
import com.mikeschvedov.whatshouldiwatch.data.repository.MediaRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//מתאם עבור ממשק,
//אומר למערכת מאיזו מחלקה ליצור מופע כשמבקשים את הממשק

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton //fine tune if this specific method/properties is singleton or not
    // so we dont really need this here if the component is singleton as well
    abstract fun bindRemoteApi(remoteApiImp: RemoteApiImpl): RemoteApi


    @Binds
    @Singleton //fine tune if this specific method/properties is singleton or not
    // so we dont really need this here if the component is singleton as well
    abstract fun provideRepository(movieRepositoryImpl: MediaRepositoryImpl): MediaRepository

    @Binds
    @Singleton
    abstract fun bindMediaProvider(contentMediator: ContentMediatorImpl) : ContentMediator
}
