package xyz.flussigkatz.remote_module.di

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xyz.flussigkatz.remote_module.BuildConfig
import xyz.flussigkatz.remote_module.ConstantsRemote
import xyz.flussigkatz.remote_module.ConstantsRemote.CALL_TIMEOUT
import xyz.flussigkatz.remote_module.ConstantsRemote.READ_TIMEOUT
import xyz.flussigkatz.remote_module.TmdbApi
import java.util.concurrent.TimeUnit

@Module
interface RemoteModule {
    companion object {
        @Provides
        @RemoteComponentScope
        fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .callTimeout(CALL_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            })
            .build()

        @Provides
        @RemoteComponentScope
        fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
            .baseUrl(ConstantsRemote.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        @Provides
        @RemoteComponentScope
        fun provideTmdbApi(retrofit: Retrofit): TmdbApi = retrofit.create(TmdbApi::class.java)
    }
}