package com.mikeschvedov.whatshouldiwatch.ui.search

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mikeschvedov.whatshouldiwatch.R
import com.mikeschvedov.whatshouldiwatch.databinding.FragmentSearchBinding
import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    var currentQuery: String = ""

    lateinit var adapter: SearchAdapter

    lateinit var searchViewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /*---------- View Model ----------*/
        searchViewModel =
            ViewModelProvider(this)[SearchViewModel::class.java]

        /*---------- Binding ----------*/
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*---------- Adapter ----------*/
        adapter = searchViewModel.getAdapter()
        binding.mainRecyclerview.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.mainRecyclerview.adapter = adapter

        /*---------- Collectors ----------*/
        settingCollectors()

        /*---------- Radio Buttons ----------*/
        settingRadioButton()

        return root
    }

    private fun settingRadioButton() {
        // set movie to be initially checked
        val moviesRadioBTN = binding.radiobuttonMovies
        val tvShowRadioBTN = binding.radiobuttonShows
        moviesRadioBTN.setOnClickListener {
            // Changing the color of the text
            moviesRadioBTN.setTextColor(Color.BLACK)
            // Setting the other button to unchecked
            tvShowRadioBTN.isChecked = false
            tvShowRadioBTN.setTextColor(Color.WHITE)
            // send a request again if the user changed filter
            if (currentQuery.isNotEmpty() && currentQuery.length > 1) {
                // clear the adapter (in case we still have results from show)
                adapter.list.clear()
                adapter.notifyDataSetChanged()
                // create new request
                searchViewModel.sendMoviesSearchRequest(currentQuery)
            }
        }

        tvShowRadioBTN.setOnClickListener {
            // Changing the color of the text
            tvShowRadioBTN.setTextColor(Color.BLACK)
            // Setting the other button to unchecked
            moviesRadioBTN.isChecked = false
            moviesRadioBTN.setTextColor(Color.WHITE)
            // send a request again if the user changed filter
            if (currentQuery.isNotEmpty() && currentQuery.length > 1) {
                // clear the adapter (in case we still have results from movies)
                adapter.list.clear()
                adapter.notifyDataSetChanged()
                // create new request
                searchViewModel.sendSeriesSearchRequest(currentQuery)
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            // Runs when the user presses enter inside the search view
            override fun onQueryTextSubmit(query: String): Boolean {
                currentQuery = query
                binding.searchView.clearFocus()
                if (query.isNotEmpty()) {
                    //save the current query to use it when clicking on filter
                    // check the filter selected
                    if (binding.radiobuttonMovies.isChecked && currentQuery.length > 1) {
                        searchViewModel.sendMoviesSearchRequest(query)
                    } else if (binding.radiobuttonShows.isChecked && currentQuery.length > 1) {
                        searchViewModel.sendSeriesSearchRequest(query)
                    }
                }

                if (query.isEmpty()) {
                    adapter.list.clear()
                    adapter.notifyDataSetChanged()
                }
                return false
            }

            // Runs each time there is a change inside the search view
            override fun onQueryTextChange(query: String): Boolean {
                currentQuery = query
                // need to find a solution, so we wont send a request every time
                if (query.isNotEmpty()) {
                    //save the current query to use it when clicking on filter
                    currentQuery = query
                    // check the filter selected

                    if (binding.radiobuttonMovies.isChecked && currentQuery.length > 1) {
                        searchViewModel.sendMoviesSearchRequest(query)
                    } else if (binding.radiobuttonShows.isChecked && currentQuery.length > 1) {
                        searchViewModel.sendSeriesSearchRequest(query)
                    }

                }

                if (query.isEmpty()) {
                    adapter.list.clear()
                    adapter.notifyDataSetChanged()
                }
                return false
            }
        })
    }

    private fun settingCollectors() {
        // List Observer
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.searchFullMediaList.collectLatest { list ->
                    list.itemList?.let { adapter.setNewData(it) }
                }
            }
        }

        // Navigation Observer
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.clicked.collect { item: TmdbItem? ->
                    if (item != null) {
                        startOverViewFragment(item)
                    }
                }
            }
        }
    }

    private fun startOverViewFragment(item: TmdbItem) {
        val bundle: Bundle = bundleOf()
        bundle.putParcelable("media", item)
        findNavController().navigate(R.id.navigation_overview, bundle)
    }

    override fun onResume() {
        super.onResume()

        binding.errorTextview.text = ""

        //Return movies to be selected
        //binding.radiobuttonMovies.isChecked = true
       // binding.radiobuttonShows.isChecked = false

        if (currentQuery.isNotEmpty()) {
            //save the current query to use it when clicking on filter
            // check the filter selected
            if (binding.radiobuttonMovies.isChecked) {
                searchViewModel.sendMoviesSearchRequest(currentQuery)
            } else if (binding.radiobuttonShows.isChecked) {
                searchViewModel.sendSeriesSearchRequest(currentQuery)
            }
        }

        if (binding.radiobuttonMovies.isChecked) {
            binding.radiobuttonMovies.setTextColor(Color.BLACK)
            binding.radiobuttonShows.setTextColor(Color.WHITE)
        }
        if (binding.radiobuttonShows.isChecked) {
            binding.radiobuttonShows.setTextColor(Color.BLACK)
            binding.radiobuttonMovies.setTextColor(Color.WHITE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}