package ca.gbc.comp3074.shaikh.hunttrail.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.gbc.comp3074.shaikh.hunttrail.R

// Connects the list of points to the RecyclerView (one card per point)
class PointAdapter(
    private val onClick: (PointUi) -> Unit
) : RecyclerView.Adapter<PointAdapter.PointViewHolder>() {

    private var items: List<PointUi> = emptyList()

    fun submit(list: List<PointUi>) {
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
        val point = items[position]
        holder.name.text = point.name
        holder.address.text = point.address
        holder.tags.text = point.tags.joinToString("   ") { "#$it" }
        holder.rating.rating = point.rating.toFloat()
        holder.itemView.setOnClickListener { onClick(point) }
    }

    override fun getItemCount(): Int = items.size
}