package ca.gbc.comp3074.shaikh.hunttrail.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import ca.gbc.comp3074.shaikh.hunttrail.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Wait 2 seconds, then open the Hunt List
        viewLifecycleOwner.lifecycleScope.launch {
            delay(2000)
            findNavController().navigate(R.id.action_splash_to_huntList)
        }
    }
}