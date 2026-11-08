package ca.gbc.comp3074.shaikh.hunttrail.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import ca.gbc.comp3074.shaikh.hunttrail.R
import ca.gbc.comp3074.shaikh.hunttrail.data.HuntRepository
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

class MapFragment : Fragment(R.layout.fragment_map) {

    private val repo by lazy { HuntRepository.get(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName = view.findViewById<TextView>(R.id.tvMapName)
        val tvAddress = view.findViewById<TextView>(R.id.tvMapAddress)
        val btn = view.findViewById<MaterialButton>(R.id.btnMapDirections)

        // Opened from Details -> show that point.
        // Opened from the bottom bar -> no point id, show all points.
        val pointId = arguments?.getInt("pointId", -1) ?: -1

        viewLifecycleOwner.lifecycleScope.launch {
            val item = if (pointId > 0) repo.point(pointId).first() else null

            if (item != null) {
                val p = item.point
                tvName.text = p.name
                // Show the coordinates saved by the Geocoder (if any)
                tvAddress.text = if (p.latitude != null && p.longitude != null) {
                    String.format(Locale.US, "%s\n%.5f, %.5f", p.address, p.latitude, p.longitude)
                } else {
                    p.address
                }
                btn.text = "Get Directions"
                btn.setOnClickListener { MapsIntents.openDirections(requireContext(), p.address) }
            } else {
                val count = repo.points.first().size
                tvName.text = "All hunt points"
                tvAddress.text = "$count points saved"
                btn.text = "Open in Google Maps"
                btn.setOnClickListener { MapsIntents.openLocation(requireContext(), "Toronto, ON") }
            }
        }
    }
}