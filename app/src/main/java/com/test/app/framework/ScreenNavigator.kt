package com.test.app.framework

import androidx.annotation.IdRes
import androidx.navigation.NavController
import com.test.app.presentation.IScreenNavigator
import java.lang.ref.WeakReference

class ScreenNavigator : IScreenNavigator {
    companion object {
        lateinit var navController:WeakReference<NavController>

        fun setNavController(navW:NavController) {
            navController = WeakReference(navW)
        }
    }

    override fun navTo(@IdRes id:Int) {
        navController.get()!!.navigate(id)
    }
}