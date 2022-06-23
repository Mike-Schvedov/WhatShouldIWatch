package com.mikeschvedov.whatshouldiwatch.di

import com.mikeschvedov.whatshouldiwatch.networking.RemoteApi
import com.mikeschvedov.whatshouldiwatch.networking.RemoteApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
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
}
