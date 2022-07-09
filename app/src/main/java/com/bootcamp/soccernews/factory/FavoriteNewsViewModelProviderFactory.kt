package com.bootcamp.soccernews.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bootcamp.soccernews.data.repository.NewsRepository
import com.bootcamp.soccernews.ui.fragments.Favorite.FavoritesViewModel

class FavoriteNewsViewModelProviderFactory(
    val newsRepository: NewsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritesViewModel(newsRepository) as T
    }
}