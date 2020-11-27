package com.test.app.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.test.app.R
import com.test.app.core.data.IConnection
import com.test.app.databinding.FragmentLoginBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class LoginFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewmodel: LoginFragmentViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentLoginBinding? = null
    private val v get() = _binding!!

    private var snackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return v.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v.signInButton.setOnClickListener { viewmodel.onSignUpButtonClick() }

        viewmodel.internetState.observe(viewLifecycleOwner, {
            when(it) {
                IConnection.InternetState.ONLINE -> {
                    v.signInButton.isEnabled = true
                    dismissSnackbar()
                }
                IConnection.InternetState.OFFLINE -> {
                    v.signInButton.isEnabled = false
                    showInternetSnackbar()
                }
            }
        })

    }

    private fun showInternetSnackbar() {
        if(snackbar == null) {
            snackbar = Snackbar.make(requireView(), R.string.msg_no_internet, Snackbar.LENGTH_INDEFINITE)
            snackbar!!.show()
        }
    }

    private fun dismissSnackbar() {
        if(snackbar != null) {
            snackbar!!.dismiss()
            snackbar = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}