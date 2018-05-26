package com.epishie.spacial.ui.features.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epishie.spacial.R
import com.epishie.spacial.ui.features.common.BottomNavigationFragment

class FavoritesFragment : BottomNavigationFragment() {
    override val bottomNavItemId: Int = R.id.favoritesFragment

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.favorites_fragment, container, false)
    }
}