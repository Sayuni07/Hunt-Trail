package ca.gbc.comp3074.shaikh.hunttrail.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.gbc.comp3074.shaikh.hunttrail.R
import ca.gbc.comp3074.shaikh.hunttrail.data.HuntRepository
import ca.gbc.comp3074.shaikh.hunttrail.data.TeamContact
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

// Team Contacts: add, view, edit, remove and contact members (FR10)
class TeamContactsFragment : Fragment(R.layout.fragment_team_contacts) {

    private val repo by lazy { HuntRepository.get(requireContext()) }

    private lateinit var adapter: ContactAdapter
    private lateinit var tvEmpty: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ContactAdapter(
            onCall = { c ->
                // ACTION_DIAL opens the dialer - no permission needed
                open(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${c.phone}")))
            },
            onSms = { c ->
                open(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${c.phone}"))
                    .putExtra("sms_body", "Hi ${c.name}, "))
            },
            onEmail = { c ->
                open(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${c.email}"))
                    .putExtra(Intent.EXTRA_SUBJECT, "HuntTrail"))
            },
            onEdit = { c -> showContactDialog(c) }
        )

        tvEmpty = view.findViewById(R.id.tvEmpty)
        val recycler = view.findViewById<RecyclerView>(R.id.rvContacts)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        view.findViewById<FloatingActionButton>(R.id.fabAddContact).setOnClickListener {
            showContactDialog(null)
        }

        // Watch the contacts table - the list updates by itself
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                repo.contacts.collect { list ->
                    adapter.submit(list)
                    tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    // existing == null -> Add, otherwise Edit (with a Delete option)
    private fun showContactDialog(existing: TeamContact?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_contact, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etContactName)
        val etPhone = dialogView.findViewById<TextInputEditText>(R.id.etContactPhone)
        val etEmail = dialogView.findViewById<TextInputEditText>(R.id.etContactEmail)

        if (existing != null) {
            etName.setText(existing.name)
            etPhone.setText(existing.phone)
            etEmail.setText(existing.email)
        }

        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (existing == null) "Add contact" else "Edit contact")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "Name is required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val contact = TeamContact(
                    id = existing?.id ?: 0,      // 0 = insert, otherwise update
                    name = name,
                    phone = etPhone.text.toString().trim(),
                    email = etEmail.text.toString().trim()
                )
                viewLifecycleOwner.lifecycleScope.launch { repo.saveContact(contact) }
            }

        if (existing != null) {
            builder.setNeutralButton("Delete") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch { repo.deleteContact(existing) }
            }
        }
        builder.show()
    }

    // Start an implicit intent without crashing if no app can handle it
    private fun open(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No app found for this action", Toast.LENGTH_SHORT).show()
        }
    }
}