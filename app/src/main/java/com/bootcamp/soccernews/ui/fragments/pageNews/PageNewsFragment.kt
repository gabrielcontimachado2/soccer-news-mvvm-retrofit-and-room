package com.bootcamp.soccernews.ui.fragments.pageNews

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.bootcamp.soccernews.databinding.PageNewsFragmentBinding

class PageNewsFragment : Fragment() {

    private val args: PageNewsFragmentArgs by navArgs()

    private var _binding: PageNewsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PageNewsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val news = args.news
        var webViewNews = binding.webViewNews

        webViewNews.apply {
            webViewClient = WebViewClient()
            news.url?.let { loadUrl(it) }
        }

    }

}