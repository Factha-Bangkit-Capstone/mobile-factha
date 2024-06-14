package com.bangkit.factha.view.adapter

import android.content.Intent
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.factha.R
import com.bangkit.factha.data.response.NewsDataItem
import com.bangkit.factha.databinding.CardSelectedForYouBinding
import com.bumptech.glide.Glide

class HomeAdapter(private val news: List<NewsDataItem?>) : RecyclerView.Adapter<HomeAdapter.NewsViewHolder> () {

    var onItemClick: ((String) -> Unit)? = null

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

/*                itemView.setOnClickListener {
                    if (news != null) {
                        onItemClick?.invoke(news.newsId ?: "")
                    }
                    val intent = Intent(itemView.context, DetailNewsActivity::class.java)
                    if (news != null) {
                        intent.putExtra("id", news.newsId)
                    }
                    itemView.context.startActivity(intent)
                }*/
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = CardSelectedForYouBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun getItemCount(): Int = news.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(news[position])
    }

}