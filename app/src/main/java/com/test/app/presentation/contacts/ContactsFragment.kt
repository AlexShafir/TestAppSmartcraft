package com.test.app.presentation.contacts

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.test.app.R
import com.test.app.core.data.IConnection.InternetState
import com.test.app.core.domain.entities.Contact
import com.test.app.databinding.FragmentContactsBinding
import dagger.android.support.DaggerFragment
import java.util.*
import javax.inject.Inject


class ContactsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewmodel: ContactsFragmentViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentContactsBinding? = null
    private val v get() = _binding!!

    private var snackbar: Snackbar? = null

    // Data

    private val contactsAdapter: ContactsAdapter = ContactsAdapter(contacts)

    companion object {
        // These objects are of low importance. We do not care if they do not survive system-initiated process death
        // So we will not save their state & will save speed during config change
        // See https://developer.android.com/topic/libraries/architecture/saving-states
        private var freshProcess: Boolean = true
        private var originalContacts: List<Contact> = listOf()
        private var contacts: MutableList<Contact> =
            mutableListOf() // we only need configuration change
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return v.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        if (savedInstanceState != null && freshProcess) { // Process was restarted, cancel state of specific views
            v.searchView.setQuery("", true)
        }

        freshProcess = false

        // Setup views
        v.searchView.isSubmitButtonEnabled = false

        v.recyclerView.adapter = contactsAdapter
        v.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
        }

        // Listeners
        viewmodel.contactsFlow.observe(viewLifecycleOwner, {

            originalContacts = it
            if (v.searchView.query.isBlank()) { // TODO: Replace with dataset change
                contacts.clear()
                contacts.addAll(originalContacts)
                contacts.sortBy { it.name }
                contactsAdapter.notifyDataSetChanged()
            }

        })

        v.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val searchText = newText.toLowerCase(Locale.getDefault())

                contacts.clear()

                if (searchText.isBlank()) { // return to original state
                    contacts.addAll(originalContacts)
                } else {
                    val filtered = originalContacts.filter {
                        it.name.toLowerCase(Locale.getDefault()).contains(
                            searchText
                        ) || it.company.toLowerCase(Locale.getDefault()).contains(searchText)
                    }
                    contacts.addAll(filtered)
                }

                contacts.sortBy { it.name }
                contactsAdapter.notifyDataSetChanged()

                return true
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_contacts, menu)

        val syncItem = menu.findItem(R.id.action_sync)

        viewmodel.syncingFlow.observe(viewLifecycleOwner, { syncing ->
            // TODO: Animation
            if (syncing) {
                showSyncSnackbar()
            } else {
                dismissSnackbar()
            }
        })

        viewmodel.internetState.observe(viewLifecycleOwner, {
            if(it == InternetState.ONLINE || it == InternetState.NONE) {
                syncItem.isEnabled = true
                syncItem.icon.alpha = 255
            } else {
                syncItem.isEnabled = false
                syncItem.icon.alpha = 130
            }

        })
    }

    private fun showSyncSnackbar() {
        if(snackbar == null) {
            snackbar = Snackbar.make(requireView(), R.string.msg_syncing_contacts, Snackbar.LENGTH_INDEFINITE)
            snackbar!!.show()
        }
    }

    private fun dismissSnackbar() {
        if(snackbar != null) {
            snackbar!!.dismiss()
            snackbar = null
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sync -> {
                viewmodel.onSyncClick(); true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}