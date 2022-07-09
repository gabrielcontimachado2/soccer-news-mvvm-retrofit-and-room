package com.bootcamp.soccernews.factory

import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bootcamp.soccernews.data.repository.NewsRepository
import com.bootcamp.soccernews.ui.fragments.homeFootballNews.NewsViewModel

class NewsViewModelProviderFactory(
    private val connectivityManager: ConnectivityManager,
    val newsRepository: NewsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(connectivityManager, newsRepository) as T
    }

}