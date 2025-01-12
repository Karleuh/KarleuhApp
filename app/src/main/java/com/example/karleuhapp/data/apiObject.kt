package com.example.karleuhapp.data

import android.util.Log

import com.example.karleuhapp.list.TasksWebService
import com.example.karleuhapp.user.UserViewModel
import androidx.activity.viewModels
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object Api {
    var TOKEN : String? = null


    private val retrofit by lazy {
        // client HTTP
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor { chain ->
                val token = TOKEN
                if (token == null) {
                    Log.e("Interceptor", "Token is null. Request will be sent without Authorization header.")

                }
                val newRequestBuilder = chain.request().newBuilder()
                if (token != null) {
                    newRequestBuilder.addHeader("Authorization", "Bearer $token")
                }
                val newRequest = newRequestBuilder.build()
                chain.proceed(newRequest)
            }
            .build()

        val jsonSerializer = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        Retrofit.Builder()
            .baseUrl("https://api.todoist.com/")
            .client(okHttpClient)
            .addConverterFactory(jsonSerializer.asConverterFactory("application/json".toMediaType()))
            .build()
    }


    val userWebService : UserWebService by lazy {
        retrofit.create(UserWebService::class.java)
    }

    val tasksWebService : TasksWebService by lazy {
        retrofit.create(TasksWebService::class.java)
    }
}