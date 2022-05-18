package com.yulius.warasapp.adapter.paging

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yulius.warasapp.R
import com.yulius.warasapp.adapter.OnItemClickCallback
import com.yulius.warasapp.data.model.Article
import com.yulius.warasapp.data.model.Source

class ListArticleAdapter: PagingDataAdapter<Article, ListArticleViewHolder>(DIFF_CALLBACK){
    private var listArticle = ArrayList<Article>()

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setData(data: List<Article>?) {
        if (data == null) return
        this.listArticle.clear()
        this.listArticle.addAll(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListArticleViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_article, parent, false)
        return ListArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListArticleViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }

//        val (
//            id,
//            author,
//            title,
//            description,
//            url,
//            urlToImage,
//            publishedAt,
//            source,
//            content,
//        ) = listArticle[position]
//
//        Glide.with(holder.itemView.context)
//            .load(urlToImage)
//            .into(holder.newsImage)
//        holder.newsTitle.text = title
//        holder.newsDate.text = publishedAt
//        holder.newsSource.text = source.toString()
//        holder.newsText.text = description
//
//        holder.itemView.setOnClickListener {
//            onItemClickCallback.onItemClicked(
//                listArticle[holder.adapterPosition]
//            )
//        }
    }

    override fun getItemCount(): Int = listArticle.size

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}