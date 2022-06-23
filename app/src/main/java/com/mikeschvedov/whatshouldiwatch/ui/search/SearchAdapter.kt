package com.mikeschvedov.whatshouldiwatch.ui.search

import android.net.Uri

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mikeschvedov.whatshouldiwatch.R
import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem
import com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters.OnClickPositionListener
import com.mikeschvedov.whatshouldiwatch.utils.Constants
import com.squareup.picasso.Picasso
import javax.inject.Inject

class SearchAdapter @Inject constructor(private val listener: OnClickPositionListener) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    var list: MutableList<TmdbItem> = mutableListOf()



    // add new data
    fun setNewData(newData: List<TmdbItem>) {
        // passing the new and old list into the callback
        val diffCallback = DiffUtilCallback(list, newData)
        // we get the result
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        // we clear the old list
        list.clear()
        // and replace it with the new list
        list.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageview: ImageView =
            itemView.findViewById(R.id.imageview_child_item)
        var errorText: TextView =
            itemView.findViewById(R.id.error_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.search_rv_view_holder,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {

        val item = list[position]
        val imagePathEnding = item.posterPath
        //if we don't get an image
        if (imagePathEnding != null) {
            Picasso.get().load(Uri.parse(Constants.IMAGE_LOCATION + imagePathEnding))
                .placeholder(R.drawable.loading)
                .into(holder.imageview)
        } else {
            holder.errorText.text = item.name
            holder.imageview.visibility = View.GONE
        }

        holder.imageview.setOnClickListener {
            listener.onItemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}


class DiffUtilCallback(private val oldList: List<TmdbItem>, private val newList: List<TmdbItem>) :
    DiffUtil.Callback() {

    // old size
    override fun getOldListSize(): Int = oldList.size

    // new list size
    override fun getNewListSize(): Int = newList.size

    // if items are same
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.javaClass == newItem.javaClass
    }

    // check if contents are same
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.hashCode() == newItem.hashCode() ||
                oldItem.id == newItem.id ||
                oldItem.posterPath == newItem.posterPath ||
                oldItem.releaseDate == newItem.releaseDate ||
                oldItem.backdropPath == newItem.backdropPath ||
                oldItem.name == newItem.name ||
                oldItem.overview == newItem.overview ||
                oldItem.voteAverage == newItem.voteAverage
    }
}