package com.test.app.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.test.app.R
import com.test.app.core.domain.usecases.IScreenSelectorUC.ScreenType
import com.test.app.databinding.ActivityMainBinding
import com.test.app.framework.ActivityResultClient
import com.test.app.framework.GoogleLoginModel
import com.test.app.framework.ScreenNavigator
import com.test.app.framework.appGoogleScopes
import dagger.android.support.DaggerAppCompatActivity
import java.lang.ref.WeakReference
import javax.inject.Inject

/*
 * This application uses relaxed variant of Clean Architecture,
 * where ViewModels can communicate with models directly (bypassing Interactors) if there is no additional logic involved.
 * This relaxation is done to reduce boilerplate code.
 */

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewmodel: MainActivityViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var client: ActivityResultClient

    private lateinit var v:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setupModelsWithActivity()
        super.onCreate(savedInstanceState)

        v = ActivityMainBinding.inflate(layoutInflater)
        setContentView(v.root)
        setSupportActionBar(findViewById(R.id.toolbar))

        ScreenNavigator.setNavController(navController)

        if(savedInstanceState == null) {
            setInitialFragment()
        } else {
            setDefaultNavGraph()
        }

        // Implement result client
        client.intentFlow
                .observe(this, {
                    startActivityForResult(it.intent, it.requestCode)
                })
        }

    private val navController:NavController by lazy {
        // See this bug: https://stackoverflow.com/q/50502269
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
    }
    
    // See: https://issuetracker.google.com/issues/171901428
    fun setInitialFragment() {
        viewmodel.screenType.observe(this, {

            val navInflater = navController.navInflater
            val graph = navInflater.inflate(R.navigation.nav_graph)

            when(it) {
                ScreenType.SIGN_UP -> graph.startDestination = R.id.LoginFragment
                ScreenType.CONTACTS -> graph.startDestination = R.id.ContactsFragment
            }

            navController.graph = graph
            val appBarConfiguration = AppBarConfiguration(setOf(R.id.LoginFragment, R.id.ContactsFragment))
            setupActionBarWithNavController(navController, appBarConfiguration)
        })

        viewmodel.onAppCreate()
    }

    fun setDefaultNavGraph() {
        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.nav_graph)

        graph.startDestination = R.id.ContactsFragment // It does not matter after config change

        navController.graph = graph
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.LoginFragment, R.id.ContactsFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupModelsWithActivity() {
        // Login Model
        GoogleLoginModel.setActivity(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        client.onActivityResult(requestCode, resultCode, data)
    }
}