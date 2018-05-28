package com.epishie.spacial.ui.features.catalog

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.*
import androidx.navigation.fragment.findNavController
import com.epishie.spacial.R
import com.epishie.spacial.component
import com.epishie.spacial.ui.extensions.reObserve
import com.epishie.spacial.ui.extensions.tintIcons
import com.epishie.spacial.ui.features.adapter.Thumbnail
import com.epishie.spacial.ui.features.adapter.ThumbnailAdapter
import com.epishie.spacial.ui.features.common.ViewModelFactory
import com.epishie.spacial.ui.widget.SpaceGridItemDecoration
import kotlinx.android.synthetic.main.catalog_fragment.*
import kotlinx.android.synthetic.main.catalog_fragment.view.*
import javax.inject.Inject

class CatalogFragment : Fragment() {
    @Inject
    lateinit var vmFactory: ViewModelFactory<CatalogViewModel>
    private lateinit var vm: CatalogViewModel
    private lateinit var thumbnailAdapter: ThumbnailAdapter
    private lateinit var name: String

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        setHasOptionsMenu(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        name = CatalogFragmentArgs.fromBundle(arguments).name
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.catalog_fragment, container, false)
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
        vm = ViewModelProviders.of(this, vmFactory)[CatalogViewModel::class.java]

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.title = name

        vm.thumbnails.reObserve(this, thumbnailsObserver)
        thumbnailAdapter.onItemClickListener = {
            findNavController().navigate(CatalogFragmentDirections.viewCatalogPhoto(it))
        }

        if (savedInstanceState == null) {
            vm.search(name)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.catalog, menu)
        menu.tintIcons(requireContext())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                vm.delete()
                findNavController().popBackStack()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private val thumbnailsObserver = Observer<PagedList<Thumbnail>> {
        thumbnailAdapter.submitList(it)
    }
}