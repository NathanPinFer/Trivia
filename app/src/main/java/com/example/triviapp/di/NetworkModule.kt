package com.example.triviapp.di

import com.example.triviapp.data.TriviaApiService
import com.example.triviapp.data.response.TriviaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://opentdb.com/"

    @Provides
    @Singleton
    fun provideTriviaRepository(api: TriviaApiService): TriviaRepository =
        TriviaRepository(api)


    @Provides
    fun provideApiService(retrofit: Retrofit): TriviaApiService =
        retrofit.create(TriviaApiService::class.java)

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient).build()

    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().build()
}