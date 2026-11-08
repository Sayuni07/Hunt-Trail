package ca.gbc.comp3074.shaikh.hunttrail.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.gbc.comp3074.shaikh.hunttrail.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class HuntListFragment : Fragment(R.layout.fragment_hunt_list) {

    // Tapping a card opens Details and passes the point's id
    private val adapter = PointAdapter { point ->
        findNavController().navigate(
            R.id.action_huntList_to_details,
            bundleOf("pointId" to point.id)
        )
    }

    private var searchText = ""
    private var selectedTag: String? = null   // null = "All"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.rvPoints)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        // Search: filter every time the text changes (FR6)
        view.findViewById<TextInputEditText>(R.id.etSearch).doAfterTextChanged {
            searchText = it?.toString().orEmpty()
            applyFilter()
        }

        // Tag chips: filter by the selected tag
        view.findViewById<ChipGroup>(R.id.chipGroupTags)
            .setOnCheckedStateChangeListener { group, checkedIds ->
                val chipText = checkedIds.firstOrNull()?.let { id ->
                    group.findViewById<Chip>(id).text.toString()
                }
                selectedTag = if (chipText == "All") null else chipText
                applyFilter()
            }

        // + button opens Add/Edit
        view.findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            findNavController().navigate(R.id.action_huntList_to_addEdit)
        }

        applyFilter()
    }

    // Shows points that match the search text (name or tag) AND the selected chip
    private fun applyFilter() {
        val query = searchText.trim().lowercase()
        val result = SampleData.points.filter { p ->
            val matchesText = query.isEmpty() ||
                    p.name.lowercase().contains(query) ||
                    p.tags.any { it.contains(query) }
            val matchesTag = selectedTag == null || selectedTag in p.tags
            matchesText && matchesTag
        }
        adapter.submit(result)
    }
}