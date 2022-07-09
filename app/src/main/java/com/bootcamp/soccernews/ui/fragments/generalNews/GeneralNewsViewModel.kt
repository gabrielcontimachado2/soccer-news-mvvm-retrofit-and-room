package com.bootcamp.soccernews.ui.fragments.generalNews

import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bootcamp.soccernews.data.model.News
import com.bootcamp.soccernews.data.model.NewsResponse
import com.bootcamp.soccernews.data.repository.NewsRepository
import com.bootcamp.soccernews.utils.NetworkCheck
import com.bootcamp.soccernews.utils.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class GeneralNewsViewModel(
    private val connectivityManager: ConnectivityManager,
    var newsRepository: NewsRepository
) : ViewModel() {

    private val _generalNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val generalNews: LiveData<Resource<NewsResponse>>
        get() = _generalNews
    var pageNumber = 1
    var generalNewsResponse: NewsResponse? = null


    init {
        getGeneralNewsBrasil()
    }

    fun getGeneralNewsBrasil() = viewModelScope.launch {
        safeCallGeneralNewsBrasil()
    }

    private suspend fun safeCallGeneralNewsBrasil() {
        _generalNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getGeneralNewsBrasil(pageNumber)
                _generalNews.postValue(handleGeneralNewsResponse(response))
            } else {
                _generalNews.postValue(Resource.Error("Sem conexão com a internet"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _generalNews.postValue(Resource.Error("Falha de Rede"))
                else -> _generalNews.postValue(Resource.Error("Erro de conversão"))
            }
        }
    }

    private fun handleGeneralNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                pageNumber++
                if (generalNewsResponse == null) {
                    generalNewsResponse = resultResponse
                } else {
                    val oldGeneralNews = generalNewsResponse?.news
                    val newGeneralNews = resultResponse.news
                    oldGeneralNews?.addAll(newGeneralNews)
                }
                return Resource.Success(generalNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())

    }

    fun saveNews(news: News) = viewModelScope.launch {
        newsRepository.upsertNewsDataBase(news)
    }

    private fun hasInternetConnection(): Boolean {

        val networkCheck = NetworkCheck()

        return networkCheck.hasInternetConnection(connectivityManager)
    }
}