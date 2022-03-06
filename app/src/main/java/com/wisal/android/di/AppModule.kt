package com.wisal.android.di

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wisal.android.data.db.CharacterDatabase
import com.wisal.android.data.db.EpisodeDatabase
import com.wisal.android.data.repository.Repository
import com.wisal.android.data.repository.RepositoryImp
import com.wisal.android.data.service.ApiService
import com.wisal.android.util.NetworkConnectionInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class CharactersDatabase

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class EpisodesDatabase

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RemoteApiService

    @Provides
    @Singleton
    fun provideHttpLoggingInter(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(
            HttpLoggingInterceptor.Level.BODY
        )
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            interceptors().add(httpLoggingInterceptor)
            interceptors().add(NetworkConnectionInterceptor(context))
        }.build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .serializeNulls()
            .setLenient()
            .create()

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson,okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl("https://rickandmortyapi.com/api/")
            addConverterFactory(GsonConverterFactory.create(gson))
            client(okHttpClient)
        }.build()
    }

    @Provides
    @Singleton
    @RemoteApiService
    fun provideApiService(retrofit: Retrofit): ApiService {
        return  retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    @CharactersDatabase
    fun provideCharacterDatabase(@ApplicationContext context: Context): CharacterDatabase {
        return CharacterDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    @EpisodesDatabase
    fun provideEpisodesDatabase(@ApplicationContext context: Context): EpisodeDatabase {
        return EpisodeDatabase.getDatabase(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepositoryImp(
        @AppModule.RemoteApiService apiService: ApiService,
        @AppModule.CharactersDatabase characterDatabase: CharacterDatabase,
        @AppModule.EpisodesDatabase episodeDatabase: EpisodeDatabase
    ): Repository {
        return RepositoryImp(
            apiService = apiService,
            characterDatabase = characterDatabase,
            episodesDatabase = episodeDatabase
        )
    }

}