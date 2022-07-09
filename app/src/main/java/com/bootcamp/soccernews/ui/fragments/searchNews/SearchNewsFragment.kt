package com.bootcamp.soccernews.ui.fragments.searchNews

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bootcamp.soccernews.R
import com.bootcamp.soccernews.data.database.local.NewsDataBase
import com.bootcamp.soccernews.data.repository.NewsRepository
import com.bootcamp.soccernews.databinding.SearchNewsFragmentBinding
import com.bootcamp.soccernews.ui.adapter.NewsAdapter
import com.bootcamp.soccernews.factory.SearchNewsViewModelProviderFactory
import com.bootcamp.soccernews.utils.Constants
import com.bootcamp.soccernews.utils.Constants.Companion.SEARCH_NEWS_DELAY
import com.bootcamp.soccernews.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment() {

    private var _binding: SearchNewsFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchAdapter: NewsAdapter
    private lateinit var viewModel: SearchNewsViewModel
    private lateinit var recyclerSearchView: RecyclerView
    private lateinit var searchNewsText: SearchView
    private lateinit var searchText: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = SearchNewsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupViewModel()
        setupRecyclerViewAdapter()
        observeNews()
        setupSearchText()
        setupNewsForNavigate()

    }

    private fun setupNewsForNavigate() {
        searchAdapter.setOnItemClickListener { news ->
            val bundle = Bundle().apply {
                putSerializable("news", news)
            }
            findNavController().navigate(
                R.id.action_navigation_search_news_to_navigation_page_news,
                bundle
            )
        }
    }

    private fun setupSearchText() {
        var job: Job? = null

        searchNewsText = binding.searhViewNews

        searchNewsText.onQueryTextChanged { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchForNews(editable)
                        searchText = editable
                    }
                }
            }
        }


    }

    inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {
        this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listener(newText.orEmpty())
                return true
            }
        })
    }

    private fun observeNews() {
        viewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgress()
                    response.data?.let { newsResponse ->
                        searchAdapter.differ.submitList(newsResponse.news)
                        val totalPage = newsResponse.totalResults / Constants.LIST_PAGE_SIZE + 2
                        isLastPage = viewModel.pageNumber == totalPage
                        if (isLastPage) {
                            recyclerSearchView.setPadding(0, 0, 0, 0)
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

    private fun setupRecyclerViewAdapter() {
        searchAdapter = NewsAdapter()

        recyclerSearchView = binding.recyclerSearchNews
        recyclerSearchView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }

    }

    private fun setupViewModel() {

        val connectivityManager = activity?.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val newsRepository = NewsRepository(NewsDataBase(requireContext()))
        val viewModelProviderFactory =
            SearchNewsViewModelProviderFactory(connectivityManager, newsRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[SearchNewsViewModel::class.java]

    }


    private fun hideProgress() {
        binding.progressBarSearchNews.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgress() {
        binding.progressBarSearchNews.visibility = View.VISIBLE
        isLoading = true
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
            val isTotalMoreThanVisible = totalItemCount >= Constants.LIST_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.searchForNews(searchText)
                isScrolling = false
            }

        }
    }

}