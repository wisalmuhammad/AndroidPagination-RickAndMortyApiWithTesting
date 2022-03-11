package com.wisal.android.paging.di

import com.wisal.android.paging.data.db.CharacterDatabase
import com.wisal.android.paging.data.db.EpisodeDatabase
import com.wisal.android.paging.data.repository.Repository
import com.wisal.android.paging.data.repository.RepositoryImp
import com.wisal.android.paging.data.service.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideRepositoryImp(
        apiService: ApiService,
        characterDatabase: CharacterDatabase,
        episodeDatabase: EpisodeDatabase
    ): Repository {
        return RepositoryImp(
            apiService = apiService,
            characterDatabase = characterDatabase,
            episodesDatabase = episodeDatabase
        )
    }

}