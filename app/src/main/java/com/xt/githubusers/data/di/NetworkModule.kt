package com.xt.githubusers.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.xt.githubusers.BuildConfig
import com.xt.githubusers.data.remote.NetworkInterceptor
import com.xt.githubusers.data.remote.UserApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.LoggingEventListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideHttpInterceptor(): HttpLoggingInterceptor? {
        if (!BuildConfig.DEBUG) return null
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    fun provideHttpEventListener(): LoggingEventListener.Factory? {
        if (!BuildConfig.DEBUG) return null
        return LoggingEventListener.Factory()
    }


    @Singleton
    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor?,
        loggingEventListener: LoggingEventListener.Factory?,
        networkInterceptor: NetworkInterceptor
    ): OkHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(networkInterceptor)
        if (null != httpLoggingInterceptor) {
            addInterceptor(httpLoggingInterceptor)
        }
        if (null != loggingEventListener) {
            eventListenerFactory(loggingEventListener)
        }
    }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .hostnameVerifier { _, _ -> true }
        .build()

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Singleton
    @Provides
    fun provideHttpConverter(gson: Gson): GsonConverterFactory {
        val gsonConverter = GsonConverterFactory.create(gson)
        return gsonConverter
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverter: GsonConverterFactory,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(gsonConverter)
            .client(okHttpClient)
            .build()

    @Singleton
    @Provides
    fun provideUserApiService(retrofit: Retrofit): UserApiService = retrofit.create()
}