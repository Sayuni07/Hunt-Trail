package ca.gbc.comp3074.shaikh.hunttrail.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.gbc.comp3074.shaikh.hunttrail.R
import ca.gbc.comp3074.shaikh.hunttrail.data.PointWithTags
import ca.gbc.comp3074.shaikh.hunttrail.data.tagNames

// Connects the list of points (from Room) to the RecyclerView - one card per point
class PointAdapter(
    private val onClick: (PointWithTags) -> Unit
) : RecyclerView.Adapter<PointAdapter.PointViewHolder>() {

    private var items: List<PointWithTags> = emptyList()

    fun submit(list: List<PointWithTags>) {
        items = list
        notifyDataSetChanged()
    }

    class PointViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvName)
        val address: TextView = view.findViewById(R.id.tvAddress)
        val tags: TextView = view.findViewById(R.id.tvTags)
        val rating: RatingBar = view.findViewById(R.id.ratingBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_point, parent, false)
        return PointViewHolder(view)
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.point.name
        holder.address.text = item.point.address
        holder.tags.text = item.tagNames.joinToString("   ") { "#$it" }
        holder.tags.visibility = if (item.tags.isEmpty()) View.GONE else View.VISIBLE
        holder.rating.rating = item.point.rating.toFloat()
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size
}