package com.wisal.android.paging.data.paging.di

import com.wisal.android.paging.data.repository.Repository
import com.wisal.android.paging.di.RepositoryModule
import com.wisal.android.paging.service.FakeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton


@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
abstract class FakeRepositoryModule {

    @Binds
    @Singleton
    abstract fun provideFakeRepository(repo: FakeRepository): Repository

}