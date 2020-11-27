package com.test.app.presentation

import androidx.annotation.IdRes

interface IScreenNavigator {
    fun navTo(@IdRes id:Int)
}