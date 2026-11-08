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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.gbc.comp3074.shaikh.hunttrail.R
import ca.gbc.comp3074.shaikh.hunttrail.data.HuntRepository
import ca.gbc.comp3074.shaikh.hunttrail.data.tagNames
import ca.gbc.comp3074.shaikh.hunttrail.data.PointWithTags
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class PointDetailsFragment : Fragment(R.layout.fragment_point_details) {

    private val repo by lazy { HuntRepository.get(requireContext()) }
    private var current: PointWithTags? = null   // the point shown on screen

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Id sent from the Hunt List
        val pointId = arguments?.getInt("pointId", -1) ?: -1

        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvAddress = view.findViewById<TextView>(R.id.tvAddress)
        val tvTask = view.findViewById<TextView>(R.id.tvTask)
        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroupTags)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)

        // Rating 1-5 stars, saved to Room right away (FR3)
        ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            val item = current ?: return@setOnRatingBarChangeListener
            if (fromUser) {
                viewLifecycleOwner.lifecycleScope.launch {
                    repo.setRating(item.point.id, rating.toInt())
                }
                Toast.makeText(requireContext(), "Rated ${rating.toInt()} stars", Toast.LENGTH_SHORT).show()
            }
        }

        // Open the full-screen Map for this point (FR7)
        view.findViewById<MaterialButton>(R.id.btnViewMap).setOnClickListener {
            val item = current ?: return@setOnClickListener
            findNavController().navigate(
                R.id.action_details_to_map,
                bundleOf("pointId" to item.point.id)
            )
        }

        // Directions from the current location (FR8)
        view.findViewById<MaterialButton>(R.id.btnDirections).setOnClickListener {
            val item = current ?: return@setOnClickListener
            MapsIntents.openDirections(requireContext(), item.point.address)
        }

        // Share by email with an implicit intent (FR9)
        view.findViewById<MaterialButton>(R.id.btnShare).setOnClickListener {
            val p = current?.point ?: return@setOnClickListener
            val email = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_SUBJECT, "HuntTrail challenge: ${p.name}")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Location: ${p.name}\nAddress: ${p.address}\nTask: ${p.task}"
                )
            }
            startActivity(Intent.createChooser(email, "Share point by email"))
        }

        // Edit: open Add/Edit with this point's id
        view.findViewById<MaterialButton>(R.id.btnEdit).setOnClickListener {
            val item = current ?: return@setOnClickListener
            findNavController().navigate(
                R.id.action_details_to_addEdit,
                bundleOf("pointId" to item.point.id)
            )
        }

        // Delete: ask first, then remove from Room and go back
        view.findViewById<MaterialButton>(R.id.btnDelete).setOnClickListener {
            val item = current ?: return@setOnClickListener
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete point?")
                .setMessage("\"${item.point.name}\" will be removed from your hunt.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete") { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        repo.deletePoint(item.point)
                        Toast.makeText(requireContext(), "Point deleted", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                }
                .show()
        }

        // Watch this point in the database - the screen refreshes after Edit too
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                repo.point(pointId).collect { item ->
                    if (item == null) return@collect   // was just deleted
                    current = item

                    (activity as? AppCompatActivity)?.supportActionBar?.title = item.point.name
                    tvName.text = item.point.name
                    tvAddress.text = item.point.address
                    tvTask.text = item.point.task.ifBlank { "No task yet" }
                    ratingBar.rating = item.point.rating.toFloat()

                    chipGroup.removeAllViews()
                    item.tagNames.forEach { tag ->
                        chipGroup.addView(Chip(requireContext()).apply {
                            text = tag
                            isClickable = false
                        })
                    }
                }
            }
        }
    }
}