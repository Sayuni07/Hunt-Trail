package ca.gbc.comp3074.shaikh.hunttrail.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.gbc.comp3074.shaikh.hunttrail.R
import com.google.android.material.button.MaterialButton

class ContactAdapter(
    private val onCall: (ContactUi) -> Unit,
    private val onSms: (ContactUi) -> Unit,
    private val onEmail: (ContactUi) -> Unit,
    private val onEdit: (ContactUi) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var items: List<ContactUi> = emptyList()

    fun submit(list: List<ContactUi>) {
        items = list
        notifyDataSetChanged()
    }

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvContactName)
        val phone: TextView = view.findViewById(R.id.tvPhone)
        val email: TextView = view.findViewById(R.id.tvEmail)
        val btnCall: MaterialButton = view.findViewById(R.id.btnCall)
        val btnSms: MaterialButton = view.findViewById(R.id.btnSms)
        val btnEmail: MaterialButton = view.findViewById(R.id.btnEmail)
        val btnEdit: MaterialButton = view.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val c = items[position]
        holder.name.text = c.name
        holder.phone.text = c.phone
        holder.email.text = c.email
        holder.btnCall.setOnClickListener { onCall(c) }
        holder.btnSms.setOnClickListener { onSms(c) }
        holder.btnEmail.setOnClickListener { onEmail(c) }
        holder.btnEdit.setOnClickListener { onEdit(c) }
    }

    override fun getItemCount(): Int = items.size
}