package com.mikeschvedov.whatshouldiwatch.ui.overview

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mikeschvedov.whatshouldiwatch.R
import com.mikeschvedov.whatshouldiwatch.databinding.OverviewFragmentBinding
import com.mikeschvedov.whatshouldiwatch.models.response.TmdbItem
import com.mikeschvedov.whatshouldiwatch.utils.Constants
import com.squareup.picasso.Picasso


class OverviewFragment : Fragment() {

    private var _binding: OverviewFragmentBinding? = null
    private val binding get() = _binding!!

    var isDescOpen = false

    private lateinit var overviewViewModel: OverviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /*---------- View Model ----------*/
        overviewViewModel =
            ViewModelProvider(this).get(OverviewViewModel::class.java)
        /*---------- Binding ----------*/
        _binding = OverviewFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        settingPassedArguments()
        setOnClick()
        return root
    }

    private fun setOnClick() {
        // Read Description onClick
     //   val guideTop = binding.guideline2
      //  val guideLow = binding.guideline3
        binding.readDesc.setOnClickListener {
            if (isDescOpen) { // if is open then close
              //  guideTop.setGuidelinePercent(0.7f)
               // guideLow.setGuidelinePercent(0.8f)
                binding.mediaDescription.visibility = View.GONE
                binding.readDesc.text = getString(R.string.read_desc)
                binding.readDesc.setBackgroundResource(R.drawable.custom_description)
                isDescOpen = false
            } else { // if is close then open
              //  guideTop.setGuidelinePercent(0.4f)
              //  guideLow.setGuidelinePercent(0.5f)

                binding.mediaDescription.visibility = View.VISIBLE
                binding.readDesc.text = getString(R.string.minimize)
                binding.readDesc.setBackgroundResource(R.drawable.custom_description_hide)
                isDescOpen = true
            }
        }
     /*   // Back Button onClick
        binding.backButtonIv.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }*/
    }

    private fun settingPassedArguments() {

        val bundle = arguments?.getParcelable<TmdbItem>("media")
        val year = bundle?.releaseDate?.take(4)
        val title = bundle?.name

        binding.mediaTitleTextview.text = "$title  ($year)"
        binding.mediaDescription.text = bundle?.overview

        Picasso.get().load(Uri.parse(Constants.IMAGE_LOCATION + bundle?.backdropPath))
            .placeholder(R.drawable.loading)
            .into(binding.mediaImageview)

        Picasso.get().load(Uri.parse(Constants.IMAGE_LOCATION + bundle?.posterPath))
            .placeholder(R.drawable.loading)
            .into(binding.posterImage)
    }

    override fun onResume() {
        super.onResume()

    }
}