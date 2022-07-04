package com.bootcamp.soccernews.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bootcamp.soccernews.data.model.News

class NewsFragmentViewModel : ViewModel() {

    private val _news: MutableLiveData<List<News>> = MutableLiveData<List<News>>()
    val news: LiveData<List<News>>
        get() = _news



}