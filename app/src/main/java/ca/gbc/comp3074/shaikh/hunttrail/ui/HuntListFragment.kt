package ca.gbc.comp3074.shaikh.hunttrail.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.gbc.comp3074.shaikh.hunttrail.R
import ca.gbc.comp3074.shaikh.hunttrail.data.HuntRepository
import ca.gbc.comp3074.shaikh.hunttrail.data.tagNames
import ca.gbc.comp3074.shaikh.hunttrail.data.PointWithTags
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class HuntListFragment : Fragment(R.layout.fragment_hunt_list) {

    private val repo by lazy { HuntRepository.get(requireContext()) }

    private lateinit var adapter: PointAdapter
    private lateinit var tvEmpty: TextView

    private var allPoints: List<PointWithTags> = emptyList()  // everything in the database
    private var searchText = ""
    private var selectedTag: String? = null                    // null = "All"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tapping a card opens Details and passes the point's id (FR5)
        adapter = PointAdapter { item ->
            findNavController().navigate(
                R.id.action_huntList_to_details,
                bundleOf("pointId" to item.point.id)
            )
        }

        val recycler = view.findViewById<RecyclerView>(R.id.rvPoints)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter
        tvEmpty = view.findViewById(R.id.tvEmpty)

        // Search by name or tag (FR6)
        view.findViewById<TextInputEditText>(R.id.etSearch).doAfterTextChanged {
            searchText = it?.toString().orEmpty()
            applyFilter()
        }

        // Filter by the selected tag chip
        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroupTags)
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val id = checkedIds.firstOrNull()
            selectedTag = if (id == null || id == R.id.chipAll) {
                null
            } else {
                group.findViewById<Chip>(id)?.text?.toString()
            }
            applyFilter()
        }

        // + button opens Add/Edit in "add" mode
        view.findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            findNavController().navigate(R.id.action_huntList_to_addEdit)
        }

        // Watch the database: the list and the chips update by themselves (Flow)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    repo.points.collect { points ->
                        allPoints = points
                        applyFilter()
                    }
                }
                launch {
                    repo.tags.collect { tags ->
                        buildTagChips(chipGroup, tags.map { it.name })
                    }
                }
            }
        }
    }

    // One filter chip per tag saved in the database (FR2: tags are extensible)
    private fun buildTagChips(group: ChipGroup, names: List<String>) {
        val keep = selectedTag
        while (group.childCount > 1) group.removeViewAt(1)   // index 0 is "All"

        var restored = false
        names.forEach { name ->
            val chip = layoutInflater.inflate(R.layout.chip_filter, group, false) as Chip
            chip.id = View.generateViewId()
            chip.text = name
            group.addView(chip)
            if (name == keep) {
                chip.isChecked = true
                restored = true
            }
        }
        if (!restored) group.check(R.id.chipAll)
    }

    // Show points that match the search text AND the selected chip
    private fun applyFilter() {
        val query = searchText.trim().lowercase()
        val result = allPoints.filter { item ->
            val tags = item.tagNames
            val matchesText = query.isEmpty() ||
                    item.point.name.lowercase().contains(query) ||
                    tags.any { it.contains(query) }
            val matchesTag = selectedTag == null || selectedTag in tags
            matchesText && matchesTag
        }
        adapter.submit(result)
        tvEmpty.visibility = if (result.isEmpty()) View.VISIBLE else View.GONE
    }
}