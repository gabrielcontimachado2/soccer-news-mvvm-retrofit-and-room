package com.bootcamp.soccernews.ui.fragments.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bootcamp.soccernews.data.database.local.NewsDataBase
import com.bootcamp.soccernews.data.repository.NewsRepository
import com.bootcamp.soccernews.ui.adapter.NewsAdapter
import com.bootcamp.soccernews.databinding.FragmentNewsBinding
import com.bootcamp.soccernews.ui.MainActivity
import com.bootcamp.soccernews.ui.NewsViewModel
import com.bootcamp.soccernews.ui.factory.NewsViewModelProviderFactory
import com.bootcamp.soccernews.utils.Resource

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setupViewModel()
        setupRecyclerViewAdapter()

        viewModel.soccerNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgress()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.news)
                    }
                }
                is Resource.Error -> {
                    hideProgress()
                    response.message?.let { message ->
                        Toast.makeText(context, "ERRO $message", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }

        }


    }

    private fun setupViewModel() {
        val newsRepository = NewsRepository()
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]

    }

    private fun setupRecyclerViewAdapter() {
        newsAdapter = NewsAdapter()

        val newsRecyclerView: RecyclerView = binding.recyclerNews

        newsRecyclerView.setHasFixedSize(true)
        newsRecyclerView.layoutManager = LinearLayoutManager(context)
        newsRecyclerView.adapter = newsAdapter


    }


    private fun newsObserve() {

    }

    private fun hideProgress() {
        binding.progressBarNews.visibility = View.INVISIBLE
    }

    private fun showProgress() {
        binding.progressBarNews.visibility = View.VISIBLE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}