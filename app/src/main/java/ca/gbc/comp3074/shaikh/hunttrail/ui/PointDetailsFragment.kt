package ca.gbc.comp3074.shaikh.hunttrail.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ca.gbc.comp3074.shaikh.hunttrail.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PointDetailsFragment : Fragment(R.layout.fragment_point_details) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the id sent from the Hunt List and find that point
        val pointId = arguments?.getInt("pointId") ?: -1
        val point = SampleData.points.find { it.id == pointId }
        if (point == null) {
            findNavController().popBackStack()
            return
        }

        // Fill the screen (FR5)
        (activity as AppCompatActivity).supportActionBar?.title = point.name
        view.findViewById<TextView>(R.id.tvName).text = point.name
        view.findViewById<TextView>(R.id.tvAddress).text = point.address
        view.findViewById<TextView>(R.id.tvTask).text = point.task

        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroupTags)
        point.tags.forEach { tag ->
            chipGroup.addView(Chip(requireContext()).apply {
                text = tag
                isClickable = false
            })
        }

        // Rating 1-5 stars (FR3) - saved to Room in the database stage
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        ratingBar.rating = point.rating.toFloat()
        ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                Toast.makeText(requireContext(), "Rated ${rating.toInt()} stars", Toast.LENGTH_SHORT).show()
            }
        }

        // Open the full-screen Map for this point (FR7)
        view.findViewById<MaterialButton>(R.id.btnViewMap).setOnClickListener {
            findNavController().navigate(
                R.id.action_details_to_map,
                bundleOf("pointId" to point.id)
            )
        }

        // Directions from current location (FR8)
        view.findViewById<MaterialButton>(R.id.btnDirections).setOnClickListener {
            MapsIntents.openDirections(requireContext(), point.address)
        }

        // Share by email using an implicit intent (FR9)
        view.findViewById<MaterialButton>(R.id.btnShare).setOnClickListener {
            val email = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_SUBJECT, "HuntTrail challenge: ${point.name}")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Location: ${point.name}\nAddress: ${point.address}\nTask: ${point.task}"
                )
            }
            startActivity(Intent.createChooser(email, "Share point by email"))
        }

        view.findViewById<MaterialButton>(R.id.btnEdit).setOnClickListener {
            findNavController().navigate(
                R.id.action_details_to_addEdit,
                bundleOf("pointId" to point.id)
            )
        }

        // Ask before deleting
        view.findViewById<MaterialButton>(R.id.btnDelete).setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete point?")
                .setMessage("\"${point.name}\" will be removed from your hunt.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete") { _, _ ->
                    Toast.makeText(requireContext(), "Point deleted", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                .show()
        }
    }
}