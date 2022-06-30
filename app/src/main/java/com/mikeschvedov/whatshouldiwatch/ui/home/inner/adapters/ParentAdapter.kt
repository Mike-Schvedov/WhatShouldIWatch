package com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikeschvedov.whatshouldiwatch.R
import com.mikeschvedov.whatshouldiwatch.models.adapters.MediaGroup
import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class ParentAdapter(
    private var categoryList: List<MediaGroup> = listOf<MediaGroup>(),
) :
    RecyclerView.Adapter<ParentAdapter.ParentViewHolder>() {

    // Item wrapper so we can give the stateflow a default null value
    class ItemWrapper(val item: TmdbItem? = null)

    // Item flow that will be used as a callback
    var itemHotFlow: MutableStateFlow<ItemWrapper> = MutableStateFlow(ItemWrapper())


    class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var childRecyclerView: RecyclerView =
            itemView.findViewById<RecyclerView>(R.id.child_recyclerview)
        var titleTextView: TextView = itemView.findViewById<TextView>(R.id.textview_parent_title)
    }

    fun submitList(list: List<MediaGroup>) {
        categoryList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        return ParentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.parent_rv_view_holder,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        holder.titleTextView.text = categoryList[position].title
        // dealing with the child adapter callback
        val childAdapter = ChildAdapter(categoryList[position].childModelList) { childItem ->
            // a flow used as callback
            itemHotFlow.value = ItemWrapper(childItem)
        }
        // setting the child adapter
        holder.childRecyclerView.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.childRecyclerView.adapter = childAdapter
        childAdapter.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }


}