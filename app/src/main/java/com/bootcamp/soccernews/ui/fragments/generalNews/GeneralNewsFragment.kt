package com.bootcamp.soccernews.ui.fragments.generalNews


import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bootcamp.soccernews.R
import com.bootcamp.soccernews.data.database.local.NewsDataBase
import com.bootcamp.soccernews.data.repository.NewsRepository
import com.bootcamp.soccernews.databinding.GeneralNewsFragmentBinding
import com.bootcamp.soccernews.ui.adapter.NewsAdapter
import com.bootcamp.soccernews.factory.GeneralNewsViewModelFactory
import com.bootcamp.soccernews.utils.Constants
import com.bootcamp.soccernews.utils.Resource


class GeneralNewsFragment : Fragment() {

    private lateinit var newsAdapter: NewsAdapter
    private var _binding: GeneralNewsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: GeneralNewsViewModel
    private lateinit var generalNewsRecycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = GeneralNewsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViewAdapter()
        setupViewModel()
        observeGeneralNews()
        setupNewsForNavigate()
        setupFavoriteButton()

    }

    private fun setupFavoriteButton() {
        newsAdapter.setOnFavoriteClickListener { news ->
            viewModel.saveNews(news)

            Toast.makeText(requireContext(), "Notícia foi salva com sucesso!!", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun setupNewsForNavigate() {
        newsAdapter.setOnItemClickListener { news ->
            val bundle = Bundle().apply {
                putSerializable("news", news)
            }
            findNavController().navigate(
                R.id.action_navigation_general_news_to_navigation_page_news,
                bundle
            )
        }
    }

    private fun observeGeneralNews() {
        viewModel.generalNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgress()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.news.toList())
                        val totalPage = newsResponse.totalResults / Constants.LIST_PAGE_SIZE + 2
                        isLastPage = viewModel.pageNumber == totalPage
                        if (isLastPage) {
                            generalNewsRecycler.setPadding(0, 0, 0, 0)
                        }
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


    private fun hideProgress() {
        binding.progressBarGeneralNews.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgress() {
        binding.progressBarGeneralNews.visibility = View.VISIBLE
        isLoading = true
    }


    private fun setupViewModel() {
        val connectivityManager = activity?.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val newsRepository = NewsRepository(NewsDataBase(requireContext()))
        val viewModelProviderFactory =
            GeneralNewsViewModelFactory(connectivityManager, newsRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[GeneralNewsViewModel::class.java]
    }

    private fun setupRecyclerViewAdapter() {
        newsAdapter = NewsAdapter()

        generalNewsRecycler = binding.recyclerGeneralNews
        generalNewsRecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
            addOnScrollListener(this@GeneralNewsFragment.scrollListener)
        }
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
                viewModel.getGeneralNewsBrasil()
                isScrolling = false
            } else {
                generalNewsRecycler.setPadding(0, 0, 0, 0)
            }

        }
    }

}