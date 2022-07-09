package com.bootcamp.soccernews.ui.fragments.Favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bootcamp.soccernews.R
import com.bootcamp.soccernews.data.database.local.NewsDataBase
import com.bootcamp.soccernews.data.repository.NewsRepository
import com.bootcamp.soccernews.databinding.FragmentFavoritesBinding
import com.bootcamp.soccernews.ui.adapter.FavoriteNewsAdapter
import com.bootcamp.soccernews.factory.FavoriteNewsViewModelProviderFactory
import com.google.android.material.snackbar.Snackbar

class FavoritesFragment : Fragment() {


    private lateinit var favoriteNewsAdapter: FavoriteNewsAdapter
    private lateinit var favoriteNewsRecycler: RecyclerView
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupRecyclerViewAdapter()
        setupNewsForNavigate()
        setupViewModel()
        observeNews()
        itemTouchHelper()
    }

    private fun itemTouchHelper() {
        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(favoriteNewsRecycler)
        }
    }

    val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val news = favoriteNewsAdapter.differ.currentList[position]

            viewModel.deleteNews(news)
            Snackbar.make(view!!, "A notícia foi deletada com sucesso!!", Snackbar.LENGTH_LONG)
                .apply {
                    setAction("Desfazer") {
                        viewModel.saveNews(news)
                        Toast.makeText(requireContext(), "Notícia salva com sucesso!!", Toast.LENGTH_LONG).show()
                    }
                    show()
                }

        }

    }


    private fun observeNews() {
        viewModel.getAllNews().observe(viewLifecycleOwner) {
            favoriteNewsAdapter.differ.submitList(it)
        }
    }

    private fun setupViewModel() {
        val newsRepository = NewsRepository(NewsDataBase(this.requireContext()))
        val viewModelProviderFactory = FavoriteNewsViewModelProviderFactory(newsRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[FavoritesViewModel::class.java]

    }

    private fun setupRecyclerViewAdapter() {
        favoriteNewsAdapter = FavoriteNewsAdapter()

        favoriteNewsRecycler = binding.recyclerFavoriteNews
        favoriteNewsRecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = favoriteNewsAdapter
        }
    }

    private fun setupNewsForNavigate() {
        favoriteNewsAdapter.setOnItemClickListener { news ->
            val bundle = Bundle().apply {
                putSerializable("news", news)
            }
            findNavController().navigate(
                R.id.action_navigation_favorite_news_to_navigation_page_news,
                bundle
            )
        }
    }

}