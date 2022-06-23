package com.mikeschvedov.whatshouldiwatch.ui.home.inner.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mikeschvedov.whatshouldiwatch.R
import com.mikeschvedov.whatshouldiwatch.databinding.FragmentDisplayMoviesBinding
import com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters.ParentAdapter
import com.mikeschvedov.whatshouldiwatch.models.adapters.CategoryModel
import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem
import com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters.OnClickPositionListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayMoviesFragment : Fragment() {

    private var _binding: FragmentDisplayMoviesBinding? = null
    private val binding get() = _binding!!

    var categoryList: MutableList<CategoryModel> = mutableListOf()

    lateinit var displayMoviesViewModel: DisplayMoviesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /*---------- View Model ----------*/
        displayMoviesViewModel =
            ViewModelProvider(this).get(DisplayMoviesViewModel::class.java)
        /*---------- Binding ----------*/
        _binding = FragmentDisplayMoviesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        /*---------- Starting Api Call ----------*/
        displayMoviesViewModel.sendApiRequests()
        /*---------- Adapter ----------*/
        val adapter = displayMoviesViewModel.getAdapter()
        binding.mainRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.mainRecyclerview.adapter = adapter
        /*---------- Observers ----------*/
        // Popular movie list
        displayMoviesViewModel.popularMovieList.observe(viewLifecycleOwner) { category ->
            updateCategory(category, adapter)
        }
        // Top Rated movie list
        displayMoviesViewModel.topRatedMovieList.observe(viewLifecycleOwner) { category ->
            updateCategory(category, adapter)
        }
        // New Releases movie list
        displayMoviesViewModel.newReleasesMovieList.observe(viewLifecycleOwner) { category ->
            updateCategory(category, adapter)
        }
        // Now Playing movie list
        displayMoviesViewModel.nowPlayingMovieList.observe(viewLifecycleOwner) { category ->
            updateCategory(category, adapter)
        }
        // Error Observer
        displayMoviesViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            binding.errorTextview.text = error
            binding.errorTextview.visibility = View.VISIBLE
            binding.progressbar.visibility = View.GONE
            // hide the swipe to refresh bar also if we get a message
            binding.swipeContainer.isRefreshing = false
        }
        // Success Observer
        displayMoviesViewModel.successFlag.observe(viewLifecycleOwner) { flag ->
            if (flag) {
                binding.errorTextview.visibility = View.GONE
                // dealing with the main progress bar
                binding.progressbar.visibility = View.GONE
                // dealing with the swipe to refresh progress bar
                binding.swipeContainer.isRefreshing = false
            }
        }

        // Navigation Observer
        displayMoviesViewModel.navigateToDetails.observe(viewLifecycleOwner, Observer { event ->
            // we get the content with the wrapped in the event
            event.getContentIfNotHandled()
                ?.let { itemWithoutEvent ->// Only proceed if the event has never been handled
                    startOverViewFragment(itemWithoutEvent)
                }
        })

        setSwipeRefresh()

        return root
    }

    private fun setSwipeRefresh() {
        binding.swipeContainer.setOnRefreshListener {
            binding.swipeContainer.isRefreshing = false
            binding.progressbar.visibility = View.VISIBLE
            binding.errorTextview.visibility = View.GONE
            displayMoviesViewModel.sendApiRequests()
        }
    }


    private fun startOverViewFragment(item: TmdbItem) {
        val bundle: Bundle = bundleOf()
        bundle.putParcelable("media", item)
        findNavController().navigate(R.id.navigation_overview, bundle)
    }

    private fun updateCategory(
        category: CategoryModel,
        adapter: ParentAdapter
    ) {
        if (!checkIfContains(category)) {
            categoryList.add(category)
        }
        updateAdapter(adapter)
    }

    private fun checkIfContains(category: CategoryModel): Boolean {
        categoryList.forEach {
            if (it.title == category.title) {
                return true
            }
        }
        return false
    }

    private fun updateAdapter(adapter: ParentAdapter) {
        if (categoryList.size == 4) {
            adapter.submitList(categoryList)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        categoryList.clear()
        /*---------- Resending Api Call ----------*/
        binding.progressbar.visibility = View.VISIBLE
        binding.errorTextview.text = ""
        displayMoviesViewModel.sendApiRequests()
    }
}