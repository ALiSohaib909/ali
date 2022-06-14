package com.pins.infinity.modules

import com.pins.infinity.BuildConfig
import com.pins.infinity.api.NetworkManager
import com.pins.infinity.api.NetworkManagerImpl
import com.pins.infinity.api.ResponseStatusInterceptor
import com.pins.infinity.api.deviceapi.DeviceApiManager
import com.pins.infinity.api.deviceapi.DeviceApiManagerImpl
import com.pins.infinity.api.paymentapi.PaymentApiManager
import com.pins.infinity.api.paymentapi.PaymentApiManagerImpl
import com.pins.infinity.api.services.DeviceApiService
import com.pins.infinity.api.services.PaymentApiService
import com.pins.infinity.api.services.RecoveryApiService
import com.pins.infinity.api.services.UserApiService
import com.pins.infinity.api.userapi.UserApiManager
import com.pins.infinity.api.userapi.UserApiManagerImpl
import com.pins.infinity.database.SettingsManager
import com.pins.infinity.modules.ModuleConstants.TIMEOUT
import com.pins.infinity.utility.ApiConstants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Pavlo Melnyk on 29.11.2018.
 */

object ModuleConstants {
    const val TIMEOUT = 40000L
    const val ACCESS_TOKEN_VALUE = "Bearer "
}

val networkModule = module {
    singleBy<NetworkManager, NetworkManagerImpl>()
    single {
        createWebService<PaymentApiService>(
                createOkHttpClient(headerInterceptor = createHeader(get())),
                BuildConfig.SERVER_URL,
                GsonConverterFactory.create())
    }
    single { PaymentApiManagerImpl(androidContext(), get()) as PaymentApiManager }

    single {
        createWebService<UserApiService>(
                createOkHttpClient(headerInterceptor = createHeader(get())),
                BuildConfig.SERVER_URL,
                GsonConverterFactory.create())
    }
    single { UserApiManagerImpl(androidContext(), get()) as UserApiManager }

    single {
        createWebService<DeviceApiService>(
                createOkHttpClient(headerInterceptor = createHeader(get())),
                BuildConfig.SERVER_URL,
                GsonConverterFactory.create())
    }
    single { DeviceApiManagerImpl(androidContext(), get()) as DeviceApiManager }
    single {
        createWebService<RecoveryApiService>(
                createOkHttpClient(headerInterceptor = createHeader(get())),
                BuildConfig.SERVER_URL,
                GsonConverterFactory.create())
    }
}

fun createHeader(settingsManager: SettingsManager): Interceptor {
    return Interceptor { chain ->
        val newRequest = chain.request().newBuilder()
                .addHeader(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE)
                .addHeader(ApiConstants.AUTH_TOKEN_KEY, settingsManager.accessToken)
                .build()
        chain.proceed(newRequest)
    }
}

fun createOkHttpClient(headerInterceptor: Interceptor): OkHttpClient {
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val statusInterceptor = ResponseStatusInterceptor()

    return OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor)
            .addInterceptor(statusInterceptor)
            .build()
}

inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String, factory: Converter.Factory): T {
    val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(factory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    return retrofit.create(T::class.java)
}