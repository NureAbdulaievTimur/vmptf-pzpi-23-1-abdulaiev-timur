package com.example.blogapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSearch: Button
    private lateinit var btnLogin: Button
    private lateinit var btnAddArticle: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArticleAdapter

    private var currentToken: String? = null

    private val filterCategoriesMap = mapOf(
        "Усі категорії" to null,
        "Технології" to "tech",
        "Фронтенд" to "frontend",
        "Загальне" to "general"
    )

    private val apiCategories = arrayOf("tech", "frontend", "general")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etSearch = findViewById(R.id.etSearch)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSearch = findViewById(R.id.btnSearch)
        btnLogin = findViewById(R.id.btnLogin)
        btnAddArticle = findViewById(R.id.btnAddArticle)
        recyclerView = findViewById(R.id.recyclerView)

        val filterAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            filterCategoriesMap.keys.toTypedArray()
        )
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = filterAdapter

        adapter = ArticleAdapter(
            emptyList(),
            { article -> showArticleDialog(article) },
            { article -> deleteArticle(article.id) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchArticles(null, null)

        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().takeIf { it.isNotBlank() }
            val selectedKey = spinnerCategory.selectedItem.toString()
            val category = filterCategoriesMap[selectedKey]
            fetchArticles(query, category)
        }

        btnLogin.setOnClickListener { showLoginDialog() }
        btnAddArticle.setOnClickListener { showArticleDialog(null) }
    }

    private fun fetchArticles(searchQuery: String?, category: String?) {
        lifecycleScope.launch {
            try {
                val articles = RetrofitClient.api.getArticles(searchQuery, category)
                adapter.updateData(articles)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Помилка завантаження", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoginDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_login, null)
        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)

        AlertDialog.Builder(this)
            .setTitle("Авторизація")
            .setView(view)
            .setPositiveButton("Увійти") { _, _ ->
                val user = etUsername.text.toString()
                val pass = etPassword.text.toString()
                handleLogin(user, pass)
            }
            .setNegativeButton("Скасувати", null)
            .show()
    }

    private fun handleLogin(user: String, pass: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.login(LoginRequest(user, pass))
                if (response.token != null) {
                    currentToken = response.token
                    btnLogin.text = "Авторизовано"
                    Toast.makeText(this@MainActivity, "Успішний вхід", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Невірні дані", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Помилка з'єднання", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showArticleDialog(article: Article?) {
        if (currentToken == null) {
            Toast.makeText(this, "Потрібна авторизація", Toast.LENGTH_SHORT).show()
            return
        }

        val view = layoutInflater.inflate(R.layout.dialog_article, null)
        val etTitle = view.findViewById<EditText>(R.id.etDialogTitle)
        val etContent = view.findViewById<EditText>(R.id.etDialogContent)
        val spinnerCat = view.findViewById<Spinner>(R.id.spinnerDialogCategory)

        val catAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, apiCategories)
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCat.adapter = catAdapter

        if (article != null) {
            etTitle.setText(article.title)
            etContent.setText(article.content)
            val index = apiCategories.indexOf(article.category)
            if (index >= 0) spinnerCat.setSelection(index)
        }

        val dialogTitle = if (article == null) "Нова стаття" else "Редагування статті"

        AlertDialog.Builder(this)
            .setTitle(dialogTitle)
            .setView(view)
            .setPositiveButton("Зберегти") { _, _ ->
                val req = ArticleRequest(
                    etTitle.text.toString(),
                    etContent.text.toString(),
                    spinnerCat.selectedItem.toString()
                )
                if (article == null) createArticle(req) else editArticle(article.id, req)
            }
            .setNegativeButton("Скасувати", null)
            .show()
    }

    private fun createArticle(request: ArticleRequest) {
        if (currentToken == null) return
        lifecycleScope.launch {
            try {
                RetrofitClient.api.createArticle("Bearer $currentToken", request)
                refreshCurrentList()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Помилка створення", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editArticle(articleId: Long, request: ArticleRequest) {
        if (currentToken == null) return
        lifecycleScope.launch {
            try {
                RetrofitClient.api.editArticle("Bearer $currentToken", articleId, request)
                refreshCurrentList()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Помилка редагування", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteArticle(articleId: Long) {
        if (currentToken == null) {
            Toast.makeText(this, "Потрібна авторизація", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            try {
                RetrofitClient.api.deleteArticle("Bearer $currentToken", articleId)
                refreshCurrentList()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Помилка видалення", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refreshCurrentList() {
        val query = etSearch.text.toString().takeIf { it.isNotBlank() }
        val selectedKey = spinnerCategory.selectedItem.toString()
        val category = filterCategoriesMap[selectedKey]
        fetchArticles(query, category)
    }
}