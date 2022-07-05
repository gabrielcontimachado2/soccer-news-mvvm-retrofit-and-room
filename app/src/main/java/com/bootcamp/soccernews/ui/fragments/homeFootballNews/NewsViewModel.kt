package com.bootcamp.soccernews.ui.fragments.homeFootballNews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bootcamp.soccernews.data.model.NewsResponse
import com.bootcamp.soccernews.data.repository.NewsRepository
import com.bootcamp.soccernews.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response


class NewsViewModel(
    var newsRepository: NewsRepository
) : ViewModel() {

    private val _soccerNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val soccerNews: LiveData<Resource<NewsResponse>>
        get() = _soccerNews

    val pageNumber = 1

    init {
        getNews()
    }

    private fun getNews() = viewModelScope.launch {
        _soccerNews.postValue(Resource.Loading())
        val response = newsRepository.getAllNews()
        _soccerNews.postValue(handleSoccerNewsResponse(response))
    }

    private fun handleSoccerNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())

    }


}