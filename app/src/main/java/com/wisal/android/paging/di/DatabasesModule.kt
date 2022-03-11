package com.wisal.android.paging.di

import android.content.Context
import com.wisal.android.paging.data.db.CharacterDatabase
import com.wisal.android.paging.data.db.EpisodeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabasesModule {

    @Provides
    @Singleton
    fun provideCharacterDatabase(@ApplicationContext context: Context): CharacterDatabase {
        return CharacterDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideEpisodesDatabase(@ApplicationContext context: Context): EpisodeDatabase {
        return EpisodeDatabase.getDatabase(context)
    }

}