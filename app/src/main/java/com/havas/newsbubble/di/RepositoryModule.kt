package com.havas.newsbubble.di

import com.havas.newsbubble.data.repository.NewsRepositoryImpl
import com.havas.newsbubble.domain.repository.NewsRepository
import com.havas.newsbubble.domain.usecase.GetHeadlinesByCategoryUseCase
import com.havas.newsbubble.domain.usecase.GetHeadlinesByCategoryUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNewsRepository(impl: NewsRepositoryImpl): NewsRepository

    @Binds
    abstract fun bindGetHeadlinesByCategoryUseCase(
        impl: GetHeadlinesByCategoryUseCaseImpl,
    ): GetHeadlinesByCategoryUseCase
}
