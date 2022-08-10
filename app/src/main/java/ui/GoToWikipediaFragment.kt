package ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.hw14.databinding.FragmentGoToWikipediaBinding



class GoToWikipediaFragment : Fragment() {
    private lateinit var binding : FragmentGoToWikipediaBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentGoToWikipediaBinding.inflate(inflater,container,false)
        return binding.root
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_show_wikipedia, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        val regex = Regex("^[ آابپتثجچحخدذرزژسشصضطظعغفقکگلمنوهیئ]+$")
        val word = requireArguments().getString("word")
        if (regex.matches(word.toString()))
            binding.webView.loadUrl("https://fa.wikipedia.org/wiki/${word}")
        else
            binding.webView.loadUrl("https://en.wikipedia.org/wiki/${word}")

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = WebViewClient()

        binding.webView.canGoBack()
        binding.webView.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == MotionEvent.ACTION_UP && binding.webView.canGoBack()) {
                binding.webView.goBack()
                return@OnKeyListener true
            }
            false
        })
    }
}