package com.epishie.spacial.ui.features.common

import android.support.v4.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.epishie.spacial.R
import kotlinx.android.synthetic.main.bottom_nav.*

abstract class BottomNavigationFragment : Fragment() {
    private companion object {
        const val ANIM_DELAY = 200L
    }

    protected abstract val bottomNavItemId: Int

    override fun onStart() {
        super.onStart()
        bottomNav.selectedItemId = bottomNavItemId
        bottomNav.setOnNavigationItemSelectedListener {
            bottomNav.postDelayed({
                val navController = findNavController()
                val options = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setEnterAnim(R.anim.nav_default_enter_anim)
                        .setExitAnim(R.anim.nav_default_exit_anim)
                        .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                        .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                        .setPopUpTo(navController.graph.startDestination, false)
                        .build()
                navController.navigate(it.itemId, null, options)
            }, ANIM_DELAY)
        }
    }
}