package com.epishie.spacial.ui.features.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.epishie.spacial.R
import com.epishie.spacial.component
import com.epishie.spacial.ui.extensions.reObserve
import com.epishie.spacial.ui.features.common.ViewModelFactory
import kotlinx.android.synthetic.main.main_fragment.*
import javax.inject.Inject

class MainFragment : Fragment() {
    @Inject
    lateinit var vmFactory: ViewModelFactory<MainViewModel>
    private lateinit var vm: MainViewModel
    private lateinit var catalogAdapter: PreviewAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        catalogAdapter = PreviewAdapter()
        catalogs.layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false)
        catalogs.adapter = catalogAdapter
        catalogs.addItemDecoration(DividerItemDecoration(requireContext(),
                DividerItemDecoration.HORIZONTAL).apply {
            ContextCompat.getDrawable(requireContext(), R.drawable.preview_divider)?.let {
                setDrawable(it)
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireContext().component.inject(this)
        vm = ViewModelProviders.of(this, vmFactory)[MainViewModel::class.java]
        vm.catalogs.reObserve(this, catalogsObserver)

        search.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.goToDiscover())
        }
        catalogAdapter.onItemClickListener = {
            findNavController().navigate(MainFragmentDirections.viewCatalog(it))
        }
        catalogAdapter.onMoreClickListener = {
            findNavController().navigate(MainFragmentDirections.viewCatalogList())
        }
    }

    private val catalogsObserver = Observer<PagedList<Preview>> {
        catalogAdapter.submitList(it)
    }
}