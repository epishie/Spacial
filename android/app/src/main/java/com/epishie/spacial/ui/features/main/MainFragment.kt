package com.epishie.spacial.ui.features.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.epishie.spacial.R
import com.epishie.spacial.component
import com.epishie.spacial.ui.features.common.ViewModelFactory
import kotlinx.android.synthetic.main.main_fragment.*
import javax.inject.Inject

class MainFragment : Fragment() {
    @Inject
    lateinit var vmFactory: ViewModelFactory<MainViewModel>
    private lateinit var vm: MainViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireContext().component.inject(this)
        vm = ViewModelProviders.of(this, vmFactory)[MainViewModel::class.java]

        search.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.goToDiscover())
        }
    }
}