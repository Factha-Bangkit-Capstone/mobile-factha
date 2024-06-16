package com.bangkit.factha.view.adapter

import android.content.Intent
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.factha.R
import com.bangkit.factha.data.remote.MainRepository
import com.bangkit.factha.data.response.NewsDataItem
import com.bangkit.factha.databinding.CardSelectedForYouBinding
import com.bangkit.factha.view.activity.article.DetailArticleActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.bangkit.factha.data.helper.Result

class ArticleAdapter(
    private val news: List<NewsDataItem?>,
    private val userId: String) : RecyclerView.Adapter<ArticleAdapter.NewsViewHolder>() {

    var onItemClick: ((String) -> Unit)? = null

    private lateinit var repository: MainRepository


    inner class NewsViewHolder(private val binding: CardSelectedForYouBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(news: NewsDataItem?) {
            val imageBytes = Base64.decode(news?.imageB64, Base64.DEFAULT)
            with(binding) {
                Glide.with(itemView.context)
                    .load(imageBytes)
                    .into(ivSelectForYou)
                tvTitleSelectForYou.text = news?.title
                val hoaxScore = news?.hoax?.toString()?.toDoubleOrNull() ?: 0.0
                if (hoaxScore == 1.0) {
                    tvValidScore.text = "H"
                    tvValidScore.setBackgroundResource(R.drawable.circle_background_red)
                } else {
                    tvValidScore.text = "F"
                    tvValidScore.setBackgroundResource(R.drawable.circle_background)
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
                        btnSaveArticle.setImageResource(R.drawable.baseline_bookmark_24)
                    } else {
                        btnSaveArticle.setImageResource(R.drawable.baseline_bookmark_border_24)
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
                                    val isBookmarked = toggleResult.data
                                    updateBookmarkIcon(isBookmarked)
                                }
                                is Result.Error -> {
                                    updateBookmarkIcon(true)
                                    Log.e("MainRepository", "Failed to toggle bookmark: ${toggleResult.error}")
                                }
                                Result.Loading -> {
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

    override fun onBindViewHolder(holder: ArticleAdapter.NewsViewHolder, position: Int) {
        holder.bind(news[position])
    }

    override fun getItemCount() = news.size

    }
