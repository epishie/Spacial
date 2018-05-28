package com.epishie.spacial.ui.features.catalog

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epishie.spacial.R
import com.epishie.spacial.component
import com.epishie.spacial.ui.extensions.reObserve
import com.epishie.spacial.ui.features.adapter.Thumbnail
import com.epishie.spacial.ui.features.adapter.ThumbnailAdapter
import com.epishie.spacial.ui.features.common.ViewModelFactory
import com.epishie.spacial.ui.widget.SpaceGridItemDecoration
import kotlinx.android.synthetic.main.catalog_list_fragment.*
import kotlinx.android.synthetic.main.catalog_list_fragment.view.*
import javax.inject.Inject

class CatalogListFragment : Fragment() {
    @Inject
    lateinit var vmFactory: ViewModelFactory<CatalogListViewModel>
    lateinit var vm: CatalogListViewModel
    lateinit var thumbnailAdapter: ThumbnailAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.catalog_list_fragment, container, false)
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
        vm = ViewModelProviders.of(this, vmFactory)[CatalogListViewModel::class.java]

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        vm.catalogs.reObserve(this, catalogsObserver)
    }

    private val catalogsObserver = Observer<PagedList<Thumbnail>> {
        thumbnailAdapter.submitList(it)
    }
}