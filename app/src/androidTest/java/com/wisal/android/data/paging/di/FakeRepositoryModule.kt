package com.wisal.android.data.paging.di

import com.wisal.android.data.repository.Repository
import com.wisal.android.di.RepositoryModule
import com.wisal.android.service.FakeRepository
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