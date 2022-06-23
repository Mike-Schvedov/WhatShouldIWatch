package com.mikeschvedov.whatshouldiwatch.ui.favorites

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikeschvedov.whatshouldiwatch.R
import com.mikeschvedov.whatshouldiwatch.databinding.FavoritesFragmentBinding
import com.mikeschvedov.whatshouldiwatch.databinding.FragmentSettingsBinding
import com.mikeschvedov.whatshouldiwatch.ui.settings.SettingsViewModel

class FavoritesFragment : Fragment() {

    private var _binding: FavoritesFragmentBinding? = null
    private val binding get() = _binding!!

    lateinit var favoritesViewModel: FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        favoritesViewModel =
            ViewModelProvider(this).get(FavoritesViewModel::class.java)

        _binding = FavoritesFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root    }


}