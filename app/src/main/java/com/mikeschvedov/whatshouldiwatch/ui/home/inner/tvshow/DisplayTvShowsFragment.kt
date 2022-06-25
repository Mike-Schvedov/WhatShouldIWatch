package com.mikeschvedov.whatshouldiwatch.ui.home.inner.tvshow

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikeschvedov.whatshouldiwatch.R
import com.mikeschvedov.whatshouldiwatch.data.remote.networking.NetworkStatusChecker
import com.mikeschvedov.whatshouldiwatch.models.adapters.CategoryModel
import com.mikeschvedov.whatshouldiwatch.databinding.FragmentDisplayTvShowBinding
import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem
import com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters.ParentAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayTvShowsFragment : Fragment() {

    private var _binding: FragmentDisplayTvShowBinding? = null
    private val binding get() = _binding!!

    var categoryListTv: MutableList<CategoryModel> = mutableListOf()

    lateinit var displayTvShowsViewModel: DisplayTvShowsViewModel
    lateinit var adapter: ParentAdapter

    //TODO inject
    lateinit var networkStatusChecker: NetworkStatusChecker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //TODO inject
        networkStatusChecker =
            NetworkStatusChecker(context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?)

        /* View Model */
        displayTvShowsViewModel =
            ViewModelProvider(this).get(DisplayTvShowsViewModel::class.java)

        /* Binding */
        _binding = FragmentDisplayTvShowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* Adapter */
        adapter = displayTvShowsViewModel.getAdapter()
        binding.mainRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.mainRecyclerview.adapter = adapter

        /* Starting Api Call */
        displayTvShowsViewModel.getDataFromDatabase()

        /* Setting LiveData Observers */
        setLiveDataObservers()

        setSwipeRefresh()

        return root
    }

    private fun setLiveDataObservers() {
        // Popular movie list
        displayTvShowsViewModel.popularShowsList.observe(viewLifecycleOwner) { category ->
            updateCategory(category, adapter)
        }
        // Top Rated movie list
        displayTvShowsViewModel.topRatedShowsList.observe(viewLifecycleOwner) { category ->
            updateCategory(category, adapter)
        }
        // New Releases movie list
        displayTvShowsViewModel.newReleasesShowsList.observe(viewLifecycleOwner) { category ->
            updateCategory(category, adapter)
        }

        // Error Observer
        displayTvShowsViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            // hide the swipe to refresh bar also if we get a message
            binding.swipeContainer.isRefreshing = false
        }
        // Success Observer
        displayTvShowsViewModel.successFlag.observe(viewLifecycleOwner) { flag ->
            if (flag) {
                // dealing with the swipe to refresh progress bar
                binding.swipeContainer.isRefreshing = false
            }
        }

        // Navigation Observer
        displayTvShowsViewModel.navigateToDetails.observe(viewLifecycleOwner) { event ->
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
                displayTvShowsViewModel.getDataFromDatabase()
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
            categoryListTv.add(category)
        }
        updateAdapter(adapter)
    }

    private fun checkIfContains(category: CategoryModel): Boolean {
        categoryListTv.forEach {
            if (it.title == category.title) {
                return true
            }
        }
        return false
    }

    private fun updateAdapter(adapter: ParentAdapter) {
        adapter.submitList(categoryListTv)
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        categoryListTv.clear()
        /*---------- Resending Api Call ----------*/
        displayTvShowsViewModel.getDataFromDatabase()

    }
}