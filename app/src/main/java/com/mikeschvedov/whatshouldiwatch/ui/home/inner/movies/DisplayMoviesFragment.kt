package com.mikeschvedov.whatshouldiwatch.ui.home.inner.movies

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikeschvedov.whatshouldiwatch.R
import com.mikeschvedov.whatshouldiwatch.data.remote.networking.NetworkStatusChecker
import com.mikeschvedov.whatshouldiwatch.databinding.FragmentDisplayMoviesBinding
import com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters.ParentAdapter
import com.mikeschvedov.whatshouldiwatch.models.adapters.CategoryModel
import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayMoviesFragment : Fragment() {

    private var _binding: FragmentDisplayMoviesBinding? = null
    private val binding get() = _binding!!

    var categoryList: MutableList<CategoryModel> = mutableListOf()

    lateinit var displayMoviesViewModel: DisplayMoviesViewModel
    lateinit var adapter: ParentAdapter

    //TODO inject
    lateinit var networkStatusChecker: NetworkStatusChecker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //TODO inject
        networkStatusChecker = NetworkStatusChecker(context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?)

        /* View Model */
        displayMoviesViewModel =
            ViewModelProvider(this).get(DisplayMoviesViewModel::class.java)

        /* Binding */
        _binding = FragmentDisplayMoviesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* Adapter */
        adapter = displayMoviesViewModel.getAdapter()
        binding.mainRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.mainRecyclerview.adapter = adapter

        /* Starting Api Call */
        displayMoviesViewModel.getDataFromDatabase()

        /* Setting LiveData Observers */
        setLiveDataObservers()

        setSwipeRefresh()

        return root
    }

    private fun setLiveDataObservers() {
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
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            // hide the swipe to refresh bar also if we get a message
            binding.swipeContainer.isRefreshing = false
        }
        // Success Observer
        displayMoviesViewModel.successFlag.observe(viewLifecycleOwner) { flag ->
            if (flag) {
                // dealing with the swipe to refresh progress bar
                binding.swipeContainer.isRefreshing = false
            }
        }

        // Navigation Observer
        displayMoviesViewModel.navigateToDetails.observe(viewLifecycleOwner) { event ->
            // we get the content with the wrapped in the event
            event.getContentIfNotHandled()
                ?.let { itemWithoutEvent ->// Only proceed if the event has never been handled
                    startOverViewFragment(itemWithoutEvent)
                }
        }
    }

    private fun setSwipeRefresh() {
        binding.swipeContainer.setOnRefreshListener {
            // If there is internet do this
            networkStatusChecker.performIfConnectedToInternet {
                displayMoviesViewModel.getDataFromDatabase()
            }
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
            adapter.submitList(categoryList)
            adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        categoryList.clear()
        /*---------- Resending Api Call ----------*/
        displayMoviesViewModel.getDataFromDatabase()
    }
}