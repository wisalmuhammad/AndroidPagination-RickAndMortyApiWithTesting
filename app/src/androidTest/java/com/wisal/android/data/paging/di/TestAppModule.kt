package com.wisal.android.data.paging.di

import android.content.Context
import androidx.room.Room
import com.wisal.android.data.db.CharacterDatabase
import com.wisal.android.data.db.EpisodeDatabase
import com.wisal.android.service.FakeApiService
import com.wisal.android.service.FakeApiServiceData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Singleton
    @Provides
    @Named("characters_test_db")
    fun provideCharacterInMemoryDb(@ApplicationContext context: Context): CharacterDatabase {
        return Room.inMemoryDatabaseBuilder(
            context,
            CharacterDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Singleton
    @Provides
    @Named("episodes_test_db")
    fun provideEpisodeInMemoryDb(@ApplicationContext context: Context): EpisodeDatabase {
        return Room.inMemoryDatabaseBuilder(
            context,
            EpisodeDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Singleton
    @Provides
    fun provideApiFakeServiceData() = FakeApiServiceData()

    @Singleton
    @Provides
    fun provideApiService(fakeData: FakeApiServiceData): FakeApiService {
        return FakeApiService().apply {
            addCharacters(1,fakeData.fakedPagedResponses[0])
            addCharacters(2,fakeData.fakedPagedResponses[1])
            addCharacters(1,fakeData.fakedPagedResponses[2])
        }
    }
}