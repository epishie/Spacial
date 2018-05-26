package com.epishie.spacial.ui.features.catalogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epishie.spacial.R
import com.epishie.spacial.ui.features.common.BottomNavigationFragment

class CatalogsFragment : BottomNavigationFragment() {
    override val bottomNavItemId: Int = R.id.catalogsFragment

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.catalogs_fragment, container, false)
    }
}