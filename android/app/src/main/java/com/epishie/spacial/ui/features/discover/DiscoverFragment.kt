package com.epishie.spacial.ui.features.discover

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.navigation.fragment.findNavController
import com.epishie.spacial.R
import com.epishie.spacial.component
import com.epishie.spacial.ui.extensions.reObserve
import com.epishie.spacial.ui.features.adapter.Thumbnail
import com.epishie.spacial.ui.features.adapter.ThumbnailAdapter
import com.epishie.spacial.ui.features.common.ViewModelFactory
import com.epishie.spacial.ui.widget.SpaceGridItemDecoration
import kotlinx.android.synthetic.main.discover_fragment.*
import kotlinx.android.synthetic.main.discover_fragment.view.*
import javax.inject.Inject

class DiscoverFragment : Fragment() {
    @Inject
    lateinit var vmFactory: ViewModelFactory<DiscoverViewModel>
    private lateinit var vm: DiscoverViewModel
    private lateinit var thumbnailAdapter: ThumbnailAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.discover_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thumbnailAdapter = ThumbnailAdapter()
        with (view.thumbnails) {
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(SpaceGridItemDecoration(
                    requireContext().resources.getDimension(R.dimen.unit_0).toInt(),
                    2,
                    SpaceGridItemDecoration.VERTICAL))
            adapter = thumbnailAdapter
            setHasFixedSize(true)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireContext().component.inject(this)
        vm = ViewModelProviders.of(this, vmFactory)[DiscoverViewModel::class.java]
        vm.thumbnails.reObserve(this, thumbnailsObserver)
        vm.savable.reObserve(this, savableObserver)
        vm.empty.reObserve(this, emptyObserver)
        vm.error.reObserve(this, errorObserver)

        if (search.requestFocusFromTouch()) {
            requireActivity().window
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                vm.search(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        retry.setOnClickListener {
            vm.retry()
        }
        thumbnailAdapter.onItemClickListener = {
            findNavController().navigate(DiscoverFragmentDirections.viewDiscoveredPhoto(it))
        }
    }

    private val thumbnailsObserver = Observer<PagedList<Thumbnail>> {
        thumbnailAdapter.submitList(it)
    }

    private val savableObserver = Observer<Boolean> {
        TransitionManager.beginDelayedTransition(appBar)
        save.visibility = when (it) {
            true -> View.VISIBLE
            else -> View.GONE
        }
    }

    private val emptyObserver = Observer<Boolean> {
        TransitionManager.beginDelayedTransition(appBar)
        emptyState.visibility = when (it) {
            true -> View.VISIBLE
            else -> View.GONE
        }
    }
    private val errorObserver = Observer<CharSequence?> {
        TransitionManager.beginDelayedTransition(appBar)
        if (it != null) {
            errorState.visibility = View.VISIBLE
            errorMessage.text = it
        } else {
            errorState.visibility = View.GONE
        }
    }
}