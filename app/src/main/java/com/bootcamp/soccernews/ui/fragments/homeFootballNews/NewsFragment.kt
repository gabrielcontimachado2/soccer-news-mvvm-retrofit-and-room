package com.bootcamp.soccernews.ui.fragments.homeFootballNews

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bootcamp.soccernews.R
import com.bootcamp.soccernews.data.database.local.NewsDataBase
import com.bootcamp.soccernews.data.repository.NewsRepository
import com.bootcamp.soccernews.ui.adapter.NewsAdapter
import com.bootcamp.soccernews.databinding.FragmentNewsBinding
import com.bootcamp.soccernews.factory.NewsViewModelProviderFactory
import com.bootcamp.soccernews.utils.Constants.Companion.LIST_PAGE_SIZE
import com.bootcamp.soccernews.utils.Resource

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var newsRecyclerView: RecyclerView

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
        observeNews()
        setupNewsForNavigate()
        setUpNewsForFavoriteButton()
    }

    private fun setUpNewsForFavoriteButton() {
        newsAdapter.setOnFavoriteClickListener { news ->
            viewModel.saveNews(news)

            Toast.makeText(
                this.requireContext(),
                "A notícia foi salva com sucesso!!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setupNewsForNavigate() {
        newsAdapter.setOnItemClickListener { news ->
            val bundle = Bundle().apply {
                putSerializable("news", news)
            }
            findNavController().navigate(
                R.id.action_navigation_news_home_to_navigation_page_news,
                bundle
            )
        }
    }

    private fun observeNews() {
        viewModel.soccerNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgress()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.news.toList())
                        val totalPage = newsResponse.totalResults / LIST_PAGE_SIZE + 2
                        isLastPage = viewModel.pageNumber == totalPage
                        if (isLastPage) {
                            newsRecyclerView.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgress()
                    response.message?.let { message ->
                        Toast.makeText(activity, "Ocorreu um erro: $message", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }

        }

    }

    private fun setupViewModel() {

        val connectivityManager = activity?.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val newsRepository = NewsRepository(NewsDataBase(this.requireContext()))
        val viewModelProviderFactory =
            NewsViewModelProviderFactory(connectivityManager, newsRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]


    }

    private fun setupRecyclerViewAdapter() {
        newsAdapter = NewsAdapter()

        newsRecyclerView = binding.recyclerNews

        newsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
            addOnScrollListener(this@NewsFragment.scrollListener)
        }
    }

    private fun hideProgress() {
        binding.progressBarNews.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgress() {
        binding.progressBarNews.visibility = View.VISIBLE
        isLoading = true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val firstItemVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstItemVisiblePosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstItemVisiblePosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= LIST_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.getSoccerNews()
                isScrolling = false
            }

        }
    }

}