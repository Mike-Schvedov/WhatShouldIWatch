package com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.mikeschvedov.whatshouldiwatch.R
import com.mikeschvedov.whatshouldiwatch.utils.Constants
import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem
import com.mikeschvedov.whatshouldiwatch.utils.toFlow
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class ChildAdapter(val childList: List<TmdbItem>, private val listener: OnClickItemListener) :
    RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {


    class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var childImageview: ImageView = itemView.findViewById<ImageView>(R.id.imageview_child_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        return ChildViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.child_rv_view_holder,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ChildAdapter.ChildViewHolder, position: Int) {
        // We get the image path ending (image.jpg)
        val imagePathEnding = childList[position].posterPath
        Picasso.get().load(Uri.parse(Constants.IMAGE_LOCATION + imagePathEnding)).into(holder.childImageview)
        // converting the item to a flow

        // passing the flow into the callback
        holder.childImageview.setOnClickListener {
            listener.onItemClicked(childList[position])
        }

    }

    override fun getItemCount(): Int {
        return childList.size
    }

   /* private fun toFlow(tmdbItem: TmdbItem) = flow{
        emit(tmdbItem)
    }*/
}