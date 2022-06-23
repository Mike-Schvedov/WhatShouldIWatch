package com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikeschvedov.whatshouldiwatch.R
import com.mikeschvedov.whatshouldiwatch.models.adapters.CategoryModel
import com.mikeschvedov.whatshouldiwatch.utils.Constants

class ParentAdapter(
    private var categoryList: List<CategoryModel> = listOf<CategoryModel>(),
    private val listener: OnClickItemListener
) :
    RecyclerView.Adapter<ParentAdapter.ParentViewHolder>() {

    val isLoading = false
    val isLastPage = false
    var isScrolling = false

    class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var childRecyclerView: RecyclerView =
            itemView.findViewById<RecyclerView>(R.id.child_recyclerview)
        var titleTextView: TextView = itemView.findViewById<TextView>(R.id.textview_parent_title)
    }

    fun submitList(list: List<CategoryModel>) {
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
            listener.onItemClicked(childItem)
        }
        // setting the child adapter
        holder.childRecyclerView.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.childRecyclerView.adapter = childAdapter
        childAdapter.notifyDataSetChanged()

        // dealing with pagination
        holder.childRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount

                val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
                val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
                val isNotAtBeginning = firstVisibleItemPosition >= 0
                val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
                val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                        isTotalMoreThanVisible && isScrolling
                if (shouldPaginate) {
                    //make api request with page number ++
                    println("------------------------------------------------------------")
                    println("LAST REACHED  ${recyclerView.adapter}, name: ${categoryList[holder.adapterPosition].title}")
                    println("------------------------------------------------------------")
                    isScrolling = false
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
        })

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}