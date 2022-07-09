package com.bootcamp.soccernews.factory

import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bootcamp.soccernews.data.repository.NewsRepository
import com.bootcamp.soccernews.ui.fragments.searchNews.SearchNewsViewModel

class SearchNewsViewModelProviderFactory(
    private val connectivityManager: ConnectivityManager,
    val newsRepository: NewsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchNewsViewModel(connectivityManager, newsRepository) as T
    }
}