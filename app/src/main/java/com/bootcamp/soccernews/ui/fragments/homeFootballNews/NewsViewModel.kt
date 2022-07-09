package com.bootcamp.soccernews.ui.fragments.homeFootballNews



import android.net.ConnectivityManager
import androidx.lifecycle.*
import com.bootcamp.soccernews.data.model.News
import com.bootcamp.soccernews.data.model.NewsResponse
import com.bootcamp.soccernews.data.repository.NewsRepository
import com.bootcamp.soccernews.utils.NetworkCheck
import com.bootcamp.soccernews.utils.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(
    private val connectivityManager: ConnectivityManager,
    val newsRepository: NewsRepository
) : ViewModel() {


    private val _soccerNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val soccerNews: LiveData<Resource<NewsResponse>>
        get() = _soccerNews

    var soccerNewsResponse: NewsResponse? = null
    var pageNumber = 1

    init {
        getSoccerNews()
    }

    fun getSoccerNews() = viewModelScope.launch {
        safeSoccerNewsCall()
    }

    private fun handleSoccerNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                pageNumber++
                if (soccerNewsResponse == null) {
                    soccerNewsResponse = resultResponse
                } else {
                    val oldSoccerNews = soccerNewsResponse?.news
                    val newSoccerNews = resultResponse.news
                    oldSoccerNews?.addAll(newSoccerNews)
                }
                return Resource.Success(soccerNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())

    }

    fun saveNews(news: News) = viewModelScope.launch {
        newsRepository.upsertNewsDataBase(news)
    }

    private suspend fun safeSoccerNewsCall() {
        _soccerNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getSoccerNews(pageNumber)
                _soccerNews.postValue(handleSoccerNewsResponse(response))
            } else {
                _soccerNews.postValue(Resource.Error("Sem conexão com a internet"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _soccerNews.postValue(Resource.Error("Falha de rede"))
                else -> {
                    _soccerNews.postValue(Resource.Error("Erro de conversão"))
                }
            }
        }
    }

    private fun hasInternetConnection(): Boolean {

        val connectionCheck = NetworkCheck()

        return connectionCheck.hasInternetConnection(connectivityManager)

    }

    //val connectivityManager = getApplication<MyApplication>().getSystemService(
    //    Context.CONNECTIVITY_SERVICE
    //) as ConnectivityManager
    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    //    val activeNetwork = connectivityManager.activeNetwork ?: return false
    //    val capabilities =
    //        connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    //    return when {
    //        capabilities.hasTransport(TRANSPORT_WIFI) -> true
    //        capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
    //        capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
    //        else -> false
    //    }
    //} else {
    //    connectivityManager.activeNetworkInfo?.run {
    //        return when (type) {
    //            TYPE_WIFI -> true
    //            TYPE_MOBILE -> true
    //            TYPE_ETHERNET -> true
    //            else -> false
    //        }
    //    }
    //}
    //return false


}