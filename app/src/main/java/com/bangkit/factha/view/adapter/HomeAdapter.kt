package com.bangkit.factha.view.adapter

import android.content.Intent
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.factha.R
import com.bangkit.factha.data.remote.MainRepository
import com.bangkit.factha.data.response.NewsDataItem
import com.bangkit.factha.databinding.CardSelectedForYouBinding
import com.bangkit.factha.view.activity.article.DetailArticleActivity
import com.bumptech.glide.Glide
import com.bangkit.factha.data.helper.Result
import com.bangkit.factha.view.ViewModelFactory
import com.bangkit.factha.view.activity.MainViewModel
import com.bangkit.factha.view.fragment.main.BookmarkViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeAdapter(
    private var news: List<NewsDataItem?>,
    private val userId: String,
    private val repository: MainRepository,
    private val bookmarkViewModel: BookmarkViewModel,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<HomeAdapter.NewsViewHolder>() {

    var onItemClick: ((String) -> Unit)? = null
    private var bookmarkedNewsIds: List<String> = emptyList()

    inner class NewsViewHolder(private val binding: CardSelectedForYouBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(news: NewsDataItem?) {
            val imageBytes = Base64.decode(news?.imageB64, Base64.DEFAULT)
            with(binding) {
                Glide.with(itemView.context)
                    .load(imageBytes)
                    .into(ivSelectForYou)
                tvTitleSelectForYou.text = news?.title
                tvDescriptionSelectForYou.text = news?.tags

                val hoaxScore = news?.hoax?.toString()?.toDoubleOrNull() ?: 0.0
                if (hoaxScore == 1.0) {
                    tvValidScore.text = "H"
                    tvValidScore.setBackgroundResource(R.drawable.circle_background_red)
                } else {
                    tvValidScore.text = "F"
                    tvValidScore.setBackgroundResource(R.drawable.circle_background)
                }

                fun updateIconFirst(isBookmarked: Boolean) {
                    if (isBookmarked) {
                        binding.btnSaveArticle.setImageResource(R.drawable.baseline_bookmark_24)
                    } else {
                        binding.btnSaveArticle.setImageResource(R.drawable.baseline_bookmark_border_24)
                    }
                }

                bookmarkViewModel.savedNewsList.observe(lifecycleOwner) { savedNews ->
                    val isBookmarked = savedNews.any { it == news?.newsId }
                    updateIconFirst(isBookmarked)
                }

                itemView.setOnClickListener {
                    news?.newsId?.let { newsId ->
                        onItemClick?.invoke(newsId)
                        val intent = Intent(itemView.context, DetailArticleActivity::class.java)
                        intent.putExtra("newsId", newsId)
                        itemView.context.startActivity(intent)
                    }
                }

                fun updateBookmarkIcon(isBookmarked: Boolean) {
                    if (isBookmarked) {
                        binding.btnSaveArticle.setImageResource(R.drawable.baseline_bookmark_24)
                    } else {
                        binding.btnSaveArticle.setImageResource(R.drawable.baseline_bookmark_border_24)
                    }
                }

                binding.btnSaveArticle.setOnClickListener {
                    news?.newsId.let { savedNewsId ->
                        CoroutineScope(Dispatchers.Main).launch {
                            val toggleResult = savedNewsId?.let { it1 ->
                                repository.toggleBookmark(
                                    it1
                                )
                            }

                            when (toggleResult) {
                                is Result.Success -> {
                                    binding.loadingMenuSaved.visibility = View.GONE
                                    val isBookmarked = toggleResult.data
                                    updateBookmarkIcon(isBookmarked)
                                }
                                is Result.Error -> {
                                    updateBookmarkIcon(true)
                                    Log.e("MainRepository", "Failed to toggle bookmark: ${toggleResult.error}")
                                }
                                Result.Loading -> {
                                    binding.loadingMenuSaved.visibility = View.VISIBLE
                                }
                                null -> {
                                    Log.e("MainRepository", "Unexpected null result from toggleBookmark")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = CardSelectedForYouBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun getItemCount(): Int{
        return minOf(news.size)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(news[position])
    }

    fun updateData(newNews: List<NewsDataItem?>) {
        news = newNews
        notifyDataSetChanged()
    }

    fun updateBookmarkedNews(bookmarkedNewsIds: List<String>) {
        this.bookmarkedNewsIds = bookmarkedNewsIds
        notifyDataSetChanged()
    }
}
