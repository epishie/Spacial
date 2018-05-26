package com.epishie.spacial.ui.features.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epishie.spacial.R
import com.epishie.spacial.ui.features.common.BottomNavigationFragment

class DiscoverFragment : BottomNavigationFragment() {
    override val bottomNavItemId: Int = R.id.discoverFragment

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.discover_fragment, container, false)
    }
}