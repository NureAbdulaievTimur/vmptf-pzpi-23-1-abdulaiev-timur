package com.example.blogapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

data class Article(
    val id: Long,
    val title: String,
    val content: String,
    val category: String,
    val comments: List<Comment>
)

data class Comment(val id: Long, val text: String)

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String?, val message: String?)
data class ArticleRequest(val title: String, val content: String, val category: String)

interface ApiService {
    @GET("/api/articles")
    suspend fun getArticles(
        @Query("search") search: String?,
        @Query("category") category: String?
    ): List<Article>

    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/api/articles")
    suspend fun createArticle(
        @Header("Authorization") token: String,
        @Body request: ArticleRequest
    ): Article

    @PUT("/api/articles/{id}")
    suspend fun editArticle(
        @Header("Authorization") token: String,
        @Path("id") articleId: Long,
        @Body request: ArticleRequest
    ): Article

    @DELETE("/api/articles/{id}")
    suspend fun deleteArticle(
        @Header("Authorization") token: String,
        @Path("id") articleId: Long
    ): Map<String, String>
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5000"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}