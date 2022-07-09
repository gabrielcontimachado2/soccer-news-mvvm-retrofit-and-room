package com.bootcamp.soccernews.ui.fragments.Favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bootcamp.soccernews.data.model.News

import com.bootcamp.soccernews.data.model.NewsResponse
import com.bootcamp.soccernews.data.repository.NewsRepository
import com.bootcamp.soccernews.utils.Resource
import kotlinx.coroutines.launch


class FavoritesViewModel(
    val newsRepository: NewsRepository
) : ViewModel() {

    private val _news: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val news: LiveData<Resource<NewsResponse>>
        get() = _news


    init {
        getAllNews()
    }

    fun getAllNews() = newsRepository.getNewsDataBase()

    fun deleteNews(news: News) = viewModelScope.launch {
        newsRepository.deleteNewsDataBase(news)
    }

    fun saveNews(news: News) = viewModelScope.launch {
        newsRepository.upsertNewsDataBase(news)
    }

}