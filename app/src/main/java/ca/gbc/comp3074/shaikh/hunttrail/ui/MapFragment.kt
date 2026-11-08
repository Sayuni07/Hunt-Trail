package ca.gbc.comp3074.shaikh.hunttrail.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import ca.gbc.comp3074.shaikh.hunttrail.R
import com.google.android.material.button.MaterialButton

class MapFragment : Fragment(R.layout.fragment_map) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName = view.findViewById<TextView>(R.id.tvMapName)
        val tvAddress = view.findViewById<TextView>(R.id.tvMapAddress)
        val btn = view.findViewById<MaterialButton>(R.id.btnMapDirections)

        // Opened from Details -> show that point.
        // Opened from the bottom bar -> no point, show all points.
        val pointId = arguments?.getInt("pointId", -1) ?: -1
        val point = SampleData.points.find { it.id == pointId }

        if (point != null) {
            tvName.text = point.name
            tvAddress.text = point.address
            btn.text = "Get Directions"
            btn.setOnClickListener { MapsIntents.openDirections(requireContext(), point.address) }
        } else {
            tvName.text = "All hunt points"
            tvAddress.text = "${SampleData.points.size} points in Toronto"
            btn.text = "Open in Google Maps"
            btn.setOnClickListener { MapsIntents.openLocation(requireContext(), "Toronto, ON") }
        }
    }
}