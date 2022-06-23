package com.mikeschvedov.whatshouldiwatch.ui.home.inner.tvshow

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
import com.mikeschvedov.whatshouldiwatch.R
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /*---------- View Model ----------*/
        displayTvShowsViewModel =
            ViewModelProvider(this).get(DisplayTvShowsViewModel::class.java)
        /*---------- Binding ----------*/
        _binding = FragmentDisplayTvShowBinding.inflate(inflater, container, false)
        val root: View = binding.root
        /*---------- Send Api Request ----------*/
        displayTvShowsViewModel.sendApiRequests()
        /*---------- Adapter ----------*/
        val adapter = displayTvShowsViewModel.getAdapter()
        binding.mainRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.mainRecyclerview.adapter = adapter
        /*---------- Observers ----------*/
        // Popular shows list
        displayTvShowsViewModel.popularShowsList.observe(viewLifecycleOwner) { category ->
            if (!checkIfContains(category)) {
                categoryListTv.add(category)
            }
            updateAdapter(adapter)
        }
        // Top Rated shows list
        displayTvShowsViewModel.topRatedShowsList.observe(viewLifecycleOwner) { category ->
            if (!checkIfContains(category)) {
                categoryListTv.add(category)
            }
            updateAdapter(adapter)
        }
        // New Released shows list
        displayTvShowsViewModel.newReleasesShowsList.observe(viewLifecycleOwner) { category ->
            if (!checkIfContains(category)) {
                categoryListTv.add(category)
            }
            updateAdapter(adapter)
        }
        // additional test list
        displayTvShowsViewModel.popularShowsList.observe(viewLifecycleOwner) { category ->
            if (!checkIfContains(category)) {
                categoryListTv.add(category)
            }
            updateAdapter(adapter)

        }

        // Error Observer
        displayTvShowsViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            binding.errorTextview.text = error
            binding.progressbar.visibility = View.GONE
        }
        // Success Observer
        displayTvShowsViewModel.successFlag.observe(viewLifecycleOwner) { flag ->
            if (flag){
                binding.progressbar.visibility = View.GONE
            }
        }

        // Navigation Observer
        displayTvShowsViewModel.navigateToDetails.observe(viewLifecycleOwner, Observer { event ->
            // we get the content with the wrapped in the event
            event.getContentIfNotHandled()
                ?.let { itemWithoutEvent ->// Only proceed if the event has never been handled
                    startOverViewFragment(itemWithoutEvent)
                }
        })

        return root
    }

    private fun startOverViewFragment(item: TmdbItem) {
        val bundle: Bundle = bundleOf()
        bundle.putParcelable("media", item)
        findNavController().navigate(R.id.navigation_overview, bundle)
    }

    private fun checkIfContains(category: CategoryModel): Boolean {
        categoryListTv.forEach {
            if (it.title == category.title){
                return true
            }
        }
        return false
    }

    private fun updateAdapter(adapter: ParentAdapter) {
        if (categoryListTv.size == 3){
            adapter.submitList(categoryListTv)
            adapter.notifyDataSetChanged()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        categoryListTv.clear()
        /*---------- Resending Api Call ----------*/
        binding.progressbar.visibility = View.VISIBLE
        binding.errorTextview.text = ""
        displayTvShowsViewModel.sendApiRequests()

    }
}