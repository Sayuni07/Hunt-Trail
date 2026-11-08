package ca.gbc.comp3074.shaikh.hunttrail.ui

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import ca.gbc.comp3074.shaikh.hunttrail.R
import ca.gbc.comp3074.shaikh.hunttrail.data.HuntRepository
import ca.gbc.comp3074.shaikh.hunttrail.data.tagNames
import ca.gbc.comp3074.shaikh.hunttrail.data.PointOfInterest
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

// One screen for both Add (no pointId) and Edit (pointId sent from Details)
class AddEditPointFragment : Fragment(R.layout.fragment_add_edit_point) {

    private val repo by lazy { HuntRepository.get(requireContext()) }

    private var editingId = 0                     // 0 = new point
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var geocodedAddress: String? = null   // address the lat/lng belong to

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tilName = view.findViewById<TextInputLayout>(R.id.tilName)
        val etName = view.findViewById<TextInputEditText>(R.id.etName)
        val tilAddress = view.findViewById<TextInputLayout>(R.id.tilAddress)
        val etAddress = view.findViewById<TextInputEditText>(R.id.etAddress)
        val etTask = view.findViewById<TextInputEditText>(R.id.etTask)
        val etNewTag = view.findViewById<TextInputEditText>(R.id.etNewTag)
        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroupTags)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)

        val pointId = arguments?.getInt("pointId", -1) ?: -1
        (activity as? AppCompatActivity)?.supportActionBar?.title =
            if (pointId > 0) "Edit Point" else "Add Point"

        // Edit mode: load the point from Room and fill the form
        if (pointId > 0) {
            viewLifecycleOwner.lifecycleScope.launch {
                val item = repo.point(pointId).first() ?: return@launch
                val p = item.point
                editingId = p.id
                latitude = p.latitude
                longitude = p.longitude
                geocodedAddress = if (p.latitude != null) p.address else null

                etName.setText(p.name)
                etAddress.setText(p.address)
                etTask.setText(p.task)
                ratingBar.rating = p.rating.toFloat()
                chipGroup.removeAllViews()
                item.tagNames.forEach { addTagChip(chipGroup, it) }
            }
        }

        // Add a tag chip from the text box (FR2)
        view.findViewById<MaterialButton>(R.id.btnAddTag).setOnClickListener {
            val tag = etNewTag.text.toString().trim().lowercase()
            if (tag.isNotEmpty()) {
                addTagChip(chipGroup, tag)
                etNewTag.text?.clear()
            }
        }

        // Check the address with Android Geocoder and format it (FR13)
        view.findViewById<MaterialButton>(R.id.btnValidate).setOnClickListener {
            val typed = etAddress.text.toString().trim()
            if (typed.isEmpty()) {
                tilAddress.error = "Enter an address first"
                return@setOnClickListener
            }
            viewLifecycleOwner.lifecycleScope.launch {
                val result = geocode(requireContext().applicationContext, typed)
                if (result != null) {
                    val formatted = result.getAddressLine(0) ?: typed
                    etAddress.setText(formatted)
                    latitude = result.latitude
                    longitude = result.longitude
                    geocodedAddress = formatted
                    tilAddress.error = null
                    tilAddress.helperText = "Address found"
                } else {
                    tilAddress.error = "Address not found - check the spelling"
                }
            }
        }

        view.findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener {
            findNavController().popBackStack()
        }

        // Save to Room (insert or update) (FR1)
        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val address = etAddress.text.toString().trim()

            tilName.error = if (name.isEmpty()) "Name is required" else null
            tilAddress.error = if (address.isEmpty()) "Address is required" else null
            if (name.isEmpty() || address.isEmpty()) return@setOnClickListener

            btnSave.isEnabled = false   // stop double taps
            viewLifecycleOwner.lifecycleScope.launch {
                // Address changed since the last check -> look up lat/lng again
                if (geocodedAddress != address) {
                    val result = geocode(requireContext().applicationContext, address)
                    latitude = result?.latitude
                    longitude = result?.longitude
                }

                val point = PointOfInterest(
                    id = editingId,
                    name = name,
                    address = address,
                    latitude = latitude,
                    longitude = longitude,
                    task = etTask.text.toString().trim(),
                    rating = ratingBar.rating.toInt()
                )
                repo.savePoint(point, tagNames(chipGroup))

                Toast.makeText(requireContext(), "Point saved", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    // Geocoder does network work, so it runs on the IO thread.
    // Gives up after 5 seconds so Save never gets stuck.
    private suspend fun geocode(context: Context, text: String): Address? =
        withTimeoutOrNull(5000) {
            withContext(Dispatchers.IO) {
                if (!Geocoder.isPresent()) return@withContext null
                try {
                    @Suppress("DEPRECATION")
                    Geocoder(context).getFromLocationName(text, 1)?.firstOrNull()
                } catch (e: Exception) {
                    null
                }
            }
        }

    private fun tagNames(group: ChipGroup): List<String> =
        (0 until group.childCount).map { (group.getChildAt(it) as Chip).text.toString() }

    // Creates a chip with an X to remove it (no duplicates)
    private fun addTagChip(group: ChipGroup, text: String) {
        if (text in tagNames(group)) return
        val chip = Chip(requireContext()).apply {
            this.text = text
            isCloseIconVisible = true
            setOnCloseIconClickListener { group.removeView(this) }
        }
        group.addView(chip)
    }
}