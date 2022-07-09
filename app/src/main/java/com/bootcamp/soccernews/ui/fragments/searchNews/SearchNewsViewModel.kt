package com.bootcamp.soccernews.ui.fragments.searchNews

import android.net.ConnectivityManager
import androidx.lifecycle.*
import com.bootcamp.soccernews.data.model.NewsResponse
import com.bootcamp.soccernews.data.repository.NewsRepository
import com.bootcamp.soccernews.utils.NetworkCheck
import com.bootcamp.soccernews.utils.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response


class SearchNewsViewModel(
    private val connectivityManager: ConnectivityManager,
    val newsRepository: NewsRepository,
) : ViewModel() {

    private val _searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNews: LiveData<Resource<NewsResponse>>
        get() = _searchNews
    var pageNumber = 1
    private var searchNewsResponse: NewsResponse? = null


    fun searchForNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        _searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.searchForNews(searchQuery, pageNumber)
                _searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                _searchNews.postValue(Resource.Error("Sem conexão com a internet"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _searchNews.postValue(Resource.Error("Falha de rede"))
                else -> _searchNews.postValue(Resource.Error("Erro de conversão"))

            }
        }
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                pageNumber++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldSearchNews = searchNewsResponse?.news
                    val newSearchNews = resultResponse.news
                    oldSearchNews?.addAll(newSearchNews)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun hasInternetConnection(): Boolean {

        val networkCheck = NetworkCheck()

        return networkCheck.hasInternetConnection(connectivityManager)
    }

}