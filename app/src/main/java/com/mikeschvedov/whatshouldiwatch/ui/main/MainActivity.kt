package com.mikeschvedov.whatshouldiwatch.ui.main

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mikeschvedov.whatshouldiwatch.R
import com.mikeschvedov.whatshouldiwatch.databinding.ActivityMainBinding
import com.mikeschvedov.whatshouldiwatch.recievers.AirplaneModeChangedReciever
import com.mikeschvedov.whatshouldiwatch.utils.Logger
import com.mikeschvedov.whatshouldiwatch.utils.workers.UpdateDatabaseWorker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var mainActivityViewModel: MainActivityViewModel

    @Inject
    lateinit var airplaneReceiver: AirplaneModeChangedReciever

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        /* Binding */
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* View Model */
        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        /* Navigation */
        supportActionBar?.hide()
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_search,
                R.id.navigation_favorites,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        /* Setting LiveData Observers */
        liveDataObservers()

        /* Setup Broadcast Receivers */
        // AirplaneMode Receiver
        airplaneReceiver = AirplaneModeChangedReciever()
        //IntentFilter - Used to determine which apps should receive which intents
        //This is a dynamic Broadcast Receiver - dynamically created here.
        //Most receiver events can onlt be called by dynamic receivers.
        //Static receivers are setup in the manifest and work even if app is closed, but are rarely used
        IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED).also {
            registerReceiver(airplaneReceiver,it)
        }
    }

    private fun liveDataObservers() {
        // Error Observer
        mainActivityViewModel.errorMessage.observe(this, Observer { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })
    }

    // TODO: Check if this is not needed, and can be deleted.
    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_activity_main).navigateUp() || super.onSupportNavigateUp()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(airplaneReceiver)
    }
}