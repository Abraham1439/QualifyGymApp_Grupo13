package com.example.qualifygym_grupo13.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RemoteModule {

    //URLs base de los microservicios
    private const val USUARIO_BASE_URL = "https://pjrm5vf1-8081.brs.devtunnels.ms/"


    //creamos un interceptor de logging para depurar tráfico HTTP
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    //construimos el cliente OkHttp con el interceptor
    private val okHttp = OkHttpClient.Builder()
        .addInterceptor (logging) //agregamos logging
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // Función genérica para crear instancias de Retrofit
    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    // Instancias de Retrofit para cada microservicio
    private val usuarioRetrofit: Retrofit = createRetrofit(USUARIO_BASE_URL)

    // APIs de los microservicios
    val usuarioApi: UsuarioApi = usuarioRetrofit.create(UsuarioApi::class.java)



}