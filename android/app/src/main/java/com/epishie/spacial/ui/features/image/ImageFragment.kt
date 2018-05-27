package com.epishie.spacial.ui.features.image

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epishie.spacial.component
import com.epishie.spacial.databinding.ImageFragmentBinding
import com.epishie.spacial.ui.features.common.ViewModelFactory
import kotlinx.android.synthetic.main.image_fragment.*
import javax.inject.Inject

class ImageFragment : Fragment() {
    @Inject
    lateinit var vmFactory: ViewModelFactory<ImageViewModel>
    private lateinit var binding: ImageFragmentBinding
    private lateinit var vm: ImageViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ImageFragmentBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireContext().component.inject(this)
        vm = ViewModelProviders.of(this, vmFactory)[ImageViewModel::class.java]
        binding.vm = vm
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        vm.load(ImageFragmentArgs.fromBundle(arguments).id)
    }
}