package ca.gbc.comp3074.shaikh.hunttrail.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ca.gbc.comp3074.shaikh.hunttrail.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddEditPointFragment : Fragment(R.layout.fragment_add_edit_point) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tilName = view.findViewById<TextInputLayout>(R.id.tilName)
        val etName = view.findViewById<TextInputEditText>(R.id.etName)
        val etNewTag = view.findViewById<TextInputEditText>(R.id.etNewTag)
        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroupTags)

        // Add a tag chip from the text box (FR2: extensible tags)
        view.findViewById<MaterialButton>(R.id.btnAddTag).setOnClickListener {
            val tag = etNewTag.text.toString().trim().lowercase()
            if (tag.isNotEmpty()) {
                addTagChip(chipGroup, tag)
                etNewTag.text?.clear()
            }
        }

        view.findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener {
            findNavController().popBackStack()
        }

        view.findViewById<MaterialButton>(R.id.btnSave).setOnClickListener {
            if (etName.text.isNullOrBlank()) {
                tilName.error = "Name is required"
                return@setOnClickListener
            }
            tilName.error = null
            // Saving to Room comes in the database stage
            Toast.makeText(requireContext(), "Point saved", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    // Creates a chip with an X to remove it
    private fun addTagChip(group: ChipGroup, text: String) {
        // Don't add the same tag twice
        for (i in 0 until group.childCount) {
            if ((group.getChildAt(i) as Chip).text == text) return
        }
        val chip = Chip(requireContext()).apply {
            this.text = text
            isCloseIconVisible = true
            setOnCloseIconClickListener { group.removeView(this) }
        }
        group.addView(chip)
    }
}