package com.airposted.bitoronbd_deliveryman.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentWebViewBinding
import com.airposted.bitoronbd_deliveryman.utils.AppHelper

class WebViewFragment : Fragment(), IOnBackPressed {
    private lateinit var binding: FragmentWebViewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        binding.toolbar.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.progressBar.max = 100
        binding.contactWebView.webViewClient = object : WebViewClient() {}
        binding.contactWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view1: WebView?, newProgress: Int) {
                binding.frameLayoutImportantContact.visibility = View.VISIBLE
                binding.progressBar.progress = newProgress
                binding.toolbar.toolbarTitle.text = "Loading..."
                if (newProgress == 100) {
                    binding.frameLayoutImportantContact.visibility = View.GONE
                    binding.toolbar.toolbarTitle.text = getString(R.string.terms_condition_policy)
                }
                super.onProgressChanged(view1, newProgress)
            }
        }
        val url = requireArguments().getString(AppHelper.DETAILS_KEY)
        binding.contactWebView.settings.javaScriptEnabled = true
        binding.contactWebView.isVerticalScrollBarEnabled = false
        binding.contactWebView.loadUrl(url!!)
        binding.progressBar.progress = 0
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}