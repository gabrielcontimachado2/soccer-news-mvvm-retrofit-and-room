package com.bootcamp.soccernews.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bootcamp.soccernews.data.model.News
import com.bootcamp.soccernews.databinding.CardNewsBinding
import com.bumptech.glide.Glide

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {


    private val differCallBack = object : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = CardNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

        var news = differ.currentList[position]

        Glide.with(holder.itemView.context).load(news.urlToImage).into(holder.imageNews)
        holder.tittleNews.text = news.title
        holder.descriptionNews.text = news.description
        holder.newsDate.text = news.publishedAt

        holder.itemView.setOnClickListener {
            onCardClickListener?.let { it(news) }
        }

    }

    private var onCardClickListener: ((News) -> Unit)? = null

    fun setOnCardClickListener(listener: (News) -> Unit) {
        onCardClickListener = listener
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class NewsViewHolder(binding: CardNewsBinding) : RecyclerView.ViewHolder(binding.root) {

        var imageNews: ImageView = binding.imageViewNews
        var imageFavorite: ImageView = binding.imageViewBtnFavorite
        var imageShare: ImageView = binding.imageViewBtnShare
        var newsDate: TextView = binding.textViewNewsDate
        var tittleNews: TextView = binding.textViewTittleNews
        var descriptionNews: TextView = binding.textViewDescriptionNews
       // var buttonLink: Button = binding.buttonOpenNews

    }

}