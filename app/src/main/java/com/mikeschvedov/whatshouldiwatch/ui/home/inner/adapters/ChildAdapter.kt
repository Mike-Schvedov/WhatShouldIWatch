package com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikeschvedov.whatshouldiwatch.R
import com.mikeschvedov.whatshouldiwatch.utils.Constants
import com.mikeschvedov.whatshouldiwatch.models.adapters.ChildModel
import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem
import com.squareup.picasso.Picasso


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

        holder.childImageview.setOnClickListener {
            listener.onItemClicked(childList[position])
        }

    }

    override fun getItemCount(): Int {
        return childList.size
    }
}