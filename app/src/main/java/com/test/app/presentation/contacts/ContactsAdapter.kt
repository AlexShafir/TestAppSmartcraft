package com.test.app.presentation.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.app.R
import com.test.app.core.domain.entities.Contact
import com.test.app.databinding.ContactItemBinding
import com.test.app.presentation.GlideApp

class ContactsAdapter(private val contacts:List<Contact>) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    private lateinit var stubCompanyText:String

    class ViewHolder(val b: ContactItemBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        stubCompanyText = parent.context.getString(R.string.stub_no_company)
        val binding = ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = contacts[position]
        val v = holder.b

        v.textName.text = item.name
        v.textOrganization.text = if(item.company.isNotEmpty()) item.company else stubCompanyText

        if(item.avatarUrl.isNotEmpty()) {
            GlideApp // Caching by default
                .with(holder.itemView)
                .load(item.avatarUrl)
                //.placeholder(circularProgressDrawable)
                .into(v.imageAvatar)
        }

    }

    override fun getItemCount(): Int = contacts.size

}