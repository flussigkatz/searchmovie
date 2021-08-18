package xyz.flussikatz.searchmovie.domain

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xyz.flussikatz.searchmovie.BuildConfig
import xyz.flussikatz.searchmovie.data.ApiConstants
import xyz.flussikatz.searchmovie.data.TmdbApi
import java.util.concurrent.TimeUnit

class Remote {
    val okHttpClient = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        })
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val retrofitService = retrofit.create(TmdbApi::class.java)
}