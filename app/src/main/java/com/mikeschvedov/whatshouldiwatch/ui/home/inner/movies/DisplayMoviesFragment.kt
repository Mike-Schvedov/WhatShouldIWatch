package com.mikeschvedov.whatshouldiwatch.ui.home.inner.movies

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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.mikeschvedov.whatshouldiwatch.R
import com.mikeschvedov.whatshouldiwatch.data.remote.networking.NetworkStatusChecker
import com.mikeschvedov.whatshouldiwatch.databinding.FragmentDisplayMoviesBinding
import com.mikeschvedov.whatshouldiwatch.ui.home.inner.adapters.ParentAdapter
import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem
import com.mikeschvedov.whatshouldiwatch.utils.Logger
import com.mikeschvedov.whatshouldiwatch.utils.paging.PagingAdapter
import com.mikeschvedov.whatshouldiwatch.utils.workers.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class DisplayMoviesFragment : Fragment() {

    private var _binding: FragmentDisplayMoviesBinding? = null
    private val binding get() = _binding!!

    lateinit var displayMoviesViewModel: DisplayMoviesViewModel
    lateinit var adapter: ParentAdapter
    private lateinit var pagingAdapter: PagingAdapter


    @Inject
    lateinit var networkStatusChecker: NetworkStatusChecker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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
        //Paging adapter
        pagingAdapter = PagingAdapter()
        binding.apply {
            pagingRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            pagingRecyclerview.setHasFixedSize(true)
            pagingRecyclerview.adapter = pagingAdapter
        }

        /* Get data from database to be displayed */
        decideFirstTimeFetching()

        /* Setting LiveData Observers */
        setLiveDataObservers()

        /* Set Periodic Update Using WorkManager */
        launchPeriodicDatabaseUpdate()

        setSwipeRefresh()

        return root
    }

    private fun decideFirstTimeFetching() {
        // If its the first run of the app populate the database,
        // Also save in shared pref if its the first time or not
        // After first time, it will not populate the DB, only fetch data from it.
        val sharedPref = getSharedPref()
        val isFirstRun = sharedPref?.getBoolean("isFirstRunMovie", true) // the default is true
        if (isFirstRun == true) {
            // If its the first time running the app, populate the DB and get data
            displayMoviesViewModel.updateDBAndFetchMovies()
            // Set shared pref that it is no longer first time
            with(sharedPref.edit()) {
                putBoolean("isFirstRunMovie", false)
                apply()
            }
        } else {
            // If it is not the first time running the app, only fetch the data to be displayed
            displayMoviesViewModel.fetchMoviesPageMedia()
        }
    }

    private fun getSharedPref(): SharedPreferences? {
        return activity?.getPreferences(Context.MODE_PRIVATE)
    }

    private fun setLiveDataObservers() {
        // All Media List
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                displayMoviesViewModel.moviesFullMediaList.collectLatest { mediaGroupList ->
                    adapter.submitList(mediaGroupList.list)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        // Navigation Observer
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                displayMoviesViewModel.clicked.collect{ item: TmdbItem? ->
                    if (item != null) {
                        startOverViewFragment(item)
                    }
                }
            }
        }

        // Paging Data Example
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                displayMoviesViewModel.getTopRatedByPaging().collectLatest { value: PagingData<TmdbItem> ->
                    pagingAdapter.submitData(value)
                }
            }
        }
    }

    private fun setSwipeRefresh() {
        binding.swipeContainer.setOnRefreshListener {
            // If there is internet do this
            networkStatusChecker.performIfConnectedToInternet {
                //  displayMoviesViewModel.getDataFromDatabase()
            }
            binding.swipeContainer.isRefreshing = false
        }
    }

    private fun startOverViewFragment(item: TmdbItem) {
        val bundle: Bundle = bundleOf()
        bundle.putParcelable("media", item)
        findNavController().navigate(R.id.navigation_overview, bundle)
    }

    /* EXAMPLE NOT USED IN PROJECT */
    private fun setOneTimeWorkRequest() {

        val workManager:WorkManager = WorkManager.getInstance(requireContext())

        val data: Data = Data.Builder()
            .putInt("KEY_COUNT_VALUE", 123)
            .build()

        // creating some constraints.
        // (when should our work start executing)
        val constraint = Constraints.Builder()
            // this is a battery charging constraint
            // even if we trigger the work, it will not start until
            // our phone will start charging.
            // until then, the work will be in "enqueued" state
            .setRequiresCharging(true)
            // this is a network  constraint
            // the work will start only if we have internet connection
            // if there is not internet it will be in the "enqueued" state.
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setConstraints(constraint) // adding the optional constraint to the work
            .setInputData(data) // adding the optional input data
            .build()

        // CREATING MORE WORKERS INSTANCES FOR CHAINNING
        val filteringRequest = OneTimeWorkRequest.Builder(FilteringWorker::class.java)
            .build()
        val compressingRequest = OneTimeWorkRequest.Builder(CompressingWorker::class.java)
            .build()
            // val downloadingRequest = OneTimeWorkRequest.Builder(DownloadingWorker::class.java)
         //   .build()

        //PARALLEL WORKS EXAMPLE
        val parallWorks = mutableListOf<OneTimeWorkRequest>()
     //   parallWorks.add(downloadingRequest)
        parallWorks.add(filteringRequest)
        // then just add the list into a chaining (beginwith/then)
        // or without chaining just enqueue it


        // CHAINING EXAMPLE
        /*workManager
            .beginWith(filteringRequest)
            .then(compressingRequest)
            .then(uploadRequest)
            .enqueue()
        */

        workManager.enqueue(uploadRequest)
        workManager.getWorkInfoByIdLiveData(uploadRequest.id)
            .observe(requireActivity(), Observer {
                // Will display in real time the statue
                // blocked-> enqueued -> running -> success
                println(it.state.declaringClass)

                //Observer the output data we send (optional)
                if (it.state.isFinished){
                    val outputtedData = it.outputData
                    val message = outputtedData.getString(UploadWorker.KEY_WORKER)
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            })
    }
/*    private fun setPeriodicWorkRequest(){
        // Will run every 16 minutes interval. (minimum is 15min) can change the TimeUnit to hours/days
        val periodicWorkRequest = PeriodicWorkRequest.Builder(DownloadingWorker::class.java, 16, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(requireContext()).enqueue(periodicWorkRequest)
    }*/
    private fun launchPeriodicDatabaseUpdate(){

        val workManager: WorkManager = WorkManager.getInstance(requireContext())
        // creating some constraints.
        // (when should our work start executing)
        val constraint = Constraints.Builder()
            // this is a network  constraint
            // the work will start only if we have internet connection
            // if there is not internet it will be in the "enqueued" state until there is internet
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        // Will run every 24 hours interval. (minimum is 15min) can change the TimeUnit to hours/days
        val periodicWorkRequest = PeriodicWorkRequest.Builder(UpdateDatabaseWorker::class.java, 16, TimeUnit.MINUTES)
            .setConstraints(constraint) // adding the optional constraint to the work
            .build()

        workManager.enqueue(periodicWorkRequest)

        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id)
            .observe(requireActivity(), Observer {
                // output data from within the worker
                val time = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentData = time.format(Date())
                // Will display in real time the statue
                // blocked-> enqueued -> running -> success
                Logger.i("Update Database Worker at $currentData", it.state.name)

                //Observer the output data we send (optional)
                if (it.state.isFinished){
                    val outputtedData = it.outputData
                    val message = outputtedData.getString(UpdateDatabaseWorker.UPDATE_DB_TIME)
                    Logger.i("Update was preformed at: ", message ?: "No Data")
                }
            })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}