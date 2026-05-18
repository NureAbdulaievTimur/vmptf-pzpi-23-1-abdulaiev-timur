package com.example.blogapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ArticleAdapter(
    private var articles: List<Article>,
    private val onEditClick: (Article) -> Unit,
    private val onDeleteClick: (Article) -> Unit
) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.tvTitle.text = article.title
        holder.tvCategory.text = article.category
        holder.tvContent.text = article.content

        holder.btnEdit.setOnClickListener { onEditClick(article) }
        holder.btnDelete.setOnClickListener { onDeleteClick(article) }
    }

    override fun getItemCount(): Int = articles.size

    fun updateData(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }
}