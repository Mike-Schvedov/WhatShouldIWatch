package com.mikeschvedov.whatshouldiwatch.ui.home.inner.tvshow

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikeschvedov.whatshouldiwatch.R
import com.mikeschvedov.whatshouldiwatch.data.remote.networking.NetworkStatusChecker
import com.mikeschvedov.whatshouldiwatch.databinding.FragmentDisplayTvShowBinding
import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem
import com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters.ParentAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DisplayTvShowsFragment : Fragment() {

    private var _binding: FragmentDisplayTvShowBinding? = null
    private val binding get() = _binding!!

    lateinit var displayTvShowsViewModel: DisplayTvShowsViewModel
    lateinit var adapter: ParentAdapter

    @Inject
    lateinit var networkStatusChecker: NetworkStatusChecker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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

        /* Get data from database to be displayed */
        decideFirstTimeFetching()

        /* Setting LiveData Observers */
        setLiveDataObservers()

        setSwipeRefresh()

        return root
    }

    private fun decideFirstTimeFetching() {
        // If its the first run of the app populate the database,
        // Also save in shared pref if its the first time or not
        // After first time, it will not populate the DB, only fetch data from it.
        val sharedPref = getSharedPref()
        val isFirstRun = sharedPref?.getBoolean("isFirstRunMovieTV", true) // the default is true
        if (isFirstRun == true) {
            // If its the first time running the app, populate the DB and get data
            displayTvShowsViewModel.updateDBAndFetchTvShows()
            // Set shared pref that it is no longer first time
            with(sharedPref.edit()) {
                putBoolean("isFirstRunMovieTV", false)
                apply()
            }
        } else {
            // If it is not the first time running the app, only fetch the data to be displayed
            displayTvShowsViewModel.fetchTvShowsPageMedia()
        }
    }

    private fun getSharedPref(): SharedPreferences? {
        return activity?.getPreferences(Context.MODE_PRIVATE)
    }

    private fun setLiveDataObservers() {
        // All media list
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                displayTvShowsViewModel.showsFullMediaList.collectLatest { mediaGroupList ->
                    adapter.submitList(mediaGroupList.list)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        // Navigation Observer
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                displayTvShowsViewModel.clicked.collect { item: TmdbItem? ->
                    if (item != null) {
                        startOverViewFragment(item)
                    }
                }
            }
        }
    }

    private fun setSwipeRefresh() {
        binding.swipeContainer.setOnRefreshListener {
            // If there is internet do this
            networkStatusChecker.performIfConnectedToInternet {
               // displayTvShowsViewModel.getDataFromDatabase()
            }
            binding.swipeContainer.isRefreshing = false
        }
    }

    private fun startOverViewFragment(item: TmdbItem) {
        val bundle: Bundle = bundleOf()
        bundle.putParcelable("media", item)
        findNavController().navigate(R.id.navigation_overview, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}